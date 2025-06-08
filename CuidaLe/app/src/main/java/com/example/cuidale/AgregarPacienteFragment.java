package com.example.cuidale;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AgregarPacienteFragment extends Fragment {

    private EditText etNombrePaciente, etUbicacionPaciente;
    private Button btnGuardarPaciente;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_agregar_paciente, container, false);

        etNombrePaciente = view.findViewById(R.id.etNombrePaciente);
        etUbicacionPaciente = view.findViewById(R.id.etUbicacionPaciente);
        btnGuardarPaciente = view.findViewById(R.id.btnGuardarPaciente);

        btnGuardarPaciente.setOnClickListener(v -> {
            String nombre = etNombrePaciente.getText().toString().trim();
            String ubicacion = etUbicacionPaciente.getText().toString().trim();

            if (nombre.isEmpty()) {
                etNombrePaciente.setError("Ingrese el nombre");
                etNombrePaciente.requestFocus();
                return;
            }

            if (ubicacion.isEmpty()) {
                etUbicacionPaciente.setError("Ingrese la ubicación");
                etUbicacionPaciente.requestFocus();
                return;
            }

            Paciente paciente = new Paciente(nombre, ubicacion);

            DatabaseReference refPacientesGlobal = FirebaseDatabase.getInstance(
                            "https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("pacientes");

            // Generar ID único para el paciente
            String pacienteId = refPacientesGlobal.push().getKey();
            if (pacienteId == null) {
                Toast.makeText(getContext(), "Error al generar ID para paciente", Toast.LENGTH_SHORT).show();
                return;
            }
            paciente.setId(pacienteId);

            // Guardar paciente en nodo global
            refPacientesGlobal.child(pacienteId).setValue(paciente)
                    .addOnSuccessListener(aVoid -> {
                        // Asignar paciente al cuidador actual
                        String cuidadorUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference refPacientesCuidador = FirebaseDatabase.getInstance(
                                        "https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("usuarios").child(cuidadorUID).child("pacientes");

                        refPacientesCuidador.child(pacienteId).setValue(paciente)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(getContext(), "Paciente agregado y asignado correctamente", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(v);
                                    navController.popBackStack();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al asignar paciente: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al guardar paciente: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return view;
    }
}
