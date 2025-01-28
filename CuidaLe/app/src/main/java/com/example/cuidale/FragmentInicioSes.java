package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentInicioSes extends Fragment {
    private View v;
    private Button b1;
    private Button b2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_inicio_ses,container,false);
        b1 = v.findViewById(R.id.bInicio);
        b2 = v.findViewById(R.id.bRegistro);

        b1.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentInicioSes_to_fragmentPantPrinc);
        });

        b2.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentInicioSes_to_fragmentRegistro);
        });

        return v;
    }
}