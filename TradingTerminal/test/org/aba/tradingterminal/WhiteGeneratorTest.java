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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author DirectoriX, kramer98489, UN-likE
 */
public class WhiteGeneratorTest {

    public WhiteGeneratorTest() {
    }

    @Before
    public void setUp() {
        try {
            FileWriter fw;
            fw = new FileWriter("DBProps.prop");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append("tradingterminal2\n");
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
     * Test of CreateCart method, of class Generator.
     */
    @Test
    public void testCreateCart() {
        System.out.println("CreateCart");
        float[] cart = {0, 0, 0, 0, 0, 0, 0, 0};
        int count = 4;
        boolean negative = false;
        try {
            SQLAgent.LoadSettings();
            SQLAgent.Connect();
            
            Generator.CreateCart(cart, count);
            for (int i = 0; i < cart.length; i++) {
                if (cart[i] < 0) {
                    negative = true;
                }

            }
            assertFalse("Some of products have negative value", negative);
        } catch (Exception ex) {
            fail("Unexpected exeption");
        }
    }

}
