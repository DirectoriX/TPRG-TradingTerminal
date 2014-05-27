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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

class Worker {

    private int steps = 0;
    private boolean ready = false;

    private int simid = -1;
    private int queue = 0;
    private float[] Cart = new float[SQLAgent.getRangeofGoods().size()];
    private boolean Discount;

    private int goodscount;
    private int peoplecount;

    double[] IdealSum = new double[1];
    int[] RealSum = new int[1];

    private int PeopleServed = 0;
    private int MaxQueue = 0;
    private int MaxQueueTime = 0;

    private int Profit = 0;
    private int Goods = 0;

    private Timer timer;

    private final ActionListener al = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final int maxsteps = 2880;
            if (getSteps() == maxsteps) {
                ready = true;

                timer.stop();

                SQLAgent.Ended(PeopleServed + queue, PeopleServed, (float) (Goods * 1.0 / PeopleServed), (float) (Profit * 1.0 / PeopleServed), Profit, (MaxQueue == 0) ? 1 : MaxQueue, MaxQueueTime, simid);
                return;
            }

            queue += Distribution.GetBuyers(getSteps(), peoplecount, IdealSum, RealSum);

            if (MaxQueue < queue) {
                MaxQueue = queue;
                MaxQueueTime = getSteps();
            }

            if (queue > 0) {
                Discount = Generator.CreateCart(Cart, goodscount);

                Profit += SQLAgent.Buyed(++PeopleServed, getSteps(), simid, Cart, Discount);

                for (int i = 0; i < Cart.length; i++) {
                    if (Cart[i] != 0) {
                        Goods++;
                    }
                }

                queue--;
            }

            setSteps(getSteps() + 1);
        }

    };

    public void StartSim(int peoplecount, int goodscount) {
        this.goodscount = goodscount;
        this.peoplecount = peoplecount;

        IdealSum[0] = 0.0;
        RealSum[0] = 0;

        if (SQLAgent.TestConnect()) {
            simid = SQLAgent.Started(peoplecount, goodscount);
            timer = new Timer(5, al);
            timer.start();
        }
    }

    public int getSteps() {
        return steps;
    }

    public boolean isReady() {
        return ready;
    }

    public int getSimid() {
        return simid;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

}
