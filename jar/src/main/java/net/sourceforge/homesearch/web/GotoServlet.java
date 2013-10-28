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
import java.util.List;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 28/10/13
 * Time: 19:52
 */
public class GotoServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            String q = request.getParameter("q");
            String from = request.getHeader("referer");
            String dicName = from.split(".zip")[0];
            dicName = dicName.substring(dicName.lastIndexOf("/") + 1);
            String pathToIndex = "content/" + dicName + ".zip/" + dicName + ".ser";
            File entry = new TFile(pathToIndex);
            InputStream os = new TFileInputStream(entry);
            ObjectInputStream ois = new ObjectInputStream(os);
            List<IndexSet> l = (List<IndexSet>) ois.readObject();
            ois.close();
            os.close();

            for (IndexSet is : l) {
                String hw = is.headword;
                if (hw.indexOf("<") != -1) {
                    hw = hw.split("<")[0];
                }
                if (hw.equals(q)) {
                    int folder = is.realIndex / 1000 + 1;
                    String red = from.split(".zip")[0] + ".zip/" + folder + "/" + is.realIndex + ".xhtml";
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
