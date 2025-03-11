package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentMenuDesplegable extends Fragment {

    private View v;

    private ImageView ajustes;

    private ImageView menuCerrar;

    private ImageView cuenta;

    private TextView recordatorio;

    private TextView pastillero;

    private TextView recetas;

    private TextView historial;

    private TextView inicio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu_desplegable,container,false);

        inicio = v.findViewById(R.id.TextoMenuPrincp);

        inicio.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        ajustes = v.findViewById(R.id.BotonAjustes);

        ajustes.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentAjustes);
        });

        menuCerrar = v.findViewById(R.id.menuCerra);

        menuCerrar.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        cuenta = v.findViewById(R.id.CuentaMenu);

        cuenta.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        recordatorio = v.findViewById(R.id.RecordatorioMenu);

        recordatorio.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentRecordatorios);
        });

        pastillero = v.findViewById(R.id.PastilleroMenu);

        pastillero.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPastillero);
        });

        recetas = v.findViewById(R.id.RecetasMenu);

        recetas.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentRecetas);
        });

        historial = v.findViewById(R.id.HistorialMenu);

        historial.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentHistorial);
        });


        return v;
    }
}