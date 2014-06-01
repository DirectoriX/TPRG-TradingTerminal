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

    private static String DBName; // Имя базы данных
    private static String URL; // Сетевой адрес базы данных
    private static String user; // Имя пользователя
    private static String password; // Пароль

    private static final LinkedList<Product> RangeofGoods = new LinkedList<>();

    private static Connection conn; // Подключение к базе данных

    private static void HandleEx(SQLException ex) { // Функция обработки исключений SQL
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    public static boolean LoadSettings() { // Загружает настройки подключения к БД
        boolean result = false; // Чтобменьше проверок делать

        try { // Пытаемся загрузить драйвер, ошибок быть не должно
            Class.forName("com.mysql.jdbc.Driver").newInstance(); // Пытаемся загрузить драйвер
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(SQLAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Пытаемся прочитать настройки подключения к БД из файла
        try (FileReader fr = new FileReader("DBprops.prop")) {
            try (BufferedReader inputData = new BufferedReader(fr)) {
                DBName = inputData.readLine();
                URL = inputData.readLine();
                user = inputData.readLine();
                password = inputData.readLine();
                result = (DBName != null && URL != null && user != null && password != null);
            }
        } catch (IOException ex) {
            Logger.getLogger(SQLAgent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;

    }

    public static void Connect() { // Функция, создающая подключение к БД
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + DBName + "?" + "user=" + user + "&password=" + password);
        } catch (SQLException ex) {
            HandleEx(ex);

            conn = null;
        }
    }

    public static void Disconnect() { // Функция, закрывающая подключение к БД
        try {
            conn.close();
        } catch (SQLException ex) {
            HandleEx(ex);
        }
    }

    public static boolean TestConnect() { // Функция, проверяющая правильность параметров подключения к БД

        boolean res = false;
        try {
            res = !conn.isClosed();
        } catch (SQLException ex) {
            Logger.getLogger(SQLAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static int Started(int peoplecount, int goodscount) { // Функция, создающая новую запись о симуляции
        int ret = 0;
        try {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO simulations() VALUES (0, ?, ?, 0, 0, 0, 0, 0, 0, 0, 0)")) {
                st.setInt(1, peoplecount);
                st.setInt(2, goodscount);
                // Другие параметры не знаем, поэтому пока ставим нули
                st.execute();
                try (ResultSet res = st.executeQuery("SELECT id FROM simulations ORDER BY id DESC LIMIT 1")) { // Получаем номер симуляции
                    res.next();
                    ret = res.getInt(1); // Получаем id симуляции
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        return ret;

    }

    public static void Ended(int peoplearrived, int peopleserved, float avggoodscount, float avgprofit, int profit, int maxqueue, int maxqueuetime, int simid) { // Функция, обновляющая запись о симуляции
        try {
            try (PreparedStatement st = conn.prepareStatement("UPDATE simulations SET peoplearrived = ?, peopleserved = ?, avggoodscount = ?, avgprofit = ?, profit = ?, maxqueue = ?, maxqueuetime = ?, iscorrect = true WHERE id = ?")) {
                st.setInt(1, peoplearrived);
                st.setInt(2, peopleserved);
                st.setFloat(3, avggoodscount);
                st.setFloat(4, avgprofit);
                st.setInt(5, profit);
                st.setInt(6, maxqueue);
                st.setTime(7, new Time((maxqueuetime * 10 / (60 * 60)) + 9, maxqueuetime * 10 % (60 * 60) / 60, maxqueuetime * 10 % 60));
                st.setInt(8, simid);

                st.execute(); // Обновляем запись о симуляции до актуальных значений
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

    }

    public static int Buyed(int buyerid, int time, int simid, float[] cart, boolean discount) { // Функция, записывающая информацию о покупках одного покупателя
        int total = 0;
        try {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO reports() VALUES (0, ?, ?, ?, ?, ?, ?, ?)")) {
                // Записываем общие для всех товаров параметры
                st.setInt(1, simid);
                st.setInt(2, buyerid);
                st.setTime(6, new Time((time * 10 / (60 * 60)) + 9, time * 10 % (60 * 60) / 60, time * 10 % 60));

                Product good;

                float count;
                // Перебираем всю корзину и пишем в БД информацию
                for (int i = 0, n = cart.length; i < n; i++) {
                    count = cart[i];
                    if (count != 0) {

                        good = RangeofGoods.get(i);

                        st.setInt(3, good.getCode());
                        st.setString(4, good.getName());
                        st.setFloat(5, count);

                        int sum = 0;
                        for (int j = 0; j < n; j++) {
                            sum += count * RangeofGoods.get(i).getPrice();
                        }
                        total = (int) (sum * ((discount) ? 0.95 : 1));

                        st.setInt(7, (int) (count * RangeofGoods.get(i).getPrice() * ((discount) ? -1 : 1)));

                        st.execute();
                    }
                }

            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        return total;
    }

    public static LinkedList<String> ShowProductInfo() { // Функция, возвращающая полную информацию об ассортименте магазина в удобном для пользователя виде
        LinkedList<String> result = new LinkedList();

        String request = "SELECT p.code, p.name, p.ispacked, p.count, p.cost FROM products p ORDER BY p.name";

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

        return result;
    }

    public static void Load() { // Функция, возвращающая полную информацию об ассортименте магазина
        for (int i = 0; i < RangeofGoods.size(); i++) {
            RangeofGoods.set(i, null);
        }
        RangeofGoods.clear();

        String request = "SELECT p.code, p.name, p.ispacked, p.count, p.cost FROM products p ORDER BY p.name";

        try {
            try (Statement st = conn.createStatement(); ResultSet res = st.executeQuery(request)) {

                while (res.next()) { // Обрабатываем все записи...
                    Product tmp = new Product();

                    tmp.setCode(res.getInt("code"));
                    tmp.setName(res.getString("name"));
                    tmp.setIsPacked(res.getBoolean("ispacked"));
                    tmp.setCount(res.getFloat("count"));
                    tmp.setPrice(res.getFloat("cost"));

                    RangeofGoods.add(tmp);

                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }
    }

    public static LinkedList<String> GetResults(int simulationid) { // Функция, возвращающая подробную информацию о симуляции
        LinkedList<String> result = new LinkedList();
        boolean empty = true;

        if (simulationid < 1) { // Некорректный номер
            result.add("<h2>Invalid sumulation id</h2>");
            return result;
        }

        String request = "SELECT * FROM simulations WHERE id = ?";
        try {
            try (PreparedStatement sim = conn.prepareStatement(request)) {
                sim.setInt(1, simulationid);
                try (ResultSet simres = sim.executeQuery()) {
                    if (simres.next()) {
                        // Если симуляция не завершена...
                        if (simres.getInt("maxqueue") == 0) {
                            result.clear(); // ...вернуть пустой список
                            return result;
                        }

                        result.add("<h2>Отчёт о симуляции №" + simulationid + "</h2>");
                        result.add("<h3>Общая сводка</h3>");
                        result.add("<table class=\"tborder\">");
                        result.add("<tr><td class=\"tborder\">Заданное количество человек</td><td class=\"tborder\">" + simres.getInt("peoplecount") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Заданное среднее количство товаров</td><td class=\"tborder\">" + simres.getInt("goodscount") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Людей пришло</td><td class=\"tborder\">" + simres.getInt("peoplearrived") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Людей обслужено</td><td class=\"tborder\">" + simres.getInt("peopleserved") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Среднее количество купленных товаров</td><td class=\"tborder\">" + simres.getFloat("avggoodscount") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Средняя выручка с одного покупателя</td><td class=\"tborder\">" + simres.getFloat("avgprofit") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Общая выручка</td><td class=\"tborder\">" + simres.getInt("profit") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Максимальная длина очереди</td><td class=\"tborder\">" + simres.getInt("maxqueue") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Момент времени, когда была очередь максимальной длины</td><td class=\"tborder\">" + simres.getTime("maxqueuetime") + "</td></tr>");
                        result.add("<tr><td class=\"tborder\">Корректно?</td><td class=\"tborder\">" + ((simres.getBoolean("iscorrect")) ? "Да" : "Нет") + "</td></tr>");
                        result.add("</table>");
                        result.add("<h3>Подробный отчёт</h3>");
                        empty = false;
                    }
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        if (!empty) { // Если что-то куплено, то...
            result.add("<table class=\"tborder\">");
            result.add("<tr><td class=\"tborder\">Номер покупателя</td><td class=\"tborder\">Время</td><td class=\"tborder\">Код товара</td><td class=\"tborder\">Название товара</td><td class=\"tborder\">Количество товара</td><td class=\"tborder\">Стоимость</td></tr>");
            result.add("<tr><td colspan=\"6\" style=\" background-color: #ffffff; height: 5px\"></td></tr>");

            request = "SELECT r.buyerid, r.time, r.productcode, r.name, r.count, r.cost FROM reports r WHERE simulationid = ? ORDER BY r.time";

            try {
                try (PreparedStatement st = conn.prepareStatement(request)) {
                    st.setInt(1, simulationid);
                    try (ResultSet res = st.executeQuery()) {
                        String tmp;
                        int buyerid = 1;
                        int goodscount = 0;
                        int profit = 0;

                        while (res.next()) {
                            tmp = "<tr><td class=\"tborder\">";
                            int q = res.getInt("buyerid");

                            if (q != buyerid) { // Если новый покупатель - добавим сводку и строку-разделитель
                                result.add("<tr><td colspan=\"4\"></td><td class=\"tborder\">Видов товара: " + goodscount + "</td><td class=\"tborder\">Выручка: " + ((profit < 0) ? ("*" + (int) (profit * -0.95)) : profit) + "</td>");
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

                            tmp += Integer.toString((cost < 0) ? -cost : cost) + "</td></tr>";

                            result.add(tmp);

                            if (res.isLast()) {
                                result.add("<tr><td colspan=\"4\"></td><td class=\"tborder\">Видов товара: " + goodscount + "</td><td class=\"tborder\">Выручка: " + ((profit < 0) ? ("*" + (int) (profit * -0.95)) : profit) + "</td>");
                                result.add("</table>");
                            }
                        }
                    }

                }

            } catch (SQLException ex) {
                HandleEx(ex);
            }

        } else {
            result.add("<h3>Неверный номер симуляции</h3>");
        }

        return result;
    }

    public static boolean DeleteProduct(int code) { // Функция, удаляющая запись о товаре из базы данных
        boolean result = false;

        if (code < 0) { // Тут ошибка
            return result;
        }

        boolean s = false;

        String request = "SELECT COUNT FROM products WHERE code = ?";
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

                    try (ResultSet res = st.executeQuery()) { // Должно быть 0 записей
                        result = !(res.next());
                    } catch (SQLException ex) {
                        HandleEx(ex);
                    }
                }
            }

        } catch (SQLException ex) {
            HandleEx(ex);
        }
        return result;
    }

    public static boolean AddProduct(Product item) { // Функция, добавляющая информацию о новом продукте
        boolean result = false;

        if (item.getCode() < 0 || item.getCount() <= 0 || item.getPrice() <= 0) { // Не надо так...
            return result;
        }

        boolean s = false;

        String request = "SELECT COUNT FROM products WHERE code = ?";

        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, item.getCode());
                try (ResultSet res = st.executeQuery()) {
                    s = (res.next()); // Смотрим, есть ли такой товар
                } catch (SQLException ex) {
                    HandleEx(ex);
                }

                if (!s) {
                    try (PreparedStatement st_add = conn.prepareStatement("INSERT INTO products() VALUES (0, ?, ?, ?, ?, ?)")) {

                        st_add.setInt(1, item.getCode());
                        st_add.setString(2, item.getName());
                        st_add.setBoolean(3, item.IsPacked());
                        st_add.setFloat(4, item.getCount());
                        st_add.setFloat(5, item.getPrice());

                        st_add.execute();

                        try (ResultSet res = st.executeQuery()) { // Проверяем, успешно ли прошло добавление
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

    public static boolean UpdateProduct(Product item) { // Функция, обновляющая информацию о продукте
        boolean result = false;

        boolean s = false;

        String request = "SELECT COUNT FROM products WHERE code = ?";

        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, item.getCode());
                try (ResultSet res = st.executeQuery()) {
                    s = (res.next()); // Проверяем, есть ли такой товар
                } catch (SQLException ex) {
                    HandleEx(ex);
                }

                if (s) {
                    request = "UPDATE products SET ispacked = " + ((item.IsPacked()) ? 1 : 0);

                    // Пишем только непустые изменения
                    if (item.getCount() > 0) {
                        request += ", count = " + item.getCount();
                    }

                    if (item.getName().length() > 0) {
                        request += ", name = '" + item.getName() + "'";
                    }

                    if (item.getPrice() > 0) {
                        request += ", cost = " + item.getPrice();
                    }

                    request += " WHERE code = " + item.getCode();

                    result = !conn.createStatement().execute(request);
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        return result;
    }

    public static void Fix() { // Функция, исправляющая сбойные записи о симуляциях
        try {
            try (Statement st = conn.createStatement()) {
                st.execute("UPDATE simulations SET maxqueue = 1 WHERE maxqueue = 0"); // Именно это поле используется как флаг начатой, но не завершённой симуляции
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

    }

    public static LinkedList<Product> getRangeofGoods() {
        return RangeofGoods;
    }

}
