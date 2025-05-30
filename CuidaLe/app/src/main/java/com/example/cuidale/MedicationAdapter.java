package com.example.cuidale;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {
    private List<Medication> lista;

    public interface OnMedicationChangeListener {
        void onMedicationChanged();
    }

    private OnMedicationChangeListener listener;

    public MedicationAdapter(List<Medication> lista) {
        this.lista = lista;
    }

    public void setOnMedicationChangeListener(OnMedicationChangeListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView horaTextView;
        public TextView nombreTextView;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            horaTextView = itemView.findViewById(R.id.textHora);
            nombreTextView = itemView.findViewById(R.id.textNombre);
            checkBox = itemView.findViewById(R.id.checkboxTomado);
        }

        public void bind(Medication medicamento) {
            itemView.setBackgroundColor(medicamento.isSeleccionado() ? Color.LTGRAY : Color.TRANSPARENT);
        }
    }

    @NonNull
    @Override
    public MedicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationAdapter.ViewHolder holder, int position) {
        Medication medicamento = lista.get(position);

        holder.horaTextView.setText(medicamento.getHora());
        holder.nombreTextView.setText(medicamento.getNombre());
        holder.checkBox.setChecked(medicamento.isTomada());

        // Al cambiar el estado de la toma, también registramos la hora actual si está tomado
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            medicamento.setTomada(isChecked);
            if (isChecked) {
                String horaActual = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                medicamento.setHoraTomada(horaActual);
            } else {
                medicamento.setHoraTomada(null);
            }
            if (listener != null) {
                listener.onMedicationChanged();
            }
        });

        // Manejamos la selección manual al hacer clic en el ítem
        holder.itemView.setOnClickListener(v -> {
            medicamento.setSeleccionado(!medicamento.isSeleccionado());
            notifyItemChanged(position);
        });

        holder.bind(medicamento);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void eliminarSeleccionados() {
        lista.removeIf(Medication::isSeleccionado);
        notifyDataSetChanged();
        if (listener != null) listener.onMedicationChanged();
    }
}
