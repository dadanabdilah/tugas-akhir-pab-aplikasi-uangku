package com.fkomuniku.uangku;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fkomuniku.uangku.adapter.KategoriAdapter;
import com.fkomuniku.uangku.model.Kategori;
import com.fkomuniku.uangku.model.KategoriResponse;
import com.fkomuniku.uangku.model.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KategoriActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KategoriAdapter kategoriAdapter;
    private List<Kategori> kategoriList;
    private List<Kategori> filteredKategoriList;
    private EditText editSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kategori);
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
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
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

        findViewById(R.id.btnTambahKategori).setOnClickListener(v -> showAdddDialog());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchKategoriData();
    }

    private void fetchKategoriData() {
        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getInstanceWithToken(token).getApiService();

        Call<KategoriResponse> call = apiService.getKategori();
        call.enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    kategoriList = response.body().getData();
                    filteredKategoriList = new ArrayList<>(kategoriList);
                    kategoriAdapter = new KategoriAdapter(KategoriActivity.this, filteredKategoriList);
                    recyclerView.setAdapter(kategoriAdapter);
                } else {
                    Toast.makeText(KategoriActivity.this, "Gagal mendapatkan data kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                Log.e("KategoriActivity", t.getMessage());
                Toast.makeText(KategoriActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        filteredKategoriList.clear();
        if (text.isEmpty()) {
            filteredKategoriList.addAll(kategoriList);
        } else {
            for (Kategori item : kategoriList) {
                if (item.getKategori().toLowerCase().contains(text.toLowerCase()) ||
                        item.getJenis().toLowerCase().contains(text.toLowerCase())) {
                    filteredKategoriList.add(item);
                }
            }
        }
        kategoriAdapter.notifyDataSetChanged();
    }

    private void showAdddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_tambah_kategori, null);
        builder.setView(dialogView);

        TextInputEditText editTextRekening = dialogView.findViewById(R.id.rekening);
        TextInputEditText editText = dialogView.findViewById(R.id.editText);
        Spinner spinnerJenis = dialogView.findViewById(R.id.spinnerJenis);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jenis_array, R.layout.general_item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerJenis.setAdapter(adapter);

        editText.setOnClickListener(v -> spinnerJenis.performClick());

        spinnerJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                editText.setText(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        builder.setPositiveButton("Tambah", (dialog, which) -> {
            String kategori = editTextRekening.getText().toString().trim();
            String jenis = spinnerJenis.getSelectedItem().toString().replace(" ", "_");

            addKategori(kategori, jenis);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(KategoriActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void addKategori(String kategori, String jenis) {
        String token = TokenManager.getInstance(this).getToken();
        Call<ResponseBody> call = ApiClient.getInstanceWithToken(token).getApiService().addKategori(kategori, jenis);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(KategoriActivity.this, "Tambah Kategori Berhasil", Toast.LENGTH_SHORT).show();
                    fetchKategoriData();
                } else {
                    Toast.makeText(KategoriActivity.this, "Gagal menambah kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(KategoriActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
