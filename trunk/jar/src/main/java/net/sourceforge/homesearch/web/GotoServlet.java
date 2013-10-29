package net.sourceforge.homesearch.web;

import com.vandale.IndexSet;
import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 28/10/13
 * Time: 19:52
 */
public class GotoServlet extends HttpServlet {
    static Set<String> PROCESSED_DICTIONARIES = new HashSet<>();
    static SortedMap<String, Map<String, Object[]>> INDEX = new TreeMap<>();


    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            String q = request.getParameter("q");
            String lemId = "";
            String bId = request.getParameter("betid");
            if (q.startsWith("_")) {
                lemId = q.split("_")[1].split("gwna")[1];
            }
            String from = request.getHeader("referer");
            String dicName = from.split(".zip")[0];
            dicName = dicName.substring(dicName.lastIndexOf("/") + 1);

            if (!PROCESSED_DICTIONARIES.contains(dicName)) {
                PROCESSED_DICTIONARIES.add(dicName);
                process(dicName);
            }

            if (!lemId.equals("") && dicName.equals("wn")) {
                for (String h : INDEX.keySet()) {
                    Map<String, Object[]> m = INDEX.get(h);
                    Object[] oos = m.get(dicName);
                    if (oos != null && oos.length == 2 && oos[1].toString().equals(lemId)) {
                        int realIndex = (Integer) oos[0];
                        int folder = realIndex / 1000 + 1;
                        String red = from.split(".zip")[0] + ".zip/" + folder + "/" + realIndex + ".xhtml";
                        if (bId != null && !bId.equals("")) {
                            red += "#" + bId;
                        }
                        response.sendRedirect(red);
                        return;
                    }
                }
            } else {//search by headword
                Map<String, Object[]> m = INDEX.get(q);
                if (m == null) {
                    m = INDEX.get(INDEX.tailMap(q).firstKey());
                }
                if (m != null) {
                    if (m.containsKey(dicName)) {
                        Object[] oos = m.get(dicName);
                        int realIndex = (Integer) oos[0];
                        int folder = realIndex / 1000 + 1;
                        String red = from.split(".zip")[0] + ".zip/" + folder + "/" + realIndex + ".xhtml";
                        if (bId != null && !bId.equals("")) {
                            red += "#" + bId;
                        }
                        response.sendRedirect(red);
                        return;
                    }
                }
            }


            response.sendRedirect(from);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void process(String dicName) throws Exception {
        String pathToIndex = "content/" + dicName + ".zip/" + dicName + ".ser";
        File entry = new TFile(pathToIndex);
        InputStream os = new TFileInputStream(entry);
        ObjectInputStream ois = new ObjectInputStream(os);
        List<IndexSet> l = (List<IndexSet>) ois.readObject();
        ois.close();
        os.close();

        for (IndexSet is : l) {
            String hw = is.headword;
            if (hw.contains("<")) hw = hw.split("<")[0];

            Map<String, Object[]> m = INDEX.get(hw);
            if (m == null) {
                m = new HashMap<>();
            }
            if (m.containsKey(dicName)) continue;
            m.put(dicName, new Object[]{is.realIndex, is.lemId});
            INDEX.put(hw, m);
        }
        System.out.println("New index size: "+INDEX.size() +"; bytes: "+getSize((Serializable) INDEX));
    }

    public static int getSize(Serializable ser) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(ser);
        oos.close();
        return baos.size();
    }
}
