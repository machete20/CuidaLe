package com.example.cuidale;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FragmentPastillero extends Fragment {
    private View v;
    private ImageButton atras;
    private ImageButton add;
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
        add = v.findViewById(R.id.addButtonPastillero);
        recyclerView = v.findViewById(R.id.medicationRecyclerView);

        // Setup del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        medicamentos = new ArrayList<>(Arrays.asList(
                new Medication("08:00", "Ibuprofeno", true),
                new Medication("12:00", "Paracetamol", true),
                new Medication("20:00", "Omeprazol", false)
        ));

        adapter = new MedicationAdapter(medicamentos);
        recyclerView.setAdapter(adapter);

        // Navegaciones
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

        // Botón Añadir
        add.setOnClickListener(v -> showAddMedicineDialog());

        return v;
    }

    private void showAddMedicineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_medicine, null);
        builder.setView(dialogView);

        final EditText editTextMedicineName = dialogView.findViewById(R.id.editTextMedicineName);
        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);

        // Listener para abrir selector de hora
        editTextTime.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view, int hourOfDay, int minute1) -> {
                String formattedTime = String.format("%02d:%02d", hourOfDay, minute1);
                editTextTime.setText(formattedTime);
            }, hour, minute, true); // true para formato 24h

            timePickerDialog.show();
        });

        builder.setTitle("Añadir Medicamento");
        builder.setPositiveButton("Añadir", (dialog, which) -> {
            String medicineName = editTextMedicineName.getText().toString().trim();
            String time = editTextTime.getText().toString().trim();

            if (!medicineName.isEmpty() && !time.isEmpty()) {
                medicamentos.add(new Medication(time, medicineName, false));
                adapter.notifyItemInserted(medicamentos.size() - 1);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
