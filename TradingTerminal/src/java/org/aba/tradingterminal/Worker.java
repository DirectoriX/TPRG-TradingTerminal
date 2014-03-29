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

    private Distribution distr = new Distribution();
    private LinkedList<Buyer> BuyersList = new LinkedList<>();
    private Generator generator;
    private Stat stat = new Stat();
    int simid = -1;

    private Timer timer;
    private Terminal terminal = new Terminal();
    private Admin admin = new Admin();

    private ActionListener al = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final int maxsteps = 2880;
            if (steps == maxsteps) {
                ready = true;

                admin.setMoney(admin.getMoney() + terminal.Money);
                terminal.AmICorrect = admin.CheckGig(terminal.Profit);

                timer.stop();

                SQLAgent.Ended(stat.PeopleArrived, stat.PeopleServed, stat.AvgGoodsCount, stat.AvgProfit, terminal.Profit, stat.MaxQueue, stat.MaxQueueTime, terminal.AmICorrect, simid);
                return;
            }

            for (int i = 0, BuyersNum = distr.GetBuyers(steps); i < BuyersNum; i++) {
                BuyersList.addLast(generator.CreateBuyer());
                stat.PeopleArrived++;
            }

            if (stat.MaxQueue < BuyersList.size()) {
                stat.MaxQueue = BuyersList.size();
                stat.MaxQueueTime = steps;
            }
            if (BuyersList.size() > 0) {
                terminal.Serve(BuyersList.peekFirst(), steps);
                stat.Consider(BuyersList.peekFirst());
                BuyersList.removeFirst();
                if (terminal.Money > 200000) {
                    admin.setMoney(admin.getMoney() + terminal.Money - admin.getAmount());
                    terminal.Money = admin.getAmount();
                }

            }

            steps++;
        }

    };

    public void StartSim(int peoplecount, int goodscount) {
        distr.clients = peoplecount;
        distr.goods = goodscount;
        generator = new Generator(goodscount);
        if (SQLAgent.TestConnect()) {
            simid = SQLAgent.Started(peoplecount, goodscount);
            terminal.simid = simid;
            terminal.Money = admin.getAmount();
            admin.setMoney(-admin.getAmount());
            timer = new Timer(25, al);
            timer.start();
        }
    }

}
