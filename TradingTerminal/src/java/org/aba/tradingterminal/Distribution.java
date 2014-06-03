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

import java.util.Random;

public class Distribution {

    private static final int mu = 1440;
    private static final int S = 200;

    // Объект класса Random
    private static final Random RNG = new Random();

    // Логистическое распределение: функция плотности вероятности
    private static double Logistic(double Median, double Scale, double x) {
        if (Scale < 1) { // Деление на 0
            return 0;
        }

        double result = Math.exp(-(x - Median) / Scale);
        result /= Scale;
        result /= Math.pow(Math.exp(-(x - Median) / Scale) + 1, 2);
        return result;
    }

    private final static float TrickyScale = (float) 0.07;

    private static float Tricky(float count, float Scale, float min) {
        float res = (float) (count - count * Scale * Math.log(1 / RNG.nextDouble() - 1));
        res = (res > min) ? res : min;
        return res;
    }

    public static int GetIntCount(float count) {
        return (int) Math.round(Tricky(count, TrickyScale, 1));
    }

    public static float GetFloatCount(float count, float min) {
        return (float) (Math.round(Tricky(count, TrickyScale, min) * 100.0) / 100.0);
    }

    public static int GetBuyers(int time, int clients, double[] IdealSum, int[] RealSum) {
        if (time < 1 || clients < 1) {
            return 0;
        }
        double value = Logistic(mu, S, time) * clients * 2.02 * RNG.nextDouble();
        IdealSum[0] += value;

        double abacaba = Math.round(value);
        int result = (int) abacaba;
        RealSum[0] += result;

        if (Math.abs(RealSum[0] - IdealSum[0]) >= 1) {
            int corr = (int) (RNG.nextDouble() * 2.0 * Math.floor(Math.abs(RealSum[0] - IdealSum[0])) * Math.signum(RealSum[0] - IdealSum[0]));
            IdealSum[0] = IdealSum[0] - RealSum[0] + corr;
            RealSum[0] = 0;
            result -= corr;
        }

        return result;
    }

}
