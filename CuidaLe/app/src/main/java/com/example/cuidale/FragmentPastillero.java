package com.example.cuidale;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentPastillero extends Fragment {
    private View v;
    private ImageButton atras, add;
    private ImageView menu, cuenta;
    private RecyclerView recyclerView;
    private MedicationAdapter adapter;
    private List<Medication> medicamentos;

    private static final String PREFS_NAME = "PastilleroPrefs";
    private static final String MED_LIST_KEY = "medicamentos";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pastillero, container, false);

        atras = v.findViewById(R.id.backButtonPastillero);
        menu = v.findViewById(R.id.menuPastillero);
        cuenta = v.findViewById(R.id.cuentaPastillero);
        add = v.findViewById(R.id.addButtonPastillero);
        recyclerView = v.findViewById(R.id.medicationRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicamentos = loadMedicamentos();

        adapter = new MedicationAdapter(medicamentos);
        recyclerView.setAdapter(adapter);

        // Guardar al marcar/desmarcar un checkbox
        adapter.setOnMedicationChangeListener(() -> saveMedicamentos());

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

        add.setOnClickListener(view -> showAddMedicationDialog());

        return v;
    }

    private void showAddMedicationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Añadir Medicamento");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_medicine, null);
        EditText editNombre = dialogView.findViewById(R.id.editNombre);
        EditText editHora = dialogView.findViewById(R.id.editHora);

        // Picker de hora
        editHora.setInputType(InputType.TYPE_NULL);
        editHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                    (TimePicker view, int hourOfDay, int minute1) -> {
                        String time = String.format("%02d:%02d", hourOfDay, minute1);
                        editHora.setText(time);
                    }, hour, minute, true);
            timePicker.show();
        });

        builder.setView(dialogView);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = editNombre.getText().toString().trim();
            String hora = editHora.getText().toString().trim();

            if (!nombre.isEmpty() && !hora.isEmpty()) {
                medicamentos.add(new Medication(hora, nombre, false));
                adapter.notifyItemInserted(medicamentos.size() - 1);
                saveMedicamentos();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveMedicamentos() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(medicamentos);
        editor.putString(MED_LIST_KEY, json);
        editor.apply();
    }

    private List<Medication> loadMedicamentos() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(MED_LIST_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
            return gson.fromJson(json, type);
        } else {
            return new ArrayList<>(); // Lista vacía al iniciar
        }
    }
}
