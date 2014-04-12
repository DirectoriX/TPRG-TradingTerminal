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

    public final static LinkedList<Product> ListOfProducts = new LinkedList<Product>();
    private Buyer buyer = new Buyer();
    private Terminal terminal = new Terminal();
    private Admin admin = new Admin();
    private Stat stat = new Stat();
    private Generator generator;
    private Timer timer;
    int simid = -1;
    public int steps = 0;
    public boolean ready = false;

    private ActionListener al = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final int maxsteps = 2880;
            if (steps == maxsteps) {
                ready = true;

                admin.setMoney(admin.getMoney() + terminal.Money);
                terminal.AmICorrect = admin.CheckGig(terminal.Profit);

                timer.stop();

                SQLAgent.Ended(stat.PeopleArrived, stat.PeopleServed,
                        stat.AvgGoodsCount, stat.AvgProfit, terminal.Profit,
                        stat.MaxQueue, stat.MaxQueueTime, terminal.AmICorrect,
                        simid);
                return;
            }

            terminal.queue = Distribution.GetBuyers(steps);
            if (stat.MaxQueue < terminal.queue) {
                stat.MaxQueue = terminal.queue;
                stat.MaxQueueTime = steps;
            }

            if (terminal.queue > 0) {
                generator.EditBuyer(buyer);
                stat.Consider(buyer.getTotal(), buyer.getSize());
                terminal.Serve(buyer, simid);
            }

            steps++;
        }

    };

    public void StartSim(int peoplecount, int goodscount) {
        Distribution.clients = peoplecount;
        generator = new Generator(goodscount);
        if (SQLAgent.TestConnect()) {
            simid = SQLAgent.Started(peoplecount, goodscount);
            terminal.simid = simid;
            terminal.Money = admin.getAmount();
            admin.setMoney(-admin.getAmount());
            timer = new Timer(20, al);
            timer.start();
        }
    }
}
