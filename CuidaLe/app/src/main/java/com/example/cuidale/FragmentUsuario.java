package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FragmentUsuario extends Fragment {

    private View v;

    private ImageButton atras;

    private ImageView menu;

    private Button cerrarSesion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_usuario, container, false);

        atras = v.findViewById(R.id.btnBackUsuario);

        atras.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        menu = v.findViewById(R.id.menuButtonUsuario);

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        cerrarSesion = v.findViewById(R.id.btnLogout);

        cerrarSesion.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentInicioSes);
        });

        return v;
    }
}