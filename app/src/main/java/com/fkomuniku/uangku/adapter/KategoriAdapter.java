package com.fkomuniku.uangku.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fkomuniku.uangku.R;
import com.fkomuniku.uangku.model.Kategori;
import com.fkomuniku.uangku.ApiClient;
import com.fkomuniku.uangku.ApiService;
import com.fkomuniku.uangku.model.TokenManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> {
    private Context context;
    private List<Kategori> kategoriList;

    public KategoriAdapter(Context context, List<Kategori> kategoriList) {
        this.context = context;
        this.kategoriList = kategoriList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.kategori_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kategori kategori = kategoriList.get(position);
        holder.textKategori.setText(kategori.getKategori());
        holder.textJenis.setText(kategori.getJenis().replace("_", " "));

        holder.editKategori.setOnClickListener(v -> showUpdateDialog(kategori, position));
        holder.hapusKategori.setOnClickListener(v -> showDeleteDialog(kategori, position));
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textKategori, textJenis;
        public ImageView hapusKategori;
        public LinearLayout editKategori;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textKategori = itemView.findViewById(R.id.textKategori);
            textJenis = itemView.findViewById(R.id.textJenis);
            hapusKategori = itemView.findViewById(R.id.hapusKategori);
            editKategori = itemView.findViewById(R.id.editKategori);
        }
    }

    private void showUpdateDialog(Kategori kategori, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_kategori, null);
        builder.setView(dialogView);

        TextInputEditText editTextRekening = dialogView.findViewById(R.id.rekening);
        TextInputEditText editTextJenis = dialogView.findViewById(R.id.editTextJenis);
        Spinner spinnerJenis = dialogView.findViewById(R.id.spinnerJenis);

        editTextRekening.setText(kategori.getKategori());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.jenis_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenis.setAdapter(adapter);

        String jenis = kategori.getJenis().replace("_", " ");
        int spinnerPosition = adapter.getPosition(jenis);
        spinnerJenis.setSelection(spinnerPosition);

        editTextJenis.setOnClickListener(v -> spinnerJenis.performClick());

        spinnerJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                editTextJenis.setText(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newKategori = editTextRekening.getText().toString().trim();
            String newJenis = spinnerJenis.getSelectedItem().toString().replace(" ", "_");

            updateKategori(kategori.getId(), newKategori, newJenis, position);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showDeleteDialog(Kategori kategori, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Hapus kategori ini?");

        builder.setPositiveButton("Hapus", (dialog, which) -> {
            deleteKategori(kategori.getId(), position);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateKategori(String id, String kategori, String jenis, int position) {
        String token = TokenManager.getInstance(context).getToken();
        Call<ResponseBody> call = ApiClient.getInstanceWithToken(token).getApiService().updateKategori(id, kategori, jenis);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    kategoriList.get(position).setKategori(kategori);
                    kategoriList.get(position).setJenis(jenis);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Kategori berhasil diupdate", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Gagal mengupdate kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteKategori(String id, int position) {
        String token = TokenManager.getInstance(context).getToken();

        Call<ResponseBody> call = ApiClient.getInstanceWithToken(token).getApiService().deleteKategori(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    kategoriList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateData(List<Kategori> newKategoriList) {
        this.kategoriList = newKategoriList;
        notifyDataSetChanged();
    }
}
