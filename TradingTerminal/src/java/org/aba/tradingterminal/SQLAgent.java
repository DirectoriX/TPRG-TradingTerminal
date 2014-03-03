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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.LinkedList;

public class SQLAgent {

    private String DBName;
    private String URL;
    private String user;
    private String password;

    private int simid = -1; // id симуляции

    private Connection conn;

    private void HandleEx(SQLException ex) {
        // handle any errors
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
    }

    public SQLAgent(String DBName, String URL, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance(); // Пытаемся загрузить драйвер
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {

        }

        this.DBName = DBName;
        this.URL = URL;
        this.user = user;
        this.password = password;
    }

    private void Connect() { // Подключение к БД
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + this.URL + "/" + this.DBName + "?"
                    + "user=" + this.user + "&password=" + this.password);
        } catch (SQLException ex) {
            HandleEx(ex);
        }
    }

    private void Disconnect() { // Отключение от БД
        try {
            conn.close();
        } catch (SQLException ex) {
            HandleEx(ex);
        }
    }

    public int Started(int peoplecount, int goodscount) { // Создаём новую запись о симуляции
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
                    ret = simid = res.getInt(1);
                }
            }
        } catch (SQLException ex) {
           HandleEx(ex);
        }

        Disconnect();

        return ret;

    }

    public void Ended(int peoplearrived, int peopleserved, float avggoodscount, float avgprofit, int profit, int maxqueue, int maxqueuetime, boolean iscorrect) {
        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement("UPDATE simulations set peoplearrived = ?, peopleserved = ?, avggoodscount = ?, avgprofit = ?, profit = ?, maxqueue = ?, maxqueuetime = ?, iscorrect = ? WHERE id = ?")) {
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

    public void Buyed(int buyer, int code, float count, int time, int cost) { // Создание записи об элементарной покупке
        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO reports() VALUES (0, ?, ?, ?, ?, ?, ?)")) {
                st.setInt(1, this.simid);
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

    public LinkedList<String> ShowProductInfo() {
        LinkedList<String> result = new LinkedList();

        String request = "SELECT p.code, p.name, p.ispacked, p.count, p.cost FROM products p ORDER BY p.name";

        Connect();
        try {
            try (Statement st = conn.createStatement(); ResultSet res = st.executeQuery(request)) {

                String tmp;

                while (res.next()) {
                    tmp = "";
                    tmp += Integer.toString(res.getInt("code")) + " ";
                    tmp += res.getString("name") + " ";
                    tmp += (res.getBoolean("ispacked")) ? "+ " : "- ";
                    tmp += Float.toString(res.getFloat("count")) + " ";
                    tmp += Float.toString(res.getFloat("cost"));

                    result.add(tmp);
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();

        return result;
    }

    public LinkedList<Product> GetProductInfo() { // Получаем полную информацию о всех товарах
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

    public LinkedList<String> GetResults(int simulationid) { // Получаем подробный отчёт о симуляции
        LinkedList<String> result = new LinkedList();

        String request = "SELECT r.buyerid, r.time, p.name, r.count, r.cost FROM reports r INNER JOIN products p ON r.productcode = p.code WHERE simulationid = ? ORDER BY r.time";

        Connect();
        try {
            try (PreparedStatement st = conn.prepareStatement(request)) {
                st.setInt(1, simulationid);
                try (ResultSet res = st.executeQuery()) {
                    String tmp;

                    while (res.next()) {
                        tmp = "";
                        tmp += Integer.toString(res.getInt("buyerid")) + " ";
                        tmp += res.getTime("time") + " ";
                        tmp += res.getString("name") + " ";
                        tmp += Integer.toString(res.getInt("count")) + " ";
                        tmp += Integer.toString(res.getInt("cost"));

                        result.add(tmp);
                    }
                }
            }
        } catch (SQLException ex) {
            HandleEx(ex);
        }

        Disconnect();

        return result;
    }
}
