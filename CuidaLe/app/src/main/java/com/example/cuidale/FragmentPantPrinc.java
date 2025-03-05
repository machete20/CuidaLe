package com.example.cuidale;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class FragmentPantPrinc extends Fragment {

    private ImageView cuenta;
    private ImageView menu;

    private CardView recetas;
    private CardView farmacias;
    private CardView pastillero;
    private CardView recordatorios;
    private CardView historial;
    private CardView calendario;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pant_princ,container,false);

        cuenta = v.findViewById(R.id.IMGcuentaPrincp);

        cuenta.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragment_cuenta);
        });

        menu = v.findViewById(R.id.IMGmenuPrincp);

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentPantPrinc_to_fragmentMenuDesplegable);
        });

        recetas = v.findViewById(R.id.CardRecetas);

        recetas.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragmentRecetas);
        });

        farmacias = v.findViewById(R.id.CardFarmacias);

        farmacias.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragmentFarmacias);
        });

        pastillero = v.findViewById(R.id.CardPastillero);

        pastillero.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragmentPastillero);
        });

        recordatorios = v.findViewById(R.id.CardRecordatorios);

        recordatorios.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragmentRecordatorios);
        });

        historial = v.findViewById(R.id.CardHistorial);

        historial.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragmentHistorial);
        });

        calendario = v.findViewById(R.id.CardCalendario);

        calendario.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragmentCalendario);
        });

        return v;
    }
}