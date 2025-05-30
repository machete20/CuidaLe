package com.example.cuidale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
public class AgregarPacienteFragment extends Fragment {

    private EditText edtNombre, edtLocalizacion;
    private Button btnGuardar;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_agregar_paciente, container, false);

        edtNombre = view.findViewById(R.id.nombrePaciente);
        edtLocalizacion = view.findViewById(R.id.localizacionPaciente);
        btnGuardar = view.findViewById(R.id.btnGuardarPaciente);

        btnGuardar.setOnClickListener(v -> guardarPaciente());

        return view;
    }

    private void guardarPaciente() {
        String nombre = edtNombre.getText().toString().trim();
        String localizacion = edtLocalizacion.getText().toString().trim();

        if (nombre.isEmpty() || localizacion.isEmpty()) {
            Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creamos el paciente con los datos introducidos
        Paciente paciente = new Paciente(null, nombre, localizacion);

        // Guardamos el paciente usando el singleton
        FirebaseDataManager.getInstance().guardarPaciente(paciente);

        Toast.makeText(getContext(), "Paciente guardado con éxito", Toast.LENGTH_SHORT).show();

        // Opcional: Limpiar campos
        edtNombre.setText("");
        edtLocalizacion.setText("");
    }
}


