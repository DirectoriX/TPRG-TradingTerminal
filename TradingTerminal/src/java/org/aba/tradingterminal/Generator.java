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

import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author DirectoriX, kramer98489, UN-likE
 */
public class Generator {

    private Distribution Dist = new Distribution();

    private LinkedList<Product> RangeOfGoods;
    private int AVGGoodsCount;
    int size;

    public Generator(int avggoodscount) {
        SQLAgent DBA = new SQLAgent();
        if (DBA.TestConnect()) {
            RangeOfGoods = DBA.GetProductInfo();
        }
        AVGGoodsCount = avggoodscount;
        size = RangeOfGoods.size();
    }

    public Buyer CreateBuyer() {
        Buyer buyer = new Buyer();
        Random RNG = new Random();
        if (RNG.nextInt(100) < 10) {
            buyer.Discount = true;
        }
        int goodscount = Dist.GetIntCount(AVGGoodsCount);
        Product tmpProduct;
        for (int i = 0; i < goodscount; i++) {
            tmpProduct = RangeOfGoods.get(RNG.nextInt(size));
            if (tmpProduct.IsPacked) {
                tmpProduct.Count = Dist.GetIntCount(tmpProduct.Count);
            } else {
                tmpProduct.Count = Dist.GetFloatCount(tmpProduct.Count, (float) 0.25);
            }
            buyer.AddProduct(tmpProduct);
        }
        return buyer;
    }
}
