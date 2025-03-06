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

public class FragmentAjustes extends Fragment {

    private View v;
    private TextView datos;
    private TextView seguridad;
    private TextView ayuda;

    private ImageView atras;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_ajustes,container,false);

        datos = v.findViewById(R.id.TextDatosPersonales);

        datos.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentDatosPersonales);
        });

        seguridad = v.findViewById(R.id.TextSeguridad);

        seguridad.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragment_seguridad);
        });

        ayuda = v.findViewById(R.id.TextAyuda);

        ayuda.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragment_ayuda);
        });

        atras = v.findViewById(R.id.btn_retrocesoAjustes);

        atras.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });


        return v;
    }
}