package com.example.cuidale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private List<Medication> medicationList;

    public MedicationAdapter(List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    @Override
    public MedicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationViewHolder holder, int position) {
        Medication med = medicationList.get(position);
        holder.textHora.setText(med.getHora());
        holder.textNombre.setText(med.getNombre());
        holder.checkTomado.setChecked(med.isTomado());

        holder.checkTomado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            med.setTomado(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView textHora;
        TextView textNombre;
        CheckBox checkTomado;

        public MedicationViewHolder(View itemView) {
            super(itemView);
            textHora = itemView.findViewById(R.id.textHora);
            textNombre = itemView.findViewById(R.id.textMedicamento);
            checkTomado = itemView.findViewById(R.id.checkTomado);
        }
    }
}
