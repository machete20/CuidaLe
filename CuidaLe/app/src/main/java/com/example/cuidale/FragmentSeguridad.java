package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class FragmentSeguridad extends Fragment {

    private View v;
    private ImageButton atras;

    private TextView contrasena;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_seguridad, container, false);
        atras = v.findViewById(R.id.btn_retrocesoSeguridad);

        contrasena = v.findViewById(R.id.contrasenaSeguridad);

        atras.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        contrasena.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentContrasena);
        });

        return v;
    }
}