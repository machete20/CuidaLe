package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentAgregarReceta extends Fragment {

    private EditText etNombre, etFecha;
    private Button btnGuardar;
    private ImageButton btnVolver;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_agregar_receta, container, false);

        etNombre = v.findViewById(R.id.etNombreReceta);
        etFecha = v.findViewById(R.id.etFechaReceta);
        btnGuardar = v.findViewById(R.id.btnGuardarReceta);
        btnVolver = v.findViewById(R.id.btnVolverAgregar);

        btnGuardar.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();

            if (nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<Receta> recetas = RecetaStorage.cargarRecetas(getContext());
                recetas.add(new Receta(nombre, fecha));
                RecetaStorage.guardarRecetas(getContext(), recetas);
                Toast.makeText(getContext(), "Receta guardada", Toast.LENGTH_SHORT).show();

                Navigation.findNavController(v).popBackStack();
            }
        });

        btnVolver.setOnClickListener(view -> {
            Navigation.findNavController(v).popBackStack();
        });

        return v;
    }
}
