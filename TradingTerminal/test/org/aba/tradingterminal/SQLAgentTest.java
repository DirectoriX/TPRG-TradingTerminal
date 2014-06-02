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
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author DirectoriX, kramer98489, UN-likE
 */
public class SQLAgentTest {

    public SQLAgentTest() {
    }

    @Before
    public void setUp() {
        try {
            FileWriter fw;
            fw = new FileWriter("DBProps.prop");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("tradingterminal\n");
            bw.append("50.50.0.106:3306\n"); // Network address!!!
            bw.append("kramer98489\n");
            bw.append("4321\n");
            bw.flush();
            bw.close();
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(SQLAgentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of Connect method, of class SQLAgent.
     */
    @Test
    public void testConnect() {
        System.out.println("Connect");
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
        } catch (Exception ex) {
            fail("Connect error");
        }
    }

    /**
     * Test of Disconnect method, of class SQLAgent.
     */
    @Test
    public void testDisconnect() {
        System.out.println("Disconnect");
        try {
            SQLAgent.Disconnect();
        } catch (Exception ex) {
            fail("Disconnect error");
        }
    }

    /**
     * Test of TestConnect method, of class SQLAgent. Disconnected
     */
    @Test
    public void testTestConnectDisconnected() {
        System.out.println("TestConnect");
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Disconnect();
            boolean result = SQLAgent.TestConnect();
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Test connect - disconnected error");
        }
    }

    /**
     * Test of TestConnect method, of class SQLAgent. Connected
     */
    @Test
    public void testTestConnectConnected() {
        System.out.println("TestConnect");
        boolean expResult = true;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            boolean result = SQLAgent.TestConnect();
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Test connect - connected error");
        }
    }

    /**
     * Test of Started method, of class SQLAgent. Normal
     */
    @Test
    public void testStartedNormal() {
        System.out.println("Started");
        int peoplecount = 100;
        int goodscount = 5;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            int result = SQLAgent.Started(peoplecount, goodscount);
            assertTrue("simid <= 0 ???", result > 0);
        } catch (Exception ex) {
            fail("Starting failed");
        }
    }

    /**
     * Test of Started method, of class SQLAgent. Normal disconnected
     */
    @Test
    public void testStartedNormalDisconnected() {
        System.out.println("Started");
        int peoplecount = 100;
        int goodscount = 5;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Disconnect();
            int result = SQLAgent.Started(peoplecount, goodscount);
            assertTrue("simid != 0 ???", result == 0);
        } catch (Exception ex) {
            fail("Starting failed");
        }
    }

    /**
     * Test of Started method, of class SQLAgent. Negative
     */
    @Test
    public void testStartedNegative() {
        System.out.println("Started");
        int peoplecount = -100;
        int goodscount = -5;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            int result = SQLAgent.Started(peoplecount, goodscount);
            assertTrue("simid != 0 ???", result == 0);
        } catch (Exception ex) {
            fail("Starting negative failed");
        }
    }

    /**
     * Test of Started method, of class SQLAgent. 0
     */
    @Test
    public void testStarted0() {
        System.out.println("Started");
        int peoplecount = 0;
        int goodscount = 0;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            int result = SQLAgent.Started(peoplecount, goodscount);
            assertTrue("simid <= 0 ???", result > 0);
        } catch (Exception ex) {
            fail("Starting 0 failed");
        }
    }

    /**
     * Test of Started method, of class SQLAgent. 999999
     */
    @Test
    public void testStarted999999() {
        System.out.println("Started");
        int peoplecount = 999999;
        int goodscount = 999999;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            int result = SQLAgent.Started(peoplecount, goodscount);
            assertTrue("simid <= 0 ???", result > 0);
        } catch (Exception ex) {
            fail("Starting 999999 failed");
        }
    }

    /**
     * Test of Ended method, of class SQLAgent. 0
     */
    @Test
    public void testEnded0() {
        System.out.println("Ended");
        int peoplearrived = 0;
        int peopleserved = 0;
        float avggoodscount = 0.0F;
        float avgprofit = 0.0F;
        int profit = 0;
        int maxqueue = 0;
        int maxqueuetime = 0;
        int simid = 20;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        } catch (Exception ex) {
            fail("Ended 0");
        }
    }

    /**
     * Test of Ended method, of class SQLAgent. 123
     */
    @Test
    public void testEnded123() {
        System.out.println("Ended");
        int peoplearrived = 132;
        int peopleserved = 123;
        float avggoodscount = 123.0F;
        float avgprofit = 123.0F;
        int profit = 123;
        int maxqueue = 123;
        int maxqueuetime = 123;
        int simid = 20;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        } catch (Exception ex) {
            fail("Ended 123");
        }
    }

    /**
     * Test of Ended method, of class SQLAgent. NoSimId
     */
    @Test
    public void testEndedNoSimId() {
        System.out.println("Ended");
        int peoplearrived = 1;
        int peopleserved = 1;
        float avggoodscount = 1.0F;
        float avgprofit = 1.0F;
        int profit = 1;
        int maxqueue = 1;
        int maxqueuetime = 1;
        int simid = 999999;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        } catch (Exception ex) {
            fail("Ended no simid");
        }
    }

    /**
     * Test of Ended method, of class SQLAgent. 0
     */
    @Test
    public void testEndedNegative() {
        System.out.println("Ended");
        int peoplearrived = -50;
        int peopleserved = -50;
        float avggoodscount = -50.0F;
        float avgprofit = -50.0F;
        int profit = -50;
        int maxqueue = -50;
        int maxqueuetime = -50;
        int simid = 20;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        } catch (Exception ex) {
            fail("The test case is a prototype.");
        }
    }

    /**
     * Test of Ended method, of class SQLAgent. 999999
     */
    @Test
    public void testEnded999999() {
        System.out.println("Ended");
        int peoplearrived = 999999;
        int peopleserved = 999999;
        float avggoodscount = 999999.999999F;
        float avgprofit = 999999.999999F;
        int profit = 999999;
        int maxqueue = 999999;
        int maxqueuetime = 999999;
        int simid = 20;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        } catch (Exception ex) {
            fail("Ended 999999");
        }
    }

    /**
     * Test of Ended method, of class SQLAgent. Disconnected
     */
    @Test
    public void testEndedDisconnected() {
        System.out.println("Ended");
        int peoplearrived = 12;
        int peopleserved = 12;
        float avggoodscount = 12.0F;
        float avgprofit = 12.0F;
        int profit = 12;
        int maxqueue = 21;
        int maxqueuetime = 2122;
        int simid = 20;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Disconnect();
            SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        } catch (Exception ex) {
            fail("Ended disconnected");
        }
    }

    /**
     * Test of Buyed method, of class SQLAgent.
     */
    @Test
    public void testBuyed() {
        System.out.println("Buyed");
        int buyerid = 0;
        int time = 0;
        int simid = 0;
        float[] cart = null;
        boolean discount = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            int result = SQLAgent.Buyed(buyerid, time, simid, cart, discount);
            assertTrue("Total < 0", result >= 0);
        } catch (Exception ex) {
            fail("The test case is a prototype.");
        }
    }

    /**
     * Test of ShowProductInfo method, of class SQLAgent.
     */
    @Test
    public void testShowProductInfo() {
        System.out.println("ShowProductInfo");
        LinkedList<String> expResult = null;
        LinkedList<String> result = SQLAgent.ShowProductInfo();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Load method, of class SQLAgent. Connected
     */
    @Test
    public void testLoadConnected() {
        System.out.println("Load");
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();

            SQLAgent.Load();
            assertTrue("Not loaded", SQLAgent.getRangeofGoods().size() > 0);
        } catch (Exception ex) {
            fail("Load error");
        }
    }

    /**
     * Test of Load method, of class SQLAgent. Disconnected
     */
    @Test
    public void testLoadDisconnected() {
        System.out.println("Load");
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Disconnect();

            SQLAgent.Load();
            assertTrue("Not loaded", SQLAgent.getRangeofGoods().size() == 0);
        } catch (Exception ex) {
            fail("Load disconnected error");
        }
    }

    /**
     * Test of GetResults method, of class SQLAgent.
     */
    @Test
    public void testGetResults() {
        System.out.println("GetResults");
        int simulationid = 0;
        LinkedList<String> expResult = null;
        LinkedList<String> result = SQLAgent.GetResults(simulationid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of DeleteProduct method, of class SQLAgent.
     */
    @Test
    public void testDeleteProduct() {
        System.out.println("DeleteProduct");
        int code = 0;
        boolean expResult = false;
        boolean result = SQLAgent.DeleteProduct(code);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of AddProduct method, of class SQLAgent. Normal
     */
    @Test
    public void testAddProductNormal() {
        System.out.println("AddProduct");
        Product item = new Product();
        Random RND = new Random();
        item.setCode(RND.nextInt(1000) + 1000);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = true;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            boolean result = SQLAgent.AddProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Normal product faild.");
        }
    }

    /**
     * Test of AddProduct method, of class SQLAgent. Negative Count
     */
    @Test
    public void testAddProductNegativeCount() {
        System.out.println("AddProduct");
        Product item = new Product();
        Random RND = new Random();
        item.setCode(RND.nextInt(1000) + 1000);
        item.setCount(-3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            boolean result = SQLAgent.AddProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Negative Count product faild.");
        }
    }

    /**
     * Test of AddProduct method, of class SQLAgent. Negative Code
     */
    @Test
    public void testAddProductNegativeCode() {
        System.out.println("AddProduct");
        Product item = new Product();
        Random RND = new Random();
        item.setCode(-666999);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            boolean result = SQLAgent.AddProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Negative Code product faild.");
        }
    }

    /**
     * Test of AddProduct method, of class SQLAgent. Null name
     */
    @Test
    public void testAddProductNullName() {
        System.out.println("AddProduct");
        Product item = new Product();
        Random RND = new Random();
        item.setCode(RND.nextInt(1000) + 1000);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName(null);
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            boolean result = SQLAgent.AddProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Null name product faild.");
        }

    }

    /**
     * Test of AddProduct method, of class SQLAgent. Negative Price
     */
    @Test
    public void testAddProductNegativePrice() {
        System.out.println("AddProduct");
        Product item = new Product();
        Random RND = new Random();
        item.setCode(RND.nextInt(1000) + 1000);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName(null);
        item.setPrice(-RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            boolean result = SQLAgent.AddProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Null name product faild.");
        }

    }

    /**
     * Test of AddProduct method, of class SQLAgent. Null
     */
    @Test(expected = NullPointerException.class)
    public void testAddProductNull() throws Exception {
        System.out.println("AddProduct");
        Product item = null;
        boolean expResult = false;

        SQLAgent.LoadSettings();
        SQLAgent.Connect();
        boolean result = SQLAgent.AddProduct(item);
        assertEquals(expResult, result);

    }

    /**
     * Test of AddProduct method, of class SQLAgent. Dublicate
     */
    @Test
    public void testAddProductDublicate() {
        System.out.println("AddProduct");
        Product item = new Product();
        Random RND = new Random();
        item.setCode(999);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            boolean result = SQLAgent.AddProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Dublicate product faild.");
        }

    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Normal
     */
    @Test
    public void testUpdateProductNormal() {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = true;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            item.setPrice(RND.nextInt(1000) + 3000);
            boolean result = SQLAgent.UpdateProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Normal - failed");
        }
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Neg Price
     */
    @Test
    public void testUpdateProductNegPrice() {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = true;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            item.setPrice(-RND.nextInt(1000) + 3000);
            boolean result = SQLAgent.UpdateProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("NegPrice - failed");
        }
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Neg Code
     */
    @Test
    public void testUpdateProductNegCode() {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            item.setCode(-987654);
            boolean result = SQLAgent.UpdateProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("NegCode - failed");
        }
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Neg Count
     */
    @Test
    public void testUpdateProductNegCount() {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = true;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            item.setCount(-3.4f);
            boolean result = SQLAgent.UpdateProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Empty Name - failed");
        }
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Null Name
     */
    @Test(expected = NullPointerException.class)
    public void testUpdateProductNullName() throws Exception {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = true;

        SQLAgent.LoadSettings();
        SQLAgent.Connect();
        SQLAgent.AddProduct(item);
        item.setName(null);
        boolean result = SQLAgent.UpdateProduct(item);
        assertEquals(expResult, result);
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Empty Name
     */
    @Test
    public void testUpdateProductEmptyName() {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = true;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            item.setName("");
            boolean result = SQLAgent.UpdateProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("EmptyName - Failed");
        }
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Invalid Code
     */
    @Test
    public void testUpdateProductInvalidCode() {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.AddProduct(item);
            item.setCode(11);
            boolean result = SQLAgent.UpdateProduct(item);
            assertEquals(expResult, result);
        } catch (Exception ex) {
            fail("Invalide Code - failed");
        }
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent. Null Prod
     */
    @Test(expected = NullPointerException.class)
    public void testUpdateProductNullProd() throws Exception {
        System.out.println("UpdateProduct");
        Product item = new Product();
        Random RND = new Random();
        int rnd = RND.nextInt(1000) + 2000;
        item.setCode(rnd);
        item.setCount(3.4f);
        item.setIsPacked(false);
        item.setName("Abacaba");
        item.setPrice(RND.nextInt(1000));
        boolean expResult = false;

        SQLAgent.LoadSettings();
        SQLAgent.Connect();
        SQLAgent.AddProduct(item);
        item = null;
        boolean result = SQLAgent.UpdateProduct(item);
        assertEquals(expResult, result);

    }

    /**
     * Test of Fix method, of class SQLAgent. Connected
     */
    @Test
    public void testFixConnected() {
        System.out.println("Fix");
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Fix();
        } catch (Exception ex) {
            fail("Fix connected");
        }
    }

    /**
     * Test of Fix method, of class SQLAgent. Disconnected
     */
    @Test
    public void testFixDisconnected() {
        System.out.println("Fix");
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            SQLAgent.Disconnect();
            SQLAgent.Fix();
        } catch (Exception ex) {
            fail("Fix disconnected");
        }
    }

}
