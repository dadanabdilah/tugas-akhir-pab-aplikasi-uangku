package com.fkomuniku.uangku.model;

import java.util.List;

public class RekeningResponse {
    private String status;
    private List<Rekening> data;

    public String getStatus() {
        return status;
    }

    public List<Rekening> getData() {
        return data;
    }
}

