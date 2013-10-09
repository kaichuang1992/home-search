package net.sourceforge.homesearch.web;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 09/10/13
 * Time: 14:29
 */

import net.sourceforge.jtpl.Template;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class HelloServlet extends HttpServlet implements SingleThreadModel {
    Template tpl;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String s = getServletContext().getRealPath(request.getRequestURI());
        File f = new File(s);
        tpl = new Template(f);

        tpl.assign("message", "Vitaly");
        tpl.parse("main");
        String gen = tpl.out();


        try {
            out.print(gen);
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

}