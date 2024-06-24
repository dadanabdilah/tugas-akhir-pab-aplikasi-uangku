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
import com.fkomuniku.uangku.R;
import com.fkomuniku.uangku.RekeningActivity;
import com.fkomuniku.uangku.model.Rekening;
import com.fkomuniku.uangku.model.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekeningHomeAdapter extends RecyclerView.Adapter<RekeningHomeAdapter.ViewHolder> {
    private Context context;
    private List<Rekening> rekeningList;

    public RekeningHomeAdapter(Context context, List<Rekening> rekeningList) {
        this.context = context;
        this.rekeningList = rekeningList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rekening_home_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rekening rekening = rekeningList.get(position);
        holder.textRekening.setText(rekening.getRekening());
        holder.textSaldo.setText(CurrencyFormatter.rupiahFormat(Integer.parseInt(rekening.getSaldo())));
    }

    @Override
    public int getItemCount() {
        return rekeningList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRekening, textSaldo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRekening = itemView.findViewById(R.id.textRekening);
            textSaldo = itemView.findViewById(R.id.textSaldo);
        }
    }
}
