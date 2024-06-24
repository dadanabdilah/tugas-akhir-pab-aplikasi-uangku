package com.fkomuniku.uangku;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fkomuniku.uangku.adapter.RekeningHomeAdapter;
import com.fkomuniku.uangku.model.KeuanganInfoResponse;
import com.fkomuniku.uangku.model.Rekening;
import com.fkomuniku.uangku.model.RekeningResponse;
import com.fkomuniku.uangku.model.TokenManager;
import com.google.android.material.navigation.NavigationView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private boolean isDialogShowing = false;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView textSaldo, textUangMasuk, textUangKeluar;
    private RecyclerView recyclerView;
    private RekeningHomeAdapter rekeningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        textSaldo = findViewById(R.id.textSaldo);
        textUangMasuk = findViewById(R.id.textUangMasuk);
        textUangKeluar = findViewById(R.id.textUangKeluar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchRekeningData();
        fetchKeuanganInfo();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else if (!isFinishing() && !isDestroyed() && !isDialogShowing) {
            showLogoutDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_rekening) {
            startActivity(new Intent(this, RekeningActivity.class));
        } else if (id == R.id.nav_kategori) {
            startActivity(new Intent(this, KategoriActivity.class));
        } else if (id == R.id.nav_keuangan) {
            startActivity(new Intent(this, KeuanganActivity.class));
        } else if (id == R.id.nav_keluar) {
            showLogoutDialog();
        }

        drawerLayout.closeDrawer(navigationView);
        return true;
    }

    private void showLogoutDialog() {
        isDialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda akan keluar aplikasi?");

        builder.setPositiveButton("Ya", (dialog, which) -> {
            dialog.dismiss();
            logout();
        });

        builder.setNegativeButton("Tidak", (dialog, which) -> {
            dialog.dismiss();
        }).setOnDismissListener(dialog -> isDialogShowing = false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void logout() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchKeuanganInfo() {
        String token = TokenManager.getInstance(this).getToken();
        Call<KeuanganInfoResponse> call = ApiClient.getInstanceWithToken(token).getApiService().getKeuanganInfo();
        call.enqueue(new Callback<KeuanganInfoResponse>() {
            @Override
            public void onResponse(Call<KeuanganInfoResponse> call, Response<KeuanganInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    KeuanganInfoResponse.Data data = response.body().getData();
                    textSaldo.setText(CurrencyFormatter.rupiahFormat(Integer.parseInt(data.getInfoSaldo())));
                    textUangMasuk.setText(CurrencyFormatter.rupiahFormat(Integer.parseInt(data.getInfoUangMasuk())));
                    textUangKeluar.setText(CurrencyFormatter.rupiahFormat(Integer.parseInt(data.getInfoUangKeluar())));
                } else {
                    Toast.makeText(HomeActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KeuanganInfoResponse> call, Throwable t) {
                Log.e("HomeActivity", "Error saat mengambil data", t);
                Toast.makeText(HomeActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRekeningData() {
        String token = TokenManager.getInstance(this).getToken();
        Call<RekeningResponse> call = ApiClient.getInstanceWithToken(token).getApiService().getRekening();
        call.enqueue(new Callback<RekeningResponse>() {
            @Override
            public void onResponse(Call<RekeningResponse> call, Response<RekeningResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Rekening> rekeningList = response.body().getData();
                    rekeningAdapter = new RekeningHomeAdapter(HomeActivity.this, rekeningList);
                    recyclerView.setAdapter(rekeningAdapter);
                } else {
                    Toast.makeText(HomeActivity.this, "Gagal Menampilkan Data Rekening", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RekeningResponse> call, Throwable t) {
                Log.e("RekeningActivity", t.getMessage());
                Toast.makeText(HomeActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
