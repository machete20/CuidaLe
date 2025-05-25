package com.example.cuidale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentPastillero extends Fragment {
    private View v;
    private ImageButton atras;
    private ImageButton add;
    private ImageButton delete;
    private ImageView menu;
    private ImageView cuenta;
    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<Medication> medicamentos;

    private final String PREFS_NAME = "med_prefs";
    private final String KEY_LISTA = "medicamentos";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pastillero, container, false);

        atras = v.findViewById(R.id.backButtonPastillero);
        menu = v.findViewById(R.id.menuPastillero);
        cuenta = v.findViewById(R.id.cuentaPastillero);
        add = v.findViewById(R.id.addButtonPastillero3);
        delete = v.findViewById(R.id.deleteButtonPastillero);
        recyclerView = v.findViewById(R.id.medicationRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicamentos = cargarMedicamentos();

        adapter = new MedicationAdapter(medicamentos);
        recyclerView.setAdapter(adapter);

        // Guardar cada vez que cambie algo
        adapter.setOnMedicationChangeListener(this::guardarMedicamentos);

        atras.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.fragmentPantPrinc));
        menu.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.fragmentMenuDesplegable));
        cuenta.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.fragmentUsuarioCuidador));

        add.setOnClickListener(v -> {
            AddMedicationDialog dialog = new AddMedicationDialog(medicamento -> {
                medicamentos.add(medicamento);
                adapter.notifyItemInserted(medicamentos.size() - 1);
                guardarMedicamentos();
            });
            dialog.show(getChildFragmentManager(), "AddMedicationDialog");
        });

        delete.setOnClickListener(v -> {
            boolean algunoSeleccionado = false;
            for (Medication m : medicamentos) {
                if (m.isSeleccionado()) {
                    algunoSeleccionado = true;
                    break;
                }
            }

            if (!algunoSeleccionado) {
                Toast.makeText(getContext(), "Ningún elemento seleccionado para borrar", Toast.LENGTH_SHORT).show();
            } else {
                medicamentos.removeIf(Medication::isSeleccionado);
                adapter.notifyDataSetChanged();
                guardarMedicamentos();
            }
        });

        return v;
    }

    private void guardarMedicamentos() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicamentos);
        editor.putString(KEY_LISTA, json);
        editor.apply();
    }

    private List<Medication> cargarMedicamentos() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_LISTA, null);
        if (json != null) {
            Gson gson = new Gson();
            Type tipoLista = new TypeToken<List<Medication>>() {}.getType();
            return gson.fromJson(json, tipoLista);
        } else {
            return new ArrayList<>();
        }
    }
}
