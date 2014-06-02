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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    private final LinkedList<Worker> workers = new LinkedList();

    private boolean IsConfigured;

    @Override
    public void init(){
        IsConfigured = SQLAgent.LoadSettings();
        SQLAgent.Connect();
    }

    private void MakeHeader(PrintWriter out, String title, boolean error) { // Функция, печатающая заголовок страницы
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

    private void MakeFooter(PrintWriter out) { // Функция, печатающая окончание страницы
        out.println("<br/><a href=\".\">Назад</a>");
        out.println("</body>");
        out.println("</html>");
    }

    private void NoConnection(PrintWriter out) { // Функция, печатающая сообщение об ошибке в случае невозможности установить соединение с базой данных
        SQLAgent.Disconnect();

        MakeHeader(out, "", true);
        out.println("<h2>Error: can't connect to database</h2>");
        MakeFooter(out);

        SQLAgent.Connect();
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
        request.setCharacterEncoding("UTF-8"); // Только UTF-8!

        String simstr;
        try {
            try (PrintWriter out = response.getWriter()) {
                if (request.getParameterNames().hasMoreElements()) { // Не просто обращение, а с параметром
                    if ((simstr = request.getParameter("simid")).length() > 0) { // Причём с нужным
                        if (SQLAgent.TestConnect()) {
                            simstr = simstr.replaceAll("\\D", ""); // Нам нужен только номер

                            if (simstr.length() == 0) { // Если не было циферок
                                MakeHeader(response.getWriter(), "", true);
                                return;
                            }

                            int simid = Integer.decode(simstr);

                            LinkedList<String> pinfo = SQLAgent.GetResults(simid);

                            if (pinfo.size() == 0) { // Симуляция ещё идёт

                                int percent = 0;

                                for (int i = 0; i < workers.size(); i++) {
                                    if (workers.get(i).getSimid() == simid) {
                                        percent = workers.get(i).getSteps() * 100 / 2880;
                                    }
                                }

                                out.println("<!DOCTYPE html>");
                                out.println("<html>");
                                out.println("<head>");
                                out.println("<title>Идёт работа</title>");
                                out.println("<meta http-equiv=\"refresh\" content=\"3; URL=./TTerminal?simid=" + simid + "\"/>");
                                out.println("</head>");
                                out.println("<body>");
                                out.println("<h2>Симуляция проводится в настоящий момент времени</h2>");

                                out.println("<h3>Уже прошло " + percent + "% симуляции</h3>");
                            } else {

                                MakeHeader(out, "Просмотр статистики", false);

                                for (int i = 0; i < pinfo.size(); i++) {
                                    out.println(pinfo.get(i)); // Добавляем все строки
                                }

                                MakeFooter(out);
                            }
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
        request.setCharacterEncoding("UTF-8"); // Только UTF-8!

        String action;
        try {
            try (PrintWriter out = response.getWriter()) {
                if ((action = request.getParameter("act")).length() > 0) { // Если есть такой параметр
                    switch (action.charAt(0)) {
                        case 'a': { // Add
                            try {
                                if (IsConfigured && request.getParameterNames().hasMoreElements()) {
                                    String codestr, namestr, countstr, countfrstr, pricestr, pricefrstr;
                                    // Получаем параметры
                                    boolean ispacked = ("on".equals(request.getParameter("ispacked")));
                                    codestr = request.getParameter("code");
                                    namestr = request.getParameter("name");
                                    countstr = request.getParameter("count");
                                    countfrstr = request.getParameter("countfr");
                                    pricestr = request.getParameter("price");
                                    pricefrstr = request.getParameter("pricefr");
                                    if (codestr.length() > 0 && namestr.length() > 0 && countstr.length() > 0 && countfrstr.length() > 0 && pricestr.length() > 0 && pricefrstr.length() > 0) {
                                        // Должны быть только числа!!.
                                        codestr = codestr.replaceAll("\\D", "");
                                        countstr = countstr.replaceAll("\\D", "");
                                        countfrstr = countfrstr.replaceAll("\\D", "");
                                        pricestr = pricestr.replaceAll("\\D", "");
                                        pricefrstr = pricefrstr.replaceAll("\\D", "");
                                        if (codestr.length() > 0 && namestr.length() > 0 && countstr.length() > 0 && countfrstr.length() > 0 && pricestr.length() > 0 && pricefrstr.length() > 0) {
                                            int code = Integer.decode(codestr);
                                            // Сразу считаем
                                            float count = (float) (Integer.decode(countstr) + ((Integer.decode(countfrstr) % 1000) / 1000.0));
                                            float price = (float) (Integer.decode(pricestr) + ((Integer.decode(pricefrstr) % 100) / 100.0));
                                            if (SQLAgent.TestConnect() && code >= 0 && count > 0 && price > 0) {
                                                Product tmp = new Product();
                                                tmp.setCode(code);
                                                tmp.setCount(count);
                                                tmp.setIsPacked(ispacked);
                                                tmp.setPrice(price);
                                                tmp.setName(namestr);

                                                MakeHeader(out, "Добавление товара", false);

                                                boolean added = SQLAgent.AddProduct(tmp);
                                                if (added) {
                                                    out.println("<h2>Товар добавлен</h2>");
                                                } else {
                                                    out.println("<h2>Товар не добавлен</h2>");
                                                    out.println("<h3>Скорее всего, товар с таким кодом уже существует</h3>");
                                                }

                                            } else {
                                                MakeHeader(out, "", true);
                                            }
                                        } else {
                                            MakeHeader(out, "", true);
                                        }
                                    } else {
                                        MakeHeader(out, "Ошибка", false);
                                        out.println("<h2>Запись не добавлена, т.к. хотя бы одно поле не заполнено</h2>");
                                    }
                                } else {
                                    MakeHeader(out, "", true);
                                }
                            } finally {
                                MakeFooter(out);
                            }
                            break;
                        }
                        case 'e': { // Edit
                            try {
                                if (IsConfigured && request.getParameterNames().hasMoreElements()) {
                                    String codestr, namestr, countstr, countfrstr, pricestr, pricefrstr;
                                    // Получаем параметры
                                    boolean ispacked = ("on".equals(request.getParameter("ispacked")));
                                    codestr = request.getParameter("code");
                                    namestr = request.getParameter("name");
                                    countstr = request.getParameter("count");
                                    countfrstr = request.getParameter("countfr");
                                    pricestr = request.getParameter("price");
                                    pricefrstr = request.getParameter("pricefr");

                                    // Только числовые параметры!!.
                                    codestr = codestr.replaceAll("\\D", "");
                                    countstr = countstr.replaceAll("\\D", "");
                                    countfrstr = countfrstr.replaceAll("\\D", "");
                                    pricestr = pricestr.replaceAll("\\D", "");
                                    pricefrstr = pricefrstr.replaceAll("\\D", "");
                                    Product tmp = new Product();

                                    tmp.setIsPacked(ispacked);

                                    // Не добавляем ненужных параметров
                                    if (codestr.length() > 0) {
                                        int code = Integer.decode(codestr);
                                        tmp.setCode(code);

                                        if (namestr.length() > 0) {
                                            tmp.setName(namestr);
                                        } else {
                                            tmp.setName("");
                                        }

                                        if (countstr.length() > 0 && countfrstr.length() > 0) {
                                            float count = (float) (Integer.decode(countstr) + ((Integer.decode(countfrstr) % 1000) / 1000.0));
                                            tmp.setCount((count > 0) ? count : -1);
                                        } else {
                                            tmp.setCount(-1);
                                        }

                                        if (pricestr.length() > 0 && pricefrstr.length() > 0) {
                                            float price = (float) (Integer.decode(pricestr) + ((Integer.decode(pricefrstr) % 100) / 100.0));
                                            tmp.setPrice((price > 0) ? price : -1);
                                        } else {
                                            tmp.setPrice(-1);
                                        }
                                        if (SQLAgent.TestConnect()) {
                                            MakeHeader(out, "Редактирование товара", false);

                                            boolean edited = SQLAgent.UpdateProduct(tmp);
                                            if (edited) {
                                                out.println("<h2>Товар изменён</h2>");
                                            } else {
                                                out.println("<h2>Товар не изменён</h2>");
                                                out.println("<h3>Скорее всего, товар с таким кодом не существует</h3>");
                                            }

                                        } else {
                                            NoConnection(out);
                                        }
                                    } else {
                                        MakeHeader(out, "", true);
                                    }
                                } else {
                                    MakeHeader(out, "", true);
                                }
                            } finally {
                                MakeFooter(out);
                            }
                            break;
                        }
                        case 'd': { // Delete
                            try {
                                if (IsConfigured && request.getParameterNames().hasMoreElements()) {

                                    String codestr;

                                    if ((codestr = request.getParameter("code")).length() > 0) {
                                        if (SQLAgent.TestConnect()) {
                                            // Вычленяем число
                                            codestr = codestr.replaceAll("\\D", "");

                                            if (codestr.length() == 0) {
                                                MakeHeader(response.getWriter(), "", true);
                                                return;
                                            }

                                            int code = Integer.decode(codestr);

                                            MakeHeader(out, "Удаление товара", false);

                                            boolean success = SQLAgent.DeleteProduct(code);

                                            out.println("<h2>Удаление товара с кодом " + code + ((success) ? " " : " не ") + "успешно</h2>");
                                            if (!success) {
                                                out.println("Возможно, неверный код товара или ошибка базы данных");
                                            }
                                        } else {
                                            NoConnection(out);
                                        }
                                    } else {
                                        MakeHeader(out, "", true);
                                    }
                                } else {
                                    MakeHeader(out, "", true);
                                }
                            } finally {
                                MakeFooter(response.getWriter());
                            }
                            break;
                        }
                        case 'l': { // Show products
                            if (IsConfigured && SQLAgent.TestConnect()) {
                                MakeHeader(out, "Список товаров", false);

                                LinkedList<String> list = SQLAgent.ShowProductInfo();
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
                        case 't': { // Telnov!
                            MakeHeader(out, "What does Telnov say?", false);
                            out.println("<h1>What does Telnov say?</h1>");
                            out.println("<h2>What does Telnov say?</h2>");
                            out.println("<h3>What does Telnov say?</h3>");
                            out.println("<h4>What does Telnov say?</h4>");
                            out.println("<h5>What does Telnov say?</h5>");
                            out.println("<h6>What does Telnov say?</h6>");
                            break;
                        }
                        case 's': { // Start
                            try {
                                // Сперва удалим завершённые симуляции из списка
                                for (int i = workers.size() - 1; i >= 0; i--) {
                                    if (workers.get(i).isReady()) {
                                        workers.set(i, null);
                                        workers.remove(i);
                                    }
                                }

                                if (workers.size() == 0) {
                                    SQLAgent.Load();
                                }

                                if (IsConfigured && request.getParameterNames().hasMoreElements()) {
                                    String peoplecountstr, goodscountstr;
                                    peoplecountstr = request.getParameter("peoplecount");
                                    goodscountstr = request.getParameter("goodscount");

                                    // Как такие числа могут содержать не цифры? Именно!
                                    peoplecountstr = peoplecountstr.replaceAll("\\D", "");
                                    goodscountstr = goodscountstr.replaceAll("\\D", "");

                                    if (peoplecountstr.length() > 0 && goodscountstr.length() > 0) {
                                        int peoplecount = Integer.decode(peoplecountstr);
                                        int goodscount = Integer.decode(goodscountstr);

                                        if (peoplecount >= 20 && goodscount >= 1) {

                                            workers.addLast(new Worker());
                                            workers.getLast().StartSim(peoplecount, goodscount);

                                            out.println("<!DOCTYPE html>");
                                            out.println("<html>");
                                            out.println("<head>");
                                            out.println("<title>Идёт работа</title>");
                                            out.println("<meta http-equiv=\"refresh\" content=\"5; URL=./TTerminal?simid=" + workers.getLast().getSimid() + "\"/>");
                                            out.println("</head>");
                                            out.println("<body>");
                                            out.println("<h1>Ждите 5 секунд...</h1>");
                                        } else {

                                            MakeHeader(out, "Ой...", false); // Плохие параметры
                                            out.println("Слишком маленькие значения! Покупателей надо не менее 20-и, а товаров - не менее одного");
                                        }
                                    } else {
                                        MakeHeader(out, "", true);
                                    }
                                } else {
                                    MakeHeader(out, "", true);
                                }
                            } finally {
                                MakeFooter(out);
                            }
                            break;
                        }

                        case 'c': { // Set DB settings
                            try {
                                if (!(IsConfigured = SQLAgent.LoadSettings()) && request.getParameterNames().hasMoreElements()) {
                                    String DBstr, URLstr, userstr, passwordstr, keystr;

                                    // Получаем параметры
                                    DBstr = request.getParameter("db");
                                    URLstr = request.getParameter("url");
                                    userstr = request.getParameter("user");
                                    passwordstr = request.getParameter("password");
                                    keystr = request.getParameter("key");

                                    // Чтоб не могли там всякие настроить раньше нас
                                    String key = "jyuwfu9xecf4im9rwvsagbzfvcuax14yqjl1pyvimvhpurmbaf";
                                    if (key.equals(keystr) && DBstr.length() > 0 && URLstr.length() > 0 && userstr.length() > 0) {
                                        try (FileWriter fw = new FileWriter("DBprops.prop")) {
                                            try (BufferedWriter output = new BufferedWriter(fw)) {
                                                output.write(DBstr);
                                                output.newLine();
                                                output.write(URLstr);
                                                output.newLine();
                                                output.write(userstr);
                                                output.newLine();
                                                output.write(passwordstr);
                                                output.newLine();

                                                output.flush();
                                            }
                                        } catch (IOException ex) {
                                            Logger.getLogger(SQLAgent.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        MakeHeader(out, "Добавление настроек", false);
                                        out.println("<h3>Готово!</h3>");
                                    } else {
                                        MakeHeader(out, "Ошибка", false);
                                        out.println("<h2>Нет какого-либо параметра</h2>");
                                    }
                                } else {
                                    MakeHeader(out, "Ошибка", false);
                                    out.println("<h2>Настройки уже загружены</h2>");
                                }
                            } finally {
                                MakeFooter(out);
                            }
                            break;
                        }

                        case 'f': { // Если уж вдруг что-то зависло...
                            try {
                                if (IsConfigured) { // Если false, то симуляция не могла быть запущена!                                    
                                    for (int i = 0; i < workers.size(); i++) {
                                        workers.set(i, null);
                                    }

                                    workers.clear();

                                    SQLAgent.Fix();

                                    MakeHeader(out, "ОК", false);
                                    out.println("<h2>OK</h2>");

                                } else {
                                    NoConnection(out);
                                }
                            } finally {
                                MakeFooter(out);
                            }
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
