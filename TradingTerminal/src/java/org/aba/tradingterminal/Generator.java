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

/**
 *
 * @author DirectoriX, kramer98489, UN-likE
 */
public class Generator {

    private final int AVGGoodsCount;
    private final int size;
    private Product tmp;
    Random RNG = new Random();

    public Generator(int avggoodscount) {

        if (avggoodscount < Worker.ListOfProducts.size()) {
            AVGGoodsCount = avggoodscount;
        } else {
            AVGGoodsCount = Worker.ListOfProducts.size();
        }

        size = Worker.ListOfProducts.size();
    }

    public void EditBuyer(Buyer buyer) {

        if (RNG.nextInt(100) < 10) {
            buyer.setDiscount(true);
        }

        for (int i = 0; i < size; i++) {
            if (RNG.nextInt(size) < AVGGoodsCount) {
                tmp = Worker.ListOfProducts.get(i);
                if (tmp.isIsPacked()) {
                    buyer.Cart[i] = Distribution.GetIntCount(tmp.getCount());
                } else {
                    buyer.Cart[i] = Distribution.GetFloatCount(tmp.getCount(), (float) 0.100);
                }
            } else {
                buyer.Cart[i] = 0;
            }
        }

    }
}
