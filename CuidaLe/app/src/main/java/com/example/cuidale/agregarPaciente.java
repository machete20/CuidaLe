package com.example.cuidale;

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

import com.google.firebase.database.DatabaseReference;

public class agregarPaciente extends Fragment {

    private EditText nomPaciente, locPaciente;
    private Button btnGuardar;
    private ImageButton btnVolver, btnañadir;

    public agregarPaciente() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agregar_paciente, container, false);

        nomPaciente = v.findViewById(R.id.nombrePaciente);
        locPaciente = v.findViewById(R.id.localizacionPaciente);
        btnGuardar = v.findViewById(R.id.btnGuardarPaciente); // Verifica que este ID exista
        btnVolver = v.findViewById(R.id.btnVolver); // Verifica que este ID exista

        btnGuardar.setOnClickListener(view -> {
            String nombre = nomPaciente.getText().toString().trim();
            String localizacion = locPaciente.getText().toString().trim();

            if (nombre.isEmpty() || localizacion.isEmpty()) {
                Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Obtener la referencia desde FirebaseDataManager
                DatabaseReference pacientesRef = FirebaseDataManager.getInstance().getDatabaseReference();
                String pacienteId = pacientesRef.push().getKey();

                Paciente paciente = new Paciente(pacienteId, nombre, localizacion);

                pacientesRef.child(pacienteId).setValue(paciente)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Paciente añadido", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v).popBackStack();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        btnVolver.setOnClickListener(view -> Navigation.findNavController(v).popBackStack());

        return v;
    }

    // Clase modelo
    public static class Paciente {
        public String id;
        public String nombre;
        public String localizacion;

        public Paciente() {
            // Constructor vacío necesario para Firebase
        }

        public Paciente(String id, String nombre, String localizacion) {
            this.id = id;
            this.nombre = nombre;
            this.localizacion = localizacion;
        }
    }
}