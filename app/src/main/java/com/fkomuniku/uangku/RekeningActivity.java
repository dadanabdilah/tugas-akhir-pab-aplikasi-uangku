package com.fkomuniku.uangku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fkomuniku.uangku.adapter.RekeningAdapter;
import com.fkomuniku.uangku.model.ApiResponse;
import com.fkomuniku.uangku.model.Rekening;
import com.fkomuniku.uangku.model.RekeningResponse;
import com.fkomuniku.uangku.ApiClient;
import com.fkomuniku.uangku.ApiService;
import com.fkomuniku.uangku.model.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekeningActivity extends AppCompatActivity {

    private Button btnTambahRekening;
    private RecyclerView recyclerView;
    private RekeningAdapter rekeningAdapter;
    private EditText editSearch;
    private List<Rekening> rekeningList;
    private List<Rekening> filteredRekeningList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rekening);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editSearch = findViewById(R.id.editSearch);
        editSearch.setOnClickListener(v -> {
            editSearch.setFocusableInTouchMode(true);
            editSearch.setFocusable(true);
            editSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnTambahRekening = findViewById(R.id.btnTambahRekening);
        btnTambahRekening.setOnClickListener(v -> showAddRekeningDialog());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchRekeningData();
    }

    private void fetchRekeningData() {
        String token = TokenManager.getInstance(this).getToken();
        Call<RekeningResponse> call = ApiClient.getInstanceWithToken(token).getApiService().getRekening();
        call.enqueue(new Callback<RekeningResponse>() {
            @Override
            public void onResponse(Call<RekeningResponse> call, Response<RekeningResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rekeningList = response.body().getData();
                    filteredRekeningList = new ArrayList<>(rekeningList);
                    rekeningAdapter = new RekeningAdapter(RekeningActivity.this, filteredRekeningList);
                    recyclerView.setAdapter(rekeningAdapter);
                } else {
                    Toast.makeText(RekeningActivity.this, "Gagal Menampilkan Data Rekening", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RekeningResponse> call, Throwable t) {
                Log.e("RekeningActivity", t.getMessage());
                Toast.makeText(RekeningActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        filteredRekeningList.clear();
        if (text.isEmpty()) {
            filteredRekeningList.addAll(rekeningList);
        } else {
            for (Rekening item : rekeningList) {
                if (item.getRekening().toLowerCase().contains(text.toLowerCase())) {
                    filteredRekeningList.add(item);
                }
            }
        }
        rekeningAdapter.notifyDataSetChanged();
    }

    private void showAddRekeningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tambah_rekening, null);
        builder.setView(dialogView);

        EditText editRekening = dialogView.findViewById(R.id.rekening);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String rekening = editRekening.getText().toString().trim();
            if (!rekening.isEmpty()) {
                addRekening(rekening);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Nama rekening tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addRekening(String rekening) {
        String token = TokenManager.getInstance(this).getToken();
        Call<ApiResponse> call = ApiClient.getInstanceWithToken(token).getApiService().addRekening(rekening);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        Toast.makeText(RekeningActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RekeningActivity.this, "Tambah Data Rekening Berhasil", Toast.LENGTH_SHORT).show();
                    }
                    refreshRekeningData();
                } else {
                    Toast.makeText(RekeningActivity.this, "Tambah Data Rekening Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RekeningActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshRekeningData() {
        fetchRekeningData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RekeningActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
