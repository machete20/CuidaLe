package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FragmentHistorial extends Fragment {

    private View v;

    private ImageButton atras;

    private ImageView menu;

    private ImageView cuenta;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_historial, container, false);

        atras = v.findViewById(R.id.btn_retrocesoHistorial);

        atras.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        menu = v.findViewById(R.id.menuPastillero);

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        cuenta = v.findViewById(R.id.cuentaPastillero);

        cuenta.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        return v;
    }
}