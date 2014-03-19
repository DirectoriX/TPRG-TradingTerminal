/* 
 * Copyright (c) 2014, DirectoriX, kramer98489, UN-likE
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.aba.tradingterminal;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "TTerminal", urlPatterns = {"/TTerminal"})
public class TTerminal extends HttpServlet {

    public String DBName;
    public String URL;
    public String user;
    public String password;

    @Override
    public void init() throws ServletException {
        LoadDbConfig("DBprops.prop");
    }

    private void LoadDbConfig(String Path) {
        FileReader fr = null;
        try {
            fr = new FileReader(Path);
            try (BufferedReader inputData = new BufferedReader(fr)) {
                DBName = inputData.readLine();
                URL = inputData.readLine();
                user = inputData.readLine();
                password = inputData.readLine();
            }
            fr.close();
        } catch (IOException ex) {
            Logger.getLogger(TTerminal.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(TTerminal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void MakeHeader(PrintWriter out, String title, boolean error) {
        if (error) {
            title = "Ошибка";
        }

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + title + "</title>");

        if (error) {
            out.println("<meta http-equiv=\"refresh\" content=\"5; URL=.\"/>");
        } else {
            out.println("<style>");// Надо для более красивого отображения таблицы
            out.println(".tborder{");
            out.println("    border: black solid thin;");
            out.println("}");
            out.println("</style>");
        }

        out.println("</head>");
        out.println("<body>");

        if (error) {
            out.println("<h1>ERROR! You will be redirected to start page in 5 seconds</h1>");
        }
    }

    private void MakeFooter(PrintWriter out) {
        out.println("<br/><a href=\".\">Назад</a>");
        out.println("</body>");
        out.println("</html>");
    }

    private void NoConnection(PrintWriter out) {
        MakeHeader(out, "", true);
        out.println("<h2>Error: can't connect to database</h2>");
        MakeFooter(out);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");// Только UTF-8!

        String simstr;
        try {
            try (PrintWriter out = response.getWriter()) {
                if (request.getParameterNames().hasMoreElements()) {
                    if ((simstr = request.getParameter("simid")).length() > 0) {
                        SQLAgent DBA = new SQLAgent(DBName, URL, user, password);
                        if (DBA.TestConnect()) {
                            simstr = simstr.replaceAll("\\D", "");

                            if (simstr.length() == 0) {
                                MakeHeader(response.getWriter(), "", true);
                                return;
                            }

                            int simid = Integer.decode(simstr);

                            MakeHeader(out, "Просмотр статистики", false);

                            LinkedList<String> pinfo = DBA.GetResults(simid);

                            for (int i = 0; i < pinfo.size(); i++) {
                                out.println(pinfo.get(i));
                            }

                            MakeFooter(out);
                        } else {
                            NoConnection(out);
                        }
                    } else {
                        MakeHeader(out, "", true);
                    }
                } else {
                    MakeHeader(out, "", true);
                }
            }
        } finally {
            MakeFooter(response.getWriter());
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");// Только UTF-8!

        String action;
        try {
            try (PrintWriter out = response.getWriter()) {
                if ((action = request.getParameter("act")).length() > 0) {
                    switch (action.charAt(0)) {
                        case 'a': { // Add
                            break;
                        }
                        case 'e': { // Edit
                            break;
                        }
                        case 'd': { // Delete
                            break;
                        }
                        case 'l': { // Show products
                            SQLAgent DBA = new SQLAgent(DBName, URL, user, password);
                            if (DBA.TestConnect()) {
                                MakeHeader(out, "Список товаров", false);

                                LinkedList<String> list = DBA.ShowProductInfo();
                                for (int i = 0; i < list.size(); i++) {
                                    out.write(list.get(i));
                                }
                                list.clear();
                                MakeFooter(out);
                            } else {
                                NoConnection(out);
                            }
                            break;
                        }
                        case 's': { // Start!
                            break;
                        }
                        case 'p': { // Show progress
                            break;
                        }
                        default: { // Error - unrecognized symbol
                            MakeHeader(out, "", true);
                        }
                    }
                }
            }
        } finally {
            MakeFooter(response.getWriter());
        }
    }

}
