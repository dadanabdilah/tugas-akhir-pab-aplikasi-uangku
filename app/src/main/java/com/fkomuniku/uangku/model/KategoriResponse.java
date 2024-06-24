package com.fkomuniku.uangku.model;

import com.fkomuniku.uangku.model.Kategori;

import java.util.List;

public class KategoriResponse {
    private String status;
    private List<Kategori> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Kategori> getData() {
        return data;
    }

    public void setData(List<Kategori> data) {
        this.data = data;
    }
}
