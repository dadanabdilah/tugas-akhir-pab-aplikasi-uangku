package com.fkomuniku.uangku.model;

public class Rekening {
    private String id;
    private String rekening;
    private String saldo;
    private String id_pengguna;

    // Constructor
    public Rekening(String id, String rekening, String saldo, String id_pengguna) {
        this.id = id;
        this.rekening = rekening;
        this.saldo = saldo;
        this.id_pengguna = id_pengguna;
    }

    // Default constructor
    public Rekening() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getId_pengguna() {
        return id_pengguna;
    }

    public void setId_pengguna(String id_pengguna) {
        this.id_pengguna = id_pengguna;
    }

    @Override
    public String toString() {
        return "Rekening{" +
                "id='" + id + '\'' +
                ", rekening='" + rekening + '\'' +
                ", saldo='" + saldo + '\'' +
                ", id_pengguna='" + id_pengguna + '\'' +
                '}';
    }
}
