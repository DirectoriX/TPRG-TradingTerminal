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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLAgent {

    private static String DBName;
    private static String URL;
    private static String user;
    private static String password;

    private static Connection conn;

    private static void HandleEx(SQLException ex) {
        // handle any errors
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    public SQLAgent() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance(); // Пытаемся загрузить драйвер
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(SQLAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        LoadSettings();
    }

    private static void LoadSettings() {
        try (FileReader fr = new FileReader("DBprops.prop")) {
            try (BufferedReader inputData = new BufferedReader(fr)) {
                DBName = inputData.readLine();
                URL = inputData.readLine();
                user = inputData.readLine();
                password = inputData.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(SQLAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void Connect() { // Подключение к БД
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + DBName + "?"
                    + "user=" + user + "&password=" + password);
        } catch (SQLException ex) {
            HandleEx(ex);

            conn = null;
        }
    }

    private static void Disconnect() { // Отключение от БД
        try {
            conn.close();
        } catch (SQLException ex) {
            HandleEx(ex);
        }
    }

    public static boolean TestConnect() {

        Connect();
        boolean res = !(conn == null);

        if (res) {
            Disconnect();
        }

        return res;
    }

    public static int Started(int peoplecount, int goodscount) { // Создаём новую запись о симуляции
        Connect();
        int ret = 0;
        try {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO simulations() VALUES (0, ?, ?, 0, 0, 0, 0, 0, 0, 0, 0)")) {
                st.setInt(1, peoplecount);
                st.setInt(2, goodscount);
                // Другие параметры не знаем, поэтому пока ставим нули
                st.execute();
                try (ResultSet res = st.executeQuery("SELECT id FROM simulations ORDER BY id DESC LIMIT 1")) { // Получаем номер симуляции
                    res.next();
                    ret = res.getInt(1);
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();

        return ret;

    }

    public static void Ended(int peoplearrived, int peopleserved, float avggoodscount, float avgprofit, int profit, int maxqueue, int maxqueuetime, boolean iscorrect, int simid) {
        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement("UPDATE simulations SET peoplearrived = ?, peopleserved = ?, avggoodscount = ?, avgprofit = ?, profit = ?, maxqueue = ?, maxqueuetime = ?, iscorrect = ? WHERE id = ?")) {
                st.setInt(1, peoplearrived);
                st.setInt(2, peopleserved);
                st.setFloat(3, avggoodscount);
                st.setFloat(4, avgprofit);
                st.setInt(5, profit);
                st.setInt(6, maxqueue);
                st.setTime(7, new Time(maxqueuetime * 10 / (60 * 60), maxqueuetime * 10 % (60 * 60) / 60, maxqueuetime * 10 % 60));
                st.setBoolean(8, iscorrect);
                st.setInt(9, simid);

                st.execute(); // Обновляем запись о симуляции до актуальных значений
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();
    }

    public static void Buyed(int buyer, int code, float count, int time, int cost, int simid) { // Создание записи об элементарной покупке
        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO reports() VALUES (0, ?, ?, ?, ?, ?, ?)")) {
                st.setInt(1, simid);
                st.setInt(2, buyer);
                st.setInt(3, code);
                st.setFloat(4, count);

                Time t = new Time(time * 10 / (60 * 60), time * 10 % (60 * 60) / 60, time * 10 % 60);
                st.setTime(5, t);

                st.setInt(6, cost);

                st.execute();
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();
    }

    public static LinkedList<String> ShowProductInfo() {
        LinkedList<String> result = new LinkedList();

        String request = "SELECT p.code, p.name, p.ispacked, p.count, p.cost FROM products p ORDER BY p.name";

        Connect();
        try {
            try (Statement st = conn.createStatement(); ResultSet res = st.executeQuery(request)) {

                result.add("<h2>Список товаров</h2>");
                result.add("<table class=\"tborder\">");
                result.add("<tr><td class=\"tborder\">Название</td><td class=\"tborder\">Код</td><td class=\"tborder\">Упакован?</td><td class=\"tborder\">Среднее приобретаемое количество</td><td class=\"tborder\">Стоимость</td></tr>");
                result.add("<tr><td colspan=\"6\" style=\" background-color: #ffffff; height: 5px\"></td></tr>");

                String tmp;

                while (res.next()) {
                    tmp = "<tr><td class=\"tborder\">";
                    tmp += res.getString("name") + "</td><td class=\"tborder\">";
                    tmp += Integer.toString(res.getInt("code")) + "</td><td class=\"tborder\">";
                    tmp += ((res.getBoolean("ispacked")) ? "+" : "-") + "</td><td class=\"tborder\">";
                    tmp += Float.toString(res.getFloat("count")) + "</td><td class=\"tborder\">";
                    tmp += Float.toString(res.getFloat("cost")) + "</td></tr>";

                    result.add(tmp);
                }
                result.add("</table>");
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();

        return result;
    }

    public static LinkedList<Product> GetProductInfo() { // Получаем полную информацию о всех товарах
        LinkedList<Product> result = new LinkedList();

        String request = "SELECT p.code, p.name, p.ispacked, p.count, p.cost FROM products p ORDER BY p.name";

        Connect();
        try {
            try (Statement st = conn.createStatement(); ResultSet res = st.executeQuery(request)) {

                while (res.next()) { // Обрабатываем все записи...
                    Product tmp = new Product();

                    tmp.Code = res.getInt("code");
                    tmp.Name = res.getString("name");
                    tmp.IsPacked = res.getBoolean("ispacked");
                    tmp.Count = res.getFloat("count");
                    tmp.Price = res.getFloat("cost");

                    result.add(tmp);

                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();

        return result;
    }

    public static LinkedList<String> GetResults(int simulationid) { // Получаем подробный отчёт о симуляции
        LinkedList<String> result = new LinkedList();

        if (simulationid < 1) {
            result.add("<h2>Invalid sumulation id</h2>");
            return result;
        }

        result.add("<table class=\"tborder\">");
        result.add("<tr><td class=\"tborder\">Номер покупателя</td><td class=\"tborder\">Время</td><td class=\"tborder\">Код товара</td><td class=\"tborder\">Название товара</td><td class=\"tborder\">Количество товара</td><td class=\"tborder\">Стоимость</td></tr>");
        result.add("<tr><td colspan=\"6\" style=\" background-color: #ffffff; height: 5px\"></td></tr>");

        String request = "SELECT r.buyerid, r.time, r.productcode, r.name, r.count, r.cost FROM reports r WHERE simulationid = ? ORDER BY r.time";

        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, simulationid);
                try (ResultSet res = st.executeQuery()) {
                    String tmp;
                    int buyerid = 1;
                    int goodscount = 0;
                    int profit = 0;
                    boolean empty = true;

                    while (res.next()) {
                        empty = false;

                        tmp = "<tr><td class=\"tborder\">";
                        int q = res.getInt("buyerid");

                        if (q != buyerid) { // Если новый покупатель - добавим сводку и строку-разделитель
                            result.add("<tr><td colspan=\"4\"></td><td class=\"tborder\">Видов товара: " + goodscount + "</td><td class=\"tborder\">Выручка: " + profit + "</td>");
                            result.add("<tr><td colspan=\"6\" style=\" background-color: #ffffff; height: 5px\"></td></tr>");
                            buyerid = q;
                            goodscount = profit = 0;
                        }

                        goodscount++;

                        tmp += Integer.toString(q) + "</td><td class=\"tborder\">";
                        tmp += res.getTime("time") + "</td><td class=\"tborder\">";
                        tmp += Integer.toString(res.getInt("productcode")) + "</td><td class=\"tborder\">";
                        tmp += res.getString("name") + "</td><td class=\"tborder\">";

                        float cnt = res.getFloat("count");

                        if (cnt < 0) {
                            tmp += Integer.toString((int) -cnt) + "</td><td class=\"tborder\">";
                        } else {
                            tmp += Float.toString(cnt) + "</td><td class=\"tborder\">";
                        }

                        int cost = res.getInt("cost");
                        profit += cost;

                        tmp += Integer.toString(cost) + "</td></tr>";

                        result.add(tmp);

                        if (res.isLast()) {
                            result.add("<tr><td colspan=\"4\"></td><td class=\"tborder\">Видов товара: " + goodscount + "</td><td class=\"tborder\">Выручка: " + profit + "</td>");
                        }
                    }

                    if (!empty) {
                        request = "SELECT * FROM simulations WHERE id = ?";
                        try {
                            try (PreparedStatement sim = conn.prepareStatement(request)) {
                                sim.setInt(1, simulationid);
                                try (ResultSet simres = sim.executeQuery()) {
                                    simres.next();
                                    result.addFirst("<h3>Подробный отчёт</h3>");
                                    result.addFirst("</table>");
                                    result.addFirst("<tr><td class=\"tborder\">Корректно?</td><td class=\"tborder\">" + ((simres.getBoolean("iscorrect")) ? "Да" : "Нет") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Момент времени, когда была очередь максимальной длины</td><td class=\"tborder\">" + simres.getTime("maxqueuetime") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Максимальная длина очереди</td><td class=\"tborder\">" + simres.getInt("maxqueue") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Общая выручка</td><td class=\"tborder\">" + simres.getInt("profit") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Средняя выручка с одного покупателя</td><td class=\"tborder\">" + simres.getFloat("avgprofit") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Среднее количество купленных товаров</td><td class=\"tborder\">" + simres.getFloat("avggoodscount") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Людей обслужено</td><td class=\"tborder\">" + simres.getInt("peopleserved") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Людей пришло</td><td class=\"tborder\">" + simres.getInt("peoplearrived") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Заданное среднее количство товаров</td><td class=\"tborder\">" + simres.getInt("goodscount") + "</td></tr>");
                                    result.addFirst("<tr><td class=\"tborder\">Заданное количество человек</td><td class=\"tborder\">" + simres.getInt("peoplecount") + "</td></tr>");
                                    result.addFirst("<table class=\"tborder\">");
                                    result.addFirst("<h3>Общая сводка</h3>");
                                    result.addFirst("<h2>Отчёт о симуляции №" + simulationid + "</h2>");
                                    result.add("</table>");
                                }
                            }
                        } catch (SQLException ex) {
                            HandleEx(ex);
                        }
                    } else {
                        result.clear();
                        result.add("<h2>Invalid sumulation id</h2>");
                    }

                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();

        return result;
    }

    public static boolean DeleteProduct(int code) {
        boolean result = false;

        if (code < 0) {
            return result;
        }

        boolean s = false;

        String request = "SELECT COUNT FROM products WHERE code = ?";

        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, code);
                try (ResultSet res = st.executeQuery()) {
                    s = (res.next());
                } catch (SQLException ex) {
                    HandleEx(ex);
                }

                if (s) {
                    Statement st_del = conn.createStatement();
                    st_del.execute("DELETE FROM products WHERE code = " + code);

                    try (ResultSet res = st.executeQuery()) {
                        result = !(res.next());
                    } catch (SQLException ex) {
                        HandleEx(ex);
                    }
                }

                Disconnect();
            }

        } catch (SQLException ex) {
            HandleEx(ex);
        }
        return result;
    }

    public static boolean AddProduct(Product item) {
        boolean result = false;

        if (item.Code < 0 || item.Count <= 0 || item.Price <= 0) {
            return result;
        }

        boolean s = false;

        String request = "SELECT COUNT FROM products WHERE code = ?";

        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, item.Code);
                try (ResultSet res = st.executeQuery()) {
                    s = (res.next());
                } catch (SQLException ex) {
                    HandleEx(ex);
                }

                if (!s) {
                    try (PreparedStatement st_add = conn.prepareStatement("INSERT INTO products() VALUES (0, ?, ?, ?, ?, ?)")) {

                        st_add.setInt(1, item.Code);
                        st_add.setString(2, item.Name);
                        st_add.setBoolean(3, item.IsPacked);
                        st_add.setFloat(4, item.Count);
                        st_add.setFloat(5, item.Price);

                        st_add.execute();

                        try (ResultSet res = st.executeQuery()) {
                            result = (res.next());
                        } catch (SQLException ex) {
                            HandleEx(ex);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        return result;
    }

    public static boolean UpdateProduct(Product item) {
        boolean result = false;

        boolean s = false;

        String request = "SELECT COUNT FROM products WHERE code = ?";

        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, item.Code);
                try (ResultSet res = st.executeQuery()) {
                    s = (res.next());
                } catch (SQLException ex) {
                    HandleEx(ex);
                }

                if (s) {
                    request = "UPDATE products SET ispacked = " + ((item.IsPacked) ? 1 : 0);

                    if (item.Count > 0) {
                        request += ", count = " + item.Count;
                    }

                    if (item.Name.length() > 0) {
                        request += ", name = '" + item.Name + "'";
                    }

                    if (item.Price > 0) {
                        request += ", cost = " + item.Price;
                    }

                    request += " WHERE code = " + item.Code;

                    result = !conn.createStatement().execute(request);
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        return result;
    }
}
