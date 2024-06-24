package com.fkomuniku.uangku.model;

public class Keuangan {
    private String id;
    private String nominal;
    private String jenis;
    private String id_rekening;
    private String id_kategori;
    private String keterangan;
    private String tanggal;
    private String kategori;
    private String rekening;
    private String nama_pengguna;

    // Getters
    public String getId() {
        return id;
    }

    public String getNominal() {
        return nominal;
    }

    public String getJenisKeuangan() {
        return jenis;
    }

    public String getIdRekening() {
        return id_rekening;
    }

    public String getIdKategori() {
        return id_kategori;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getKategori() {
        return kategori;
    }

    public String getRekening() {
        return rekening;
    }

    public String getNamaPengguna() {
        return nama_pengguna;
    }

    public String getJenisWithSpaces() {
        return jenis.replace("_", " ");
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public void setJenisKeuangan(String jenis) {
        this.jenis = jenis;
    }

    public void setIdRekening(String id_rekening) {
        this.id_rekening = id_rekening;
    }

    public void setIdKategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public void setNamaPengguna(String nama_pengguna) {
        this.nama_pengguna = nama_pengguna;
    }
}
