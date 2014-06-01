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
public class DistributionTest {

    public DistributionTest() {
    }

    /**
     * Test of GetIntCount method, of class Distribution.
     */
    @Test
    public void testGetIntCount() {
        System.out.println("GetIntCount");
        float count = 0;
        int result = 0;

        try {
            result = Distribution.GetIntCount(count);

            for (int i = 0; i < 15; i++) {
                count = (float) (i * Math.exp(i - 1) / (Math.log(i) + 1));
                result = Distribution.GetIntCount(count);
            }
        } catch (Exception ex) {
            fail("GetIntCount - exception! count=" + count + " result=" + result);
        }
    }

    /**
     * Test of GetFloatCount method, of class Distribution.
     */
    @Test
    public void testGetFloatCount() {
        System.out.println("GetFloatCount");
        float count = 0;
        float min = 0;
        float result = 0;

        try {
            Distribution.GetFloatCount(count, min);

            for (int i = 0; i < 15; i++) {
                count = (float) (i * Math.exp(i - 1) / (Math.log(i) + 1));
                min = i * -123 + i * i;
                result = Distribution.GetFloatCount(count, min);
            }
        } catch (Exception ex) {
            fail("GetFloatCount - exception! count=" + count + " min=" + min + "result=" + result);
        }
    }

    /**
     * Test of GetBuyers method, of class Distribution. 0
     */
    @Test
    public void testGetBuyers0() {
        System.out.println("GetBuyers");
        int time = 0;
        int clients = 0;
        double[] IdealSum = {0};
        int[] RealSum = {0};
        int result = 0;
        try {
            result = Distribution.GetBuyers(time, clients, IdealSum, RealSum);
            assertEquals("IdealSum!=0", 0, (int) IdealSum[0]);
            assertEquals("RealSum!=0", 0, RealSum[0]);
        } catch (Exception ex) {
            fail("GetBuyers - exception! time=" + time + " clients=" + clients + " IdealSum=" + IdealSum[0] + " RealSum=" + RealSum[0] + " result=" + result);
        }
    }

    /**
     * Test of GetBuyers method, of class Distribution. Minus1
     */
    @Test
    public void testGetBuyersMinus1() {
        System.out.println("GetBuyers");
        int time = -1;
        int clients = -1;
        double[] IdealSum = {-1};
        int[] RealSum = {-1};
        int result = 0;
        try {
            result = Distribution.GetBuyers(time, clients, IdealSum, RealSum);
            assertEquals("IdealSum!=0", -1, (int) IdealSum[0]);
            assertEquals("RealSum!=0", -1, RealSum[0]);
        } catch (Exception ex) {
            fail("GetBuyers - exception! time=" + time + " clients=" + clients + " IdealSum=" + IdealSum[0] + " RealSum=" + RealSum[0] + " result=" + result);
        }
    }

    /**
     * Test of GetBuyers method, of class Distribution. Random
     */
    @Test
    public void testGetBuyersRandom() {
        System.out.println("GetBuyers");
        int time = (int) Math.random()+5;
        int clients = (int) Math.random()+5;
        double[] IdealSum = {Math.random()};
        int[] RealSum = {(int) Math.random()};
        int result = 0;
        try {
            result = Distribution.GetBuyers(time, clients, IdealSum, RealSum);
            assertEquals("IdealSum!=0", 0, (int) IdealSum[0]);
            assertEquals("RealSum!=0", 0, RealSum[0]);
        } catch (Exception ex) {
            fail("GetBuyers - exception! time=" + time + " clients=" + clients + " IdealSum=" + IdealSum[0] + " RealSum=" + RealSum[0] + " result=" + result);
        }
    }

    /**
     * Test of GetBuyers method, of class Distribution. 99999
     */
    @Test
    public void testGetBuyers99999() {
        System.out.println("GetBuyers");
        int time = 99999;
        int clients = 99999;
        double[] IdealSum = {99999};
        int[] RealSum = {99999};
        int result = 0;
        try {
            result = Distribution.GetBuyers(time, clients, IdealSum, RealSum);
            assertEquals("IdealSum!=0", 0, (int) IdealSum[0]);
            assertEquals("RealSum!=0", 0, RealSum[0]);
        } catch (Exception ex) {
            fail("GetBuyers - exception! time=" + time + " clients=" + clients + " IdealSum=" + IdealSum[0] + " RealSum=" + RealSum[0] + " result=" + result);
        }
    }

    /**
     * Test of GetBuyers method, of class Distribution. 9999999999
     */
    @Test
    public void testGetBuyers9999999999() {
        System.out.println("GetBuyers");
        int time = 999999999;
        int clients = 999999999;
        double[] IdealSum = {9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999D};
        int[] RealSum = {999999999};
        int result = 0;
        try {
            result = Distribution.GetBuyers(time, clients, IdealSum, RealSum);
            assertEquals("IdealSum!=0", 0, (int) IdealSum[0]);
            assertEquals("RealSum!=0", 0, RealSum[0]);
        } catch (Exception ex) {
            fail("GetBuyers - exception! time=" + time + " clients=" + clients + " IdealSum=" + IdealSum[0] + " RealSum=" + RealSum[0] + " result=" + result);
        }
    }

}
