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

import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author DirectoriX, kramer98489, UN-likE
 */
public class SQLAgentTest {
    
    public SQLAgentTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of LoadSettings method, of class SQLAgent.
     */
    @Test
    public void testLoadSettings() {
        System.out.println("LoadSettings");
        boolean expResult = false;
        boolean result = SQLAgent.LoadSettings();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Connect method, of class SQLAgent.
     */
    @Test
    public void testConnect() {
        System.out.println("Connect");
        SQLAgent.Connect();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Disconnect method, of class SQLAgent.
     */
    @Test
    public void testDisconnect() {
        System.out.println("Disconnect");
        SQLAgent.Disconnect();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of TestConnect method, of class SQLAgent.
     */
    @Test
    public void testTestConnect() {
        System.out.println("TestConnect");
        boolean expResult = false;
        boolean result = SQLAgent.TestConnect();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Started method, of class SQLAgent.
     */
    @Test
    public void testStarted() {
        System.out.println("Started");
        int peoplecount = 0;
        int goodscount = 0;
        int expResult = 0;
        int result = SQLAgent.Started(peoplecount, goodscount);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Ended method, of class SQLAgent.
     */
    @Test
    public void testEnded() {
        System.out.println("Ended");
        int peoplearrived = 0;
        int peopleserved = 0;
        float avggoodscount = 0.0F;
        float avgprofit = 0.0F;
        int profit = 0;
        int maxqueue = 0;
        int maxqueuetime = 0;
        int simid = 0;
        SQLAgent.Ended(peoplearrived, peopleserved, avggoodscount, avgprofit, profit, maxqueue, maxqueuetime, simid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
        int expResult = 0;
        int result = SQLAgent.Buyed(buyerid, time, simid, cart, discount);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
     * Test of Load method, of class SQLAgent.
     */
    @Test
    public void testLoad() {
        System.out.println("Load");
        SQLAgent.Load();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
     * Test of AddProduct method, of class SQLAgent.
     */
    @Test
    public void testAddProduct() {
        System.out.println("AddProduct");
        Product item = null;
        boolean expResult = false;
        boolean result = SQLAgent.AddProduct(item);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of UpdateProduct method, of class SQLAgent.
     */
    @Test
    public void testUpdateProduct() {
        System.out.println("UpdateProduct");
        Product item = null;
        boolean expResult = false;
        boolean result = SQLAgent.UpdateProduct(item);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of Fix method, of class SQLAgent.
     */
    @Test
    public void testFix() {
        System.out.println("Fix");
        SQLAgent.Fix();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
