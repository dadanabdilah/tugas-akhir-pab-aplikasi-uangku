package com.fkomuniku.uangku;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fkomuniku.uangku.adapter.KeuanganAdapter;
import com.fkomuniku.uangku.model.ApiResponse;
import com.fkomuniku.uangku.model.Kategori;
import com.fkomuniku.uangku.model.KategoriResponse;
import com.fkomuniku.uangku.model.Keuangan;
import com.fkomuniku.uangku.model.KeuanganResponse;
import com.fkomuniku.uangku.model.Rekening;
import com.fkomuniku.uangku.model.RekeningResponse;
import com.fkomuniku.uangku.model.TokenManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeuanganActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private KeuanganAdapter adapter;
    private EditText editSearch;
    private List<Keuangan> keuanganList;
    private List<Keuangan> filteredKeuanganList;
    private int oldNominal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_keuangan);
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

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchKeuanganData();

        findViewById(R.id.btnCatatanBaru).setOnClickListener(v -> showAddDialog());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(KeuanganActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchKeuanganData() {
        String token = TokenManager.getInstance(this).getToken();
        Call<KeuanganResponse> call = ApiClient.getInstanceWithToken(token).getApiService().getKeuangan();
        call.enqueue(new Callback<KeuanganResponse>() {
            @Override
            public void onResponse(Call<KeuanganResponse> call, Response<KeuanganResponse> response) {
                if (response.isSuccessful()) {
                    keuanganList = response.body().getData();
                    filteredKeuanganList = new ArrayList<>(keuanganList);
                    adapter = new KeuanganAdapter(KeuanganActivity.this, filteredKeuanganList, KeuanganActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<KeuanganResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void filter(String text) {
        filteredKeuanganList.clear();
        if (text.isEmpty()) {
            filteredKeuanganList.addAll(keuanganList);
        } else {
            for (Keuangan item : keuanganList) {
                if (item.getKategori().toLowerCase().contains(text.toLowerCase()) || item.getNominal().toLowerCase().contains(text.toLowerCase())) {
                    filteredKeuanganList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tambah_keuangan, null);
        builder.setView(dialogView);

        Spinner spinnerJenis = dialogView.findViewById(R.id.spinnerJenis);
        Spinner spinnerKategori = dialogView.findViewById(R.id.spinnerKategori);
        Spinner spinnerRekening = dialogView.findViewById(R.id.spinnerRekening);
        EditText nominal = dialogView.findViewById(R.id.nominal);
        EditText keterangan = dialogView.findViewById(R.id.keterangan);
        EditText tanggal = dialogView.findViewById(R.id.tanggal);

        // Load rekening data
        loadRekeningData(spinnerRekening);

        // Set up jenis spinner
        spinnerJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String jenis = parent.getItemAtPosition(position).toString().replace(" ", "_");
                loadKategoriData(jenis, spinnerKategori);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up date picker
        tanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(KeuanganActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String formattedMonth = String.format("%02d", monthOfYear + 1);
                        String formattedDay = String.format("%02d", dayOfMonth);
                        String date = year1 + "-" + formattedMonth + "-" + formattedDay;
                        tanggal.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String selectedJenis = spinnerJenis.getSelectedItem().toString();
            String selectedKategori = spinnerKategori.getSelectedItem().toString();
            String selectedRekening = spinnerRekening.getSelectedItem().toString();
            String nominalValue = nominal.getText().toString();
            String keteranganValue = keterangan.getText().toString();
            String tanggalValue = tanggal.getText().toString();

            // Call addKeuangan method to save data
            addKeuangan(selectedJenis, selectedKategori, nominalValue, keteranganValue, tanggalValue, selectedRekening);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addKeuangan(String jenis, String kategori, String nominal, String keterangan, String tanggal, String rekening) {
        Log.d("Keuangan", kategori + jenis + rekening + tanggal + nominal);
        String token = TokenManager.getInstance(this).getToken();
        Call<KeuanganResponse> call = ApiClient.getInstanceWithToken(token).getApiService().addKeuangan(
                kategori, jenis, Integer.parseInt(nominal), tanggal, keterangan, rekening
        );

        call.enqueue(new Callback<KeuanganResponse>() {
            @Override
            public void onResponse(Call<KeuanganResponse> call, Response<KeuanganResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(KeuanganActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    fetchKeuanganData();  // Refresh data
                } else {
                    Toast.makeText(KeuanganActivity.this, "Gagal menambah catatan keuangan", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<KeuanganResponse> call, Throwable t) {
                Toast.makeText(KeuanganActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadRekeningData(Spinner spinnerRekening) {
        String token = TokenManager.getInstance(this).getToken();
        Call<RekeningResponse> call = ApiClient.getInstanceWithToken(token).getApiService().getRekening();
        call.enqueue(new Callback<RekeningResponse>() {
            @Override
            public void onResponse(Call<RekeningResponse> call, Response<RekeningResponse> response) {
                if (response.isSuccessful()) {
                    List<Rekening> rekeningList = response.body().getData();
                    List<String> rekeningNames = new ArrayList<>();
                    for (Rekening rekening : rekeningList) {
                        rekeningNames.add(rekening.getRekening());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(KeuanganActivity.this,
                            android.R.layout.simple_spinner_item, rekeningNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRekening.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<RekeningResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void loadKategoriData(String jenis, Spinner spinnerKategori) {
        String token = TokenManager.getInstance(this).getToken();
        Call<KategoriResponse> call = ApiClient.getInstanceWithToken(token).getApiService().getKeuanganKategori(jenis);
        call.enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.isSuccessful()) {
                    List<Kategori> kategoriList = response.body().getData();
                    List<String> kategoriNames = new ArrayList<>();
                    for (Kategori kategori : kategoriList) {
                        kategoriNames.add(kategori.getKategori());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(KeuanganActivity.this,
                            android.R.layout.simple_spinner_item, kategoriNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerKategori.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    public void showEditDialog(Keuangan keuangan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_keuangan, null);
        builder.setView(dialogView);

        Spinner spinnerJenis = dialogView.findViewById(R.id.spinnerEditJenis);
        Spinner spinnerKategori = dialogView.findViewById(R.id.spinnerEditKategori);
        Spinner spinnerRekening = dialogView.findViewById(R.id.spinnerEditRekening);
        EditText nominal = dialogView.findViewById(R.id.editNominal);
        EditText keterangan = dialogView.findViewById(R.id.editKeterangan);
        EditText tanggal = dialogView.findViewById(R.id.editTanggal);

        // Load rekening data
        loadRekeningData(spinnerRekening);

        // Set initial data
        oldNominal = Integer.parseInt(keuangan.getNominal());
        nominal.setText(String.valueOf(keuangan.getNominal()));
        keterangan.setText(keuangan.getKeterangan());
        tanggal.setText(keuangan.getTanggal());

        // Set up jenis spinner
        ArrayAdapter<CharSequence> jenisAdapter = ArrayAdapter.createFromResource(this,
                R.array.jenis_array, android.R.layout.simple_spinner_item);
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenis.setAdapter(jenisAdapter);
        spinnerJenis.setSelection(jenisAdapter.getPosition(keuangan.getJenisWithSpaces()));

        spinnerJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String jenis = parent.getItemAtPosition(position).toString().replace(" ", "_");
                loadKategoriData(jenis, spinnerKategori);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up date picker
        tanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(KeuanganActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String formattedMonth = String.format("%02d", monthOfYear + 1);
                        String formattedDay = String.format("%02d", dayOfMonth);
                        String date = year1 + "-" + formattedMonth + "-" + formattedDay;
                        tanggal.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setPositiveButton("Update", (dialog, which) -> {
            String selectedJenis = spinnerJenis.getSelectedItem().toString().replace(" ", "_");
            String selectedKategori = spinnerKategori.getSelectedItem().toString();
            String selectedRekening = spinnerRekening.getSelectedItem().toString();
            String newNominal = nominal.getText().toString();
            String newKeterangan = keterangan.getText().toString();
            String newTanggal = tanggal.getText().toString();

            updateKeuangan(keuangan.getId(), selectedJenis, selectedKategori, oldNominal, newNominal, newKeterangan, newTanggal, selectedRekening);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateKeuangan(String id, String jenis, String kategori, int oldNominal, String nominal, String keterangan, String tanggal, String rekening) {
        Log.d("Keuangan", id + jenis + kategori + nominal + keterangan + tanggal + rekening);
        String token = TokenManager.getInstance(KeuanganActivity.this).getToken();
        Call<ApiResponse> call = ApiClient.getInstanceWithToken(token).getApiService().updateKeuangan(id, jenis, kategori, oldNominal, nominal, keterangan, tanggal, rekening);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        Toast.makeText(KeuanganActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(KeuanganActivity.this, "Edit Data Keuangan Berhasil", Toast.LENGTH_SHORT).show();
                    }
                    fetchKeuanganData();
                } else {
                    Toast.makeText(KeuanganActivity.this, "Edit Data Keuangan Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(KeuanganActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
