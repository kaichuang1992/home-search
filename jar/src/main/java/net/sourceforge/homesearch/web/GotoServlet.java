package net.sourceforge.homesearch.web;

import com.vandale.IndexSet;
import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileInputStream;
import net.java.truevfs.access.TVFS;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;

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
    static DB DB;
//    static SortedMap<String, Map<String, Object[]>> INDEX = new TreeMap<>();


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


            if (!FileServlet.CURRENT_DIC.equals(dicName)) {
                TVFS.umount();
                FileServlet.CURRENT_DIC = dicName;
            }

            if (!PROCESSED_DICTIONARIES.contains(dicName)) {
                PROCESSED_DICTIONARIES.add(dicName);
                process(dicName);
            }

            if (!lemId.equals("") && dicName.equals("wn")) {

                DBIterator iterator = DB.iterator();

                for (iterator.seekToFirst(); iterator.hasNext(); iterator.next()) {
                    String h = Iq80DBFactory.asString(iterator.peekNext().getKey());
                    Map<String, Object[]> m = (Map<String, Object[]>) deserialize(iterator.peekNext().getValue());
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
                byte[] bbs = DB.get(Iq80DBFactory.bytes(q));

                Map<String, Object[]> m = null;
                if (bbs != null) {
                    m = (Map<String, Object[]>) deserialize(bbs);
                }
//                if (m == null) {
//                    m = INDEX.get(INDEX.tailMap(q).firstKey());
//                }
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

        if (DB == null) {
            Options options = new Options();
            options.createIfMissing(true);
            DB = Iq80DBFactory.factory.open(new File("index"), options);
        }


        // Use the db in here....
        for (IndexSet is : l) {
            String hw = is.headword;
            if (hw.contains("<")) hw = hw.split("<")[0];

            Map<String, Object[]> m = null;
            byte[] key = DB.get(Iq80DBFactory.bytes(hw));
            if (key != null) {
                m = (Map<String, Object[]>) deserialize(DB.get(Iq80DBFactory.bytes(hw)));
            }
            if (m == null) {
                m = new HashMap<>();
            }
            if (m.containsKey(dicName)) continue;
            m.put(dicName, new Object[]{is.realIndex, is.lemId});

            DB.put(Iq80DBFactory.bytes(hw), serialize((Serializable) m));
        }


        TVFS.umount();
//        System.out.println("New index size: " + INDEX.size() + "; bytes: " + getSize((Serializable) INDEX));
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
