package com.example.cuidale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class FragmentRegistro extends Fragment {


    private Button registro;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_registro,container,false);
        registro = v.findViewById(R.id.bRegistroR);

        registro.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentInicioSes);
            Toasty.success(requireContext(), "Registro completado", Toast.LENGTH_SHORT, true).show();
        });

        return v;
    }


}