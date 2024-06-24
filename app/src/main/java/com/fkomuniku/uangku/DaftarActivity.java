package com.fkomuniku.uangku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fkomuniku.uangku.model.DaftarRequest;
import com.fkomuniku.uangku.model.DaftarResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, passwordKonfirmEditText;
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameEditText = findViewById(R.id.editNama);
        emailEditText = findViewById(R.id.editEmail);
        passwordEditText = findViewById(R.id.editPassword);
        passwordKonfirmEditText = findViewById(R.id.editPasswordKonfirmasi);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButtonRegister);

        registerButton.setOnClickListener(v -> registerUser());

        loginButton.setOnClickListener(v -> {
            Intent intent =  new Intent(DaftarActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DaftarActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerUser() {
        String nama_pengguna = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String password_konfirm = passwordKonfirmEditText.getText().toString().trim();

        if (nama_pengguna.isEmpty() || email.isEmpty() || password.isEmpty() || password_konfirm.isEmpty()) {
            Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password_konfirm)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        DaftarRequest registerRequest = new DaftarRequest(nama_pengguna, email, password);

        Call<DaftarResponse> call = ApiClient.getInstanceWithoutToken().getApiService().registerUser(registerRequest);
        call.enqueue(new Callback<DaftarResponse>() {
            @Override
            public void onResponse(Call<DaftarResponse> call, Response<DaftarResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        Intent intent =  new Intent(DaftarActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(DaftarActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Register", response.body().toString());
                        Toast.makeText(DaftarActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("Register", response.errorBody() != null ? response.errorBody().string() : "Error body is null");
                    } catch (IOException e) {
                        Log.e("Register", "Error reading errorBody", e);
                    }
                    Toast.makeText(DaftarActivity.this, "Registrasi gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DaftarResponse> call, Throwable t) {
                Log.e("Register", "Register user failed", t);
                Toast.makeText(DaftarActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
