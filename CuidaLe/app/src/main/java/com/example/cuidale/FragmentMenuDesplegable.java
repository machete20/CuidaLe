package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class FragmentMenuDesplegable extends Fragment {

    private View v;

    private ImageView ajustes;

    private ImageView menuCerrar;

    private ImageView cuenta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_menu_desplegable,container,false);

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

        return v;
    }
}