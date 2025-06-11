package com.example.cuidale;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FragmentAgregarReceta extends Fragment {

    private EditText etNombre, etFecha;
    private Button btnGuardar;
    private ImageButton btnVolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agregar_receta, container, false);

        etNombre = v.findViewById(R.id.etNombreReceta);
        etFecha = v.findViewById(R.id.etFechaReceta);
        btnGuardar = v.findViewById(R.id.btnGuardarReceta);
        btnVolver = v.findViewById(R.id.btnVolverAgregar);

        // Configurar el selector de fecha
        etFecha.setOnClickListener(view -> mostrarDatePicker());
        etFecha.setFocusable(false); // Evitar que se abra el teclado
        etFecha.setClickable(true);

        btnGuardar.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();

            if (nombre.isEmpty() || fecha.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!isValidDate(fecha)) {
                Toast.makeText(getContext(), "Por favor ingresa una fecha válida (dd/MM/yyyy)", Toast.LENGTH_SHORT).show();
            } else {
                guardarReceta(nombre, fecha, v);
            }
        });

        btnVolver.setOnClickListener(view -> Navigation.findNavController(v).popBackStack());

        return v;
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etFecha.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void guardarReceta(String nombre, String fecha, View v) {
        try {
            ArrayList<Receta> recetas = RecetaStorage.cargarRecetas(requireContext());
            recetas.add(new Receta(nombre, fecha));
            RecetaStorage.guardarRecetas(requireContext(), recetas);

            Toast.makeText(getContext(), "Receta guardada exitosamente", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).popBackStack();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al guardar la receta: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}