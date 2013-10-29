package net.sourceforge.homesearch.web;

import com.vandale.IndexSet;
import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 28/10/13
 * Time: 19:52
 */
public class GotoServlet extends HttpServlet {
    static Map<String, List<IndexSet>> INDEX_SETS = new HashMap<>();


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

            List<IndexSet> l = INDEX_SETS.get(dicName);
            if (l == null) {
                String pathToIndex = "content/" + dicName + ".zip/" + dicName + ".ser";
                File entry = new TFile(pathToIndex);
                InputStream os = new TFileInputStream(entry);
                ObjectInputStream ois = new ObjectInputStream(os);
                l = (List<IndexSet>) ois.readObject();
                INDEX_SETS.put(dicName, l);
                ois.close();
                os.close();
            }


            for (IndexSet is : l) {
                String hw = is.headword;
                if (hw.indexOf("<") != -1) {
                    hw = hw.split("<")[0];
                }
                if (hw.equals(q) || (is.lemId!=null && is.lemId.equals(lemId))) {
                    int folder = is.realIndex / 1000 + 1;
                    String red = from.split(".zip")[0] + ".zip/" + folder + "/" + is.realIndex + ".xhtml";
                    if (bId != null && !bId.equals("")) {
                        red += "#" + bId;
                    }
                    response.sendRedirect(red);
                    return;
                }
            }
            response.sendRedirect(from);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
