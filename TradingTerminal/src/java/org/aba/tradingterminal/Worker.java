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
import java.util.LinkedList;
import javax.swing.Timer;

class Worker {

    public int steps = 0;
    public boolean ready = false;

    private final int maxsteps = 2880;
    private Distribution distr = new Distribution();
    private LinkedList<Buyer> BuyersList = new LinkedList<Buyer>();
    private Generator generator;
    private Stat stat = new Stat();

    private ActionListener al = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (steps == maxsteps) {
                ready = true;
                timer.stop();
                return;
            }

            for (steps = 0; steps < maxsteps; steps++) {
                int BuyersNum = distr.GetBuyers(steps);

                for (int i = 0; i < BuyersNum; i++) {
                    BuyersList.addLast(generator.CreateBuyer());
                }

                if (stat.MaxQueue < BuyersList.size()) {
                    stat.MaxQueue = BuyersList.size();
                    stat.MaxQueueTime = steps;
                }
                terminal.Serve(BuyersList.peekFirst());
                BuyersList.removeFirst();

            }
        }

    };

    private Timer timer = new Timer(25, al);

    private Terminal terminal = new Terminal();
    private Admin admin = new Admin();

    public void StartSim(int peoplecount, int goodscount) {
        int simid = -1;
        distr.clients = peoplecount;
        distr.goods = goodscount;
        generator = new Generator(goodscount);
        if (SQLAgent.TestConnect()) {
            simid = SQLAgent.Started(peoplecount, goodscount);
            timer.start();
        }
    }

}
