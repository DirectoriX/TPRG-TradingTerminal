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

    private static final Random RNG = new Random();

    private static Product tmpProduct, EG;

    public static boolean CreateCart(float[] cart, int count) {
        for (int i = 0, goodscount = SQLAgent.getRangeofGoods().size(); i < goodscount; i++) {
            if (RNG.nextInt(goodscount) < count) {
                EG = SQLAgent.getRangeofGoods().get(i);

                tmpProduct.setCode(EG.getCode());
                tmpProduct.setIsPacked(EG.IsPacked());
                tmpProduct.setName(EG.getName());
                tmpProduct.setPrice(EG.getPrice());

                if (tmpProduct.IsPacked()) {
                    tmpProduct.setCount(Distribution.GetIntCount(EG.getCount()));
                } else {
                    tmpProduct.setCount(Distribution.GetFloatCount(EG.getCount(), (float) 0.25));
                }

                cart[i] = tmpProduct.getCount();
            } else {
                cart[i] = 0;
            }

        }

        return RNG.nextInt(100) < 10;
    }

}
