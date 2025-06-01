package com.example.cuidale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentHistorial extends Fragment {

    private View v;
    private ImageButton atras;
    private ImageView menu, cuenta;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_historial, container, false);

        atras = v.findViewById(R.id.btn_retrocesoHistorial);
        menu = v.findViewById(R.id.menuPastillero);
        cuenta = v.findViewById(R.id.cuentaFarmacias);
        recyclerView = v.findViewById(R.id.recyclerViewHistorial); // Asegúrate de poner este RecyclerView en tu XML

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new HistorialAdapter(cargarHistorial()));

        atras.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.fragmentPantPrinc));
        menu.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.fragmentMenuDesplegable));
        cuenta.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.fragmentUsuarioCuidador));

        return v;
    }

    private List<HistorialItem> cargarHistorial() {
        SharedPreferences prefs = requireContext().getSharedPreferences("med_prefs", Context.MODE_PRIVATE);
        String json = prefs.getString("medicamentos", null);

        List<Medication> listaMed;
        if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Medication>>() {}.getType();
            listaMed = gson.fromJson(json, tipoLista);
        } else {
            listaMed = new ArrayList<>();
        }

        List<HistorialItem> historial = new ArrayList<>();
        for (Medication med : listaMed) {
            historial.add(new HistorialItem(med.getNombre(), med.getHora(), med.isTomada(), med.getHoraTomada()));
        }
        return historial;
    }
}
