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
            FirebaseDataManager.getInstance().guardarPaciente(paciente);

            Toast.makeText(getContext(), "Paciente agregado", Toast.LENGTH_SHORT).show();

            // Navegar atrás al listado de pacientes
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        return view;
    }
}
