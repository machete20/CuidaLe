package com.example.cuidale;

import android.os.Bundle;

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
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pant_princ,container,false);
        cuenta = v.findViewById(R.id.IMGcuentaPrincp);
        menu = v.findViewById(R.id.IMGmenuPrincp);

        cuenta.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            //navController.navigate(R.id.fragment_cuenta);
        });

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentPantPrinc_to_fragmentMenuDesplegable);
        });


        return v;
    }
}