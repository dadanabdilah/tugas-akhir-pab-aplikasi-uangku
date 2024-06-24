package com.fkomuniku.uangku.model;

import java.util.List;

public class KeuanganResponse {
    private String status;
    private String message;
    private List<Keuangan> data;

    // Getters and setters

    public String getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<Keuangan> getData() {
        return data;
    }

    public void setData(List<Keuangan> data) {
        this.data = data;
    }
}
