package com.fkomuniku.uangku.model;

public class DaftarRequest {
    private String nama_pengguna;
    private String email;
    private String password;

    public DaftarRequest(String nama_pengguna, String email, String password) {
        this.nama_pengguna = nama_pengguna;
        this.email = email;
        this.password = password;
    }

    // Getters and setters...
}

