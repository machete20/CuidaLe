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
            int oldPosition = selectedPosition;
            if (selectedPosition == holder.getAdapterPosition()) {
                // Deseleccionar si se hace clic en el mismo elemento
                selectedPosition = RecyclerView.NO_POSITION;
            } else {
                // Seleccionar nuevo elemento
                selectedPosition = holder.getAdapterPosition();
            }

            // Actualizar ambas posiciones
            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recetaList.size();
    }

    public Receta getSelectedReceta() {
        if (selectedPosition != RecyclerView.NO_POSITION &&
                selectedPosition >= 0 &&
                selectedPosition < recetaList.size()) {
            return recetaList.get(selectedPosition);
        }
        return null;
    }

    public void eliminarSeleccionada() {
        if (selectedPosition != RecyclerView.NO_POSITION &&
                selectedPosition >= 0 &&
                selectedPosition < recetaList.size()) {

            recetaList.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);

            // Reajustar la selección después de eliminar
            if (selectedPosition >= recetaList.size()) {
                selectedPosition = RecyclerView.NO_POSITION;
            } else {
                // Mantener la selección válida si hay elementos después
                notifyItemRangeChanged(selectedPosition, recetaList.size() - selectedPosition);
            }

            // Limpiar selección
            selectedPosition = RecyclerView.NO_POSITION;
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        if (oldPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(oldPosition);
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