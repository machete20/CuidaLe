package com.example.cuidale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView horaTextView;
        public TextView nombreTextView;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            horaTextView = itemView.findViewById(R.id.textHora);
            nombreTextView = itemView.findViewById(R.id.textNombre);
            checkBox = itemView.findViewById(R.id.checkboxTomado);
        }
    }

    @Override
    public MedicationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MedicationAdapter.ViewHolder holder, int position) {
        Medication medicamento = lista.get(position);

        holder.horaTextView.setText(medicamento.getHora());
        holder.nombreTextView.setText(medicamento.getNombre());
        holder.checkBox.setChecked(medicamento.isTomada());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            medicamento.setTomada(isChecked);
            if (listener != null) {
                listener.onMedicationChanged();  // Notifica para guardar
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
