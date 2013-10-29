package net.sourceforge.homesearch.web;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 09/10/13
 * Time: 14:29
 */

import net.java.truevfs.access.*;
import net.java.truevfs.comp.zipdriver.ZipDriver;
import net.sourceforge.jtpl.Template;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class FileServlet extends HttpServlet {
    Template tpl;
    public static String CURRENT_DIC = "";
    List<String> textExtensions = Arrays.asList(new String[]{"js", "css", "xhtml"});

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");

            PrintWriter out = response.getWriter();
            String content = "";

            String webResourcePath = getServletContext().getRealPath(request.getRequestURI());
            String relativeContentPath = request.getRequestURI();
            relativeContentPath = relativeContentPath.startsWith("/") ? relativeContentPath.substring(1) : relativeContentPath;

            if (relativeContentPath.contains(".zip/")) {

                String dicName = relativeContentPath.split(".zip")[0];
                dicName = dicName.substring(dicName.lastIndexOf("/") + 1);
                if (!CURRENT_DIC.equals(dicName)) {
                    TVFS.umount();
                    FileServlet.CURRENT_DIC = dicName;
                }

                tpl = new Template(new File("content/template.xhtml"));
                String article = readFromArchive(relativeContentPath);
                tpl.assign("CONTENT", article);
                tpl.assign("TS", String.valueOf(System.currentTimeMillis()));
                tpl.parse("main");
                content = tpl.out();
            } else {
                File f = new File(webResourcePath);
                if (!f.exists()) {
                    f = new File(relativeContentPath);
                }
                FileInputStream fis = new FileInputStream(f);
                byte[] bbs = new byte[fis.available()];
                fis.read(bbs);
                fis.close();

                if (textExtensions.contains(f.getName().substring(f.getName().lastIndexOf(".") + 1))) {
                    content = new String(bbs, "utf-8");
                } else {
                    response.getOutputStream().write(bbs);
                    response.getOutputStream().close();
                    return;
                }

            }


            out.print(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            TVFS.umount();
        }
    }


    private String readFromArchive(String path) throws IOException {

        TConfig.current().setArchiveDetector(new TArchiveDetector("zip", new ZipDriver()));

        TFile entry = new TFile(path);
        Reader reader = new TFileReader(entry, Charset.forName("utf-8"));
        BufferedReader in = new BufferedReader(reader);
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        in.close();
        String art = sb.toString();
        art = art.replaceAll("http://goto", "../../goto");
        return art;
    }

    private Document makeDocument(String doc) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(doc));
            return builder.parse(is);
        } catch (Exception ex) {
            System.out.println("Article corrupted:" + doc);
            return null;
        }

    }

}