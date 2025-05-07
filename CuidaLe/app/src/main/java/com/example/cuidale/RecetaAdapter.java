package com.example.cuidale;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {

    private final List<Receta> recetaList;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public RecetaAdapter(List<Receta> recetaList) {
        this.recetaList = recetaList;
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta receta = recetaList.get(position);
        holder.tvNombre.setText(receta.getNombre());
        holder.tvFecha.setText(receta.getFecha());

        // Mostrar selección visual
        holder.itemView.setBackgroundColor(
                position == selectedPosition ? Color.parseColor("#D3E3FC") : Color.TRANSPARENT
        );

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return recetaList.size();
    }

    public Receta getSelectedReceta() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < recetaList.size()) {
            return recetaList.get(selectedPosition);
        }
        return null;
    }

    public void eliminarSeleccionada() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < recetaList.size()) {
            recetaList.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            selectedPosition = RecyclerView.NO_POSITION;
        }
    }

    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreReceta);
            tvFecha = itemView.findViewById(R.id.tvFechaReceta);
        }
    }
}
