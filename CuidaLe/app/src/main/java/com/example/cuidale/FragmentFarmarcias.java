package com.example.cuidale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;



public class FragmentFarmarcias extends Fragment {

    private View v;
    private ImageButton atras;

    private ImageButton aGoogleMaps;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_farmarcias, container, false);

        atras = rootView.findViewById(R.id.btn_retrocesoFarmacias);

        atras.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        aGoogleMaps = rootView.findViewById(R.id.previewGoogleMaps);

        aGoogleMaps.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:37.76922,-3.79028?q=farmacias");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
        });

        return rootView;
    }


}