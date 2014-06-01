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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author DirectoriX, kramer98489, UN-likE
 */
public class GeneratorTest {

    public GeneratorTest() {
    }

    /**
     * Test of CreateCart method, of class Generator. Negative
     */
    @Test
    public void testCreateCartNegativeValue() {
        System.out.println("CreateCart");
        float[] cart = {0};
        int count = -1;
        boolean negative = false;
        try {
            Generator.CreateCart(cart, count);
            for (int i = 0; i < cart.length; i++) {
                if (cart[i] < 0) {
                    negative = true;
                }

            }
            assertFalse("Some of products have negative value", negative);
        } catch (Exception ex) {
            fail("Unexpected exeption - negative");
        }
    }

    /**
     * Test of CreateCart method, of class Generator. Null
     *
     */
    @Test
    public void testCreateCartNull(){
        System.out.println("CreateCart");
        float[] cart = null;
        int count = -1;

        Generator.CreateCart(cart, count);

    }

    /**
     * Test of CreateCart method, of class Generator. TooBig
     */
    @Test
    public void testCreateCartTooBig() {
        System.out.println("CreateCart");
        float[] cart = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int count = 3;
        try {
            Generator.CreateCart(cart, count);
        } catch (Exception ex) {
            fail("Unexpected exeption - too big");
        }
    }

    /**
     * Test of CreateCart method, of class Generator. Not 0
     */
    @Test
    public void testCreateCartNot0() {
        System.out.println("CreateCart");
        float[] cart = {9999, 99999, 9, 9, 9, 9};
        int count = 5;
        try {
            Generator.CreateCart(cart, count);
        } catch (Exception ex) {
            fail("Unexpected exeption - not 0");
        }
    }

    /**
     * Test of CreateCart method, of class Generator. BigCount
     */
    @Test
    public void testCreateCartBigCount() {
        System.out.println("CreateCart");
        float[] cart = {0};
        int count = 9999999;
        try {
            Generator.CreateCart(cart, count);
        } catch (Exception ex) {
            fail("Unexpected exeption - big count");
        }
    }

    /**
     * Test of CreateCart method, of class Generator. Negative cart
     */
    @Test
    public void testCreateCartNegativeCart() {
        System.out.println("CreateCart");
        float[] cart = {-9, -9, -9, -9, -9, -9};
        int count = 5;
        try {
            Generator.CreateCart(cart, count);
        } catch (Exception ex) {
            fail("Unexpected exeption - negative cart");
        }
    }

}
