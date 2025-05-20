package com.example.cuidale;

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
        medicamentos = new ArrayList<>();
        adapter = new MedicationAdapter(medicamentos);
        recyclerView.setAdapter(adapter);

        atras.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        menu.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        cuenta.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        add.setOnClickListener(v -> {
            AddMedicationDialog dialog = new AddMedicationDialog(medicamento -> {
                medicamentos.add(medicamento);
                adapter.notifyItemInserted(medicamentos.size() - 1);
            });
            dialog.show(getChildFragmentManager(), "AddMedicationDialog");
        });

        delete.setOnClickListener(v -> adapter.eliminarSeleccionados());

        return v;
    }
}
