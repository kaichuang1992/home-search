package net.sourceforge.homesearch.web;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 09/10/13
 * Time: 14:29
 */

import net.java.truevfs.access.TFile;
import net.java.truevfs.access.TFileReader;
import net.sourceforge.homesearch.model.DictionaryDescriptor;
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

public class FileServlet extends HttpServlet {
    Template tpl;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String content = "";

        String webResourcePath = getServletContext().getRealPath(request.getRequestURI());
        String relativeContentPath = request.getRequestURI();
        relativeContentPath = relativeContentPath.startsWith("/") ? relativeContentPath.substring(1) : relativeContentPath;

        File entry = new TFile(relativeContentPath);
        if (entry.exists()) {
            DictionaryDescriptor dd = getDescriptor(getPathToDescriptor(relativeContentPath));
            content += dd.header;
            content += readFromArchive(relativeContentPath);
            content += dd.footer;
        } else {
            File f = new File(webResourcePath);
            tpl = new Template(f);
            tpl.assign("message", "Vitaly");
            tpl.parse("main");
            content = tpl.out();
        }


        try {
            out.print(content);
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

    private String getPathToDescriptor(String relativeContentPath) {
        String[] parts = relativeContentPath.split("/");
        StringBuffer sb = new StringBuffer();
        for (String part : parts) {
            if (part.endsWith(".zip")) {
                sb.append(part);
                sb.append("/");
                sb.append("descriptor.xml");
                break;
            } else {
                sb.append(part);
                sb.append("/");
            }
        }
        return sb.toString();
    }

    private DictionaryDescriptor getDescriptor(String pathToDescriptor) throws IOException {
        DictionaryDescriptor dd = new DictionaryDescriptor();
        String descriptorContent = readFromArchive(pathToDescriptor);
        Document descriptor = makeDocument(descriptorContent);
        String header = descriptor.getElementsByTagName("header").item(0).getFirstChild().getNodeValue();
        String footer = descriptor.getElementsByTagName("footer").item(0).getFirstChild().getNodeValue();
        dd.header = header;
        dd.footer = footer;
        return dd;
    }

    private String readFromArchive(String path) throws IOException {
        File entry = new TFile(path);
        Reader reader = new TFileReader(entry);
        BufferedReader in = new BufferedReader(reader);
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        in.close();
        return sb.toString();
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