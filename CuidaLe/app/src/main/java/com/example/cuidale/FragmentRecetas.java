package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class FragmentRecetas extends Fragment {

    private View v;
    private ImageButton atras;
    private ImageButton add;
    private ImageView menu;
    private ImageView cuenta;
    private RecyclerView recyclerView;
    private RecetaAdapter adapter;
    private ArrayList<Receta> recetas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_recetas, container, false);

        recetas = RecetaStorage.cargarRecetas(getContext());

        recyclerView = v.findViewById(R.id.recyclerRecetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecetaAdapter(recetas);
        recyclerView.setAdapter(adapter);

        atras = v.findViewById(R.id.btn_retrocesoRecetas);
        atras.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        menu = v.findViewById(R.id.menuRecetas);
        menu.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        cuenta = v.findViewById(R.id.PerfilRecetas);
        cuenta.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        add = v.findViewById(R.id.addButtonRecetas);
        add.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentAgregarReceta);
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        recetas.clear();
        recetas.addAll(RecetaStorage.cargarRecetas(getContext()));
        adapter.notifyDataSetChanged();
    }
}
