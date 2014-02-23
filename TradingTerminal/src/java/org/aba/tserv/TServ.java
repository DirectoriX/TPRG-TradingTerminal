package org.aba.tserv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "TServ", urlPatterns = {"/TServ"})
public class TServ extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response, boolean isGET)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");// Только UTF-8!
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TServ</title>");
            out.println("<style>");// Надо для более красивого отображения таблицы
            out.println(".tborder{");
            out.println("    border: black solid thin;");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Метод - " + ((isGET) ? "Get" : "Post") + "</h1>");
            out.println("<hr/>");
            out.println("<table class=\"tborder\">");
            out.println("<tr>");
            out.println("<td class=\"tborder\">Имя параметра</td>");
            out.println("<td class=\"tborder\">Значение параметра</td>");
            out.println("</tr>");

            Enumeration<String> params = request.getParameterNames();
            while (params.hasMoreElements()) { // Просто выведем названия параметров и их значения
                String pName = params.nextElement();
                String pValue = request.getParameter(pName);
                out.println("<tr><td class=\"tborder\">" + pName + "</td><td class=\"tborder\">" + pValue + "</td></tr>");
            }

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, true);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response, false);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
