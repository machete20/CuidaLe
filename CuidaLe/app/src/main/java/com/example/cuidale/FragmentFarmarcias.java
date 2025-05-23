package com.example.cuidale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;



public class FragmentFarmarcias extends Fragment {

    private View v;
    private ImageButton atras;

    private ImageButton aGoogleMaps;
    private ImageView menu;
    private ImageView cuenta;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento
        v = inflater.inflate(R.layout.fragment_farmarcias, container, false);

        atras = v.findViewById(R.id.btn_retrocesoFarmacias);

        atras.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        aGoogleMaps = v.findViewById(R.id.previewGoogleMaps);

        aGoogleMaps.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:37.76922,-3.79028?q=farmacias");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
        });

        menu = v.findViewById(R.id.menuFarmacias);

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        cuenta = v.findViewById(R.id.cuentaFarmacias);

        cuenta.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        return v;
    }
}