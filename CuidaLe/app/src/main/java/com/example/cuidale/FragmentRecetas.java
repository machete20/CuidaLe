package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentRecetas extends Fragment {

    private View v;
    private ArrayList<Receta> recetas;
    private RecetaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_recetas, container, false);

        recetas = RecetaStorage.cargarRecetas(requireContext());
        Collections.sort(recetas, Collections.reverseOrder());

        RecyclerView recycler = v.findViewById(R.id.recyclerRecetas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecetaAdapter(recetas);
        recycler.setAdapter(adapter);

        ImageButton atras = v.findViewById(R.id.btn_retrocesoRecetas);
        atras.setOnClickListener(view -> Navigation.findNavController(v).navigate(R.id.fragmentPantPrinc));

        ImageView menu = v.findViewById(R.id.menuRecetas);
        menu.setOnClickListener(view -> Navigation.findNavController(v).navigate(R.id.fragmentMenuDesplegable));

        ImageView cuenta = v.findViewById(R.id.PerfilRecetas);
        cuenta.setOnClickListener(view -> Navigation.findNavController(v).navigate(R.id.fragmentUsuarioCuidador));

        ImageButton add = v.findViewById(R.id.addButtonRecetas);
        add.setOnClickListener(view -> Navigation.findNavController(v).navigate(R.id.fragmentAgregarReceta));

        ImageButton btnEliminar = v.findViewById(R.id.btnEliminarReceta);
        btnEliminar.setOnClickListener(view -> {
            Receta seleccionada = adapter.getSelectedReceta();
            if (seleccionada != null) {
                recetas.remove(seleccionada);
                RecetaStorage.guardarRecetas(requireContext(), recetas);
                adapter.eliminarSeleccionada();
                Toast.makeText(getContext(), "Receta eliminada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Selecciona una receta", Toast.LENGTH_SHORT).show();
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        recetas.clear();
        recetas.addAll(RecetaStorage.cargarRecetas(requireContext()));
        Collections.sort(recetas, Collections.reverseOrder());
        adapter.notifyDataSetChanged();
    }
}
