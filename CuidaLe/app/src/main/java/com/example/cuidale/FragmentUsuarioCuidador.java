package com.example.cuidale;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentUsuarioCuidador extends Fragment {

    private View v;

    private ImageButton atras;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v= inflater.inflate(R.layout.fragment_usuariocuidador, container, false);

        atras = v.findViewById(R.id.btn_retroceso);

        atras.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
        return v;
    }
}