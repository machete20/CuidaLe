package com.example.cuidale;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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
    private ImageView menu;
    private ImageView cuenta;
    private RecyclerView recyclerView;

    // Cambiar la lista a ArrayList para poder modificarla
    private List<Medication> medicamentos;
    private MedicationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pastillero, container, false);

        atras = v.findViewById(R.id.backButtonPastillero);
        menu = v.findViewById(R.id.menuPastillero);
        cuenta = v.findViewById(R.id.cuentaPastillero);
        add = v.findViewById(R.id.addButtonPastillero);
        recyclerView = v.findViewById(R.id.medicationRecyclerView);

        // Convertir a ArrayList para agregar dinámicamente
        medicamentos = new ArrayList<>();
        medicamentos.add(new Medication("08:00", "Ibuprofeno", true));
        medicamentos.add(new Medication("12:00", "Paracetamol", true));
        medicamentos.add(new Medication("20:00", "Omeprazol", false));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MedicationAdapter(medicamentos);
        recyclerView.setAdapter(adapter);

        // Navegaciones
        atras.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        menu.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        cuenta.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        // Botón añadir abre diálogo para agregar medicamento
        add.setOnClickListener(view -> showAddMedicineDialog());

        return v;
    }

    private void showAddMedicineDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_medicine, null);

        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        final EditText editTextMedicineName = dialogView.findViewById(R.id.editTextMedicineName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Añadir Medicamento")
                .setView(dialogView)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    String time = editTextTime.getText().toString().trim();
                    String name = editTextMedicineName.getText().toString().trim();

                    if (TextUtils.isEmpty(time) || TextUtils.isEmpty(name)) {
                        Toast.makeText(getContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Añadir el nuevo medicamento y actualizar el RecyclerView
                        medicamentos.add(new Medication(time, name, false));
                        adapter.notifyItemInserted(medicamentos.size() - 1);
                        recyclerView.scrollToPosition(medicamentos.size() - 1);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
