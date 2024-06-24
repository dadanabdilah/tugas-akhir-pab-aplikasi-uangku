package com.fkomuniku.uangku.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fkomuniku.uangku.ApiClient;
import com.fkomuniku.uangku.CurrencyFormatter;
import com.fkomuniku.uangku.KeuanganActivity;
import com.fkomuniku.uangku.R;
import com.fkomuniku.uangku.model.ApiResponse;
import com.fkomuniku.uangku.model.Kategori;
import com.fkomuniku.uangku.model.Keuangan;
import com.fkomuniku.uangku.model.TokenManager;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeuanganAdapter extends RecyclerView.Adapter<KeuanganAdapter.ViewHolder> {
    private Context context;
    private List<Keuangan> keuanganList;
    private KeuanganActivity activity;

    public KeuanganAdapter(Context context, List<Keuangan> keuanganList, KeuanganActivity activity) {
        this.context = context;
        this.keuanganList = keuanganList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.keuangan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Keuangan keuangan = keuanganList.get(position);
        holder.nominalTextView.setText(CurrencyFormatter.rupiahFormat(Integer.parseInt(keuangan.getNominal())));
        holder.jenisTextView.setText(keuangan.getJenisWithSpaces());
        holder.tanggalTextView.setText(keuangan.getTanggal());

        holder.editKeuangan.setOnClickListener(v -> {
            activity.showEditDialog(keuangan);
        });

        holder.hapusKeuangan.setOnClickListener(v -> {
            showDeleteDialog(keuangan, position);
        });
    }

    private void showDeleteDialog(Keuangan keuangan, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Hapus catatan ini?");

        builder.setPositiveButton("Hapus", (dialog, which) -> {
            deleteKeuangan(keuangan.getId(), position);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteKeuangan(String id, int position) {
        String token = TokenManager.getInstance(context).getToken();

        Call<ApiResponse> call = ApiClient.getInstanceWithToken(token).getApiService().deleteKeuangan(id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Catatan Keuangan Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                    }
                    keuanganList.remove(position);
                    notifyItemRemoved(position);
                } else {
                    Toast.makeText(context, "Catatan Keuangan Gagal Dihapus", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return keuanganList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nominalTextView;
        public TextView jenisTextView;
        public TextView tanggalTextView;
        public LinearLayout editKeuangan;

        public ImageView hapusKeuangan;

        public ViewHolder(View view) {
            super(view);
            nominalTextView = view.findViewById(R.id.textNominal);
            jenisTextView = view.findViewById(R.id.textJenis);
            tanggalTextView = view.findViewById(R.id.textTanggal);
            editKeuangan = view.findViewById(R.id.editKeuangan);
            hapusKeuangan = view.findViewById(R.id.hapusKeuangan);
        }
    }
}
