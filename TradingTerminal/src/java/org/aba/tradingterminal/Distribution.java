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

    public int mu = 1440;
    public int S = 200;
    public int clients;
    public int goods;

    // Объект класса Random
    private static java.util.Random RNG = new Random();

    // Сумма без округления
    private double IdealSum = 0;
    // Сумма после округления
    private int RealSum = 0;

    // Логистическое распределение: функция плотности вероятности
    private double Logistic(double Median, double Scale, double x) {
        double result = Math.exp(-(x - Median) / Scale);
        result /= Scale;
        result /= Math.pow(Math.exp(-(x - Median) / Scale) + 1, 2);
        return result;
    }

    private final float TrickyScale = (float) 0.33;

    private float Tricky(float count, float Scale, float min) {
        float res = (float) (count - count * Scale * Math.log(1 / RNG.nextDouble() - 1));
        return (res > min) ? res : min;
    }

    public int GetIntCount(float count) {
        return (int) Math.round(Tricky(count, TrickyScale, 1));
    }

    public float GetFloatCount(float count, float min) {
        return Tricky(count, TrickyScale, min);
    }

    public int GetBuyers(int time) {
        double value = Logistic(mu, S, time) * clients * 2.02 * RNG.nextDouble();
        IdealSum += value;
        int result = (int) Math.round(value);
        RealSum += result;

        if (Math.abs(RealSum - IdealSum) >= 1) {
            int corr = (int) (Math.floor(Math.abs(RealSum - IdealSum)) * Math.signum(RealSum - IdealSum));
            IdealSum -= RealSum - corr;
            RealSum = 0;
            result -= corr;
        }

        return result;
    }
}
