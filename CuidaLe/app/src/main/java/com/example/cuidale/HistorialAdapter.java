package com.example.cuidale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<HistorialItem> historialList;

    public HistorialAdapter(List<HistorialItem> historialList) {
        this.historialList = historialList;
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        HistorialItem item = historialList.get(position);

        holder.nombre.setText(item.getNombre());
        holder.hora.setText("Programada: " + item.getHoraProgramada());
        holder.estado.setText(item.isFueTomada() ? "Tomada a: " + item.getHoraTomada() : "No tomada");
        holder.diferencia.setText(item.isFueTomada() ? "Diferencia: " + item.getDiferenciaTiempo() : "Diferencia: -");
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, hora, estado, diferencia;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombre);
            hora = itemView.findViewById(R.id.textViewHora);
            estado = itemView.findViewById(R.id.textViewEstado);
            diferencia = itemView.findViewById(R.id.textViewDiferencia);
        }
    }
}
