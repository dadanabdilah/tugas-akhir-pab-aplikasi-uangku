package com.fkomuniku.uangku;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {

    public static String rupiahFormat(int amount) {
        Locale indonesia = new Locale("id", "ID");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(indonesia);
        return currencyFormatter.format(amount);
    }
}

