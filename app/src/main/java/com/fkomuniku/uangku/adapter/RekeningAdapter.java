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

import com.fkomuniku.uangku.CurrencyFormatter;
import com.fkomuniku.uangku.R;
import com.fkomuniku.uangku.RekeningActivity;
import com.fkomuniku.uangku.model.Rekening;
import com.fkomuniku.uangku.ApiClient;
import com.fkomuniku.uangku.model.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekeningAdapter extends RecyclerView.Adapter<RekeningAdapter.ViewHolder> {
    private Context context;
    private List<Rekening> rekeningList;

    public RekeningAdapter(Context context, List<Rekening> rekeningList) {
        this.context = context;
        this.rekeningList = rekeningList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rekening_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rekening rekening = rekeningList.get(position);
        holder.textRekening.setText(rekening.getRekening());
        holder.textSaldo.setText(CurrencyFormatter.rupiahFormat(Integer.parseInt(rekening.getSaldo())));

        holder.editRekening.setOnClickListener(v -> {
            showUpdateDialog(rekening);
        });

        holder.hapusRekening.setOnClickListener(view -> {
            showDeleteDialog(rekening, position);
        });
    }

    @Override
    public int getItemCount() {
        return rekeningList.size();
    }

    private void showUpdateDialog(Rekening rekening) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_rekening, null);
        builder.setView(dialogView);

        TextInputEditText editRekening = dialogView.findViewById(R.id.editRekening);
        editRekening.setText(rekening.getRekening());

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newRekening = editRekening.getText().toString().trim();
            updateRekening(rekening.getId(), newRekening);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteDialog(Rekening rekening, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Hapus rekening ini?");

        builder.setPositiveButton("Hapus", (dialog, which) -> {
            deleteRekening(rekening.getId(), position);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateRekening(String id, String rekening) {
        String token = TokenManager.getInstance(context).getToken();
        Call<Void> call = ApiClient.getInstanceWithToken(token).getApiService().updateRekening(id, rekening);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Edit Data Rekening Berhasil", Toast.LENGTH_SHORT).show();
                    if (context instanceof RekeningActivity) {
                        ((RekeningActivity) context).refreshRekeningData();
                    }
                } else {
                    Toast.makeText(context, "Edit Data Rekening Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteRekening(String id, int position) {
        String token = TokenManager.getInstance(context).getToken();
        Call<Void> call = ApiClient.getInstanceWithToken(token).getApiService().deleteRekening(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Hapus Data Rekening Berhasil", Toast.LENGTH_SHORT).show();
                    rekeningList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, rekeningList.size());
                } else {
                    Toast.makeText(context, "Hapus Data Rekening Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRekening, textSaldo;
        ImageView hapusRekening;
        LinearLayout editRekening;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRekening = itemView.findViewById(R.id.textRekening);
            textSaldo = itemView.findViewById(R.id.textSaldo);
            hapusRekening = itemView.findViewById(R.id.hapusRekening);
            editRekening = itemView.findViewById(R.id.editRekening);
        }
    }
}
