package com.fkomuniku.uangku.model;

import com.google.gson.annotations.SerializedName;

public class KeuanganInfoResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public class Data {
        @SerializedName("Saldo")
        private String saldo;

        @SerializedName("UangMasuk")
        private String uangMasuk;

        @SerializedName("UangKeluar")
        private String uangKeluar;

        public String getInfoSaldo() {
            return saldo;
        }

        public String getInfoUangMasuk() {
            return uangMasuk;
        }

        public String getInfoUangKeluar() {
            return uangKeluar;
        }
    }
}
