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

public class Buyer {

    // Номер временного интервала, в течение которого покупатель встал в очередь
    public int ArriveTime;
    // Номер временного интервала, в течение которого покупатель был обслужен
    public int ServedTime;
    
    public int Money; // Количество денег
    public boolean Discount = false; // Дисконтная карта

    public LinkedList<Product> Cart = new LinkedList<>(); // Корзина продуктов

    // Добавление товара
    public void AddProduct(Product thing) {
        Cart.add(thing);
        Money += thing.Count * thing.Price + 1;
    }

    // Получение суммарной стоимости всех товаров в корзине
    public int GetTotal() {
        int sum = 0;
        int gc = Cart.size();
        for (int i = 0; i < gc; i++) {
            sum += Cart.get(i).GetTotalPrice();
        }
        return (int) (sum * ((Discount) ? 0.95 : 1));
    }
}
