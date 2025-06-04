package com.example.cuidale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentDatosPersonales extends Fragment {

    private EditText etNombre, etDNI, etDireccion, etCorreo;
    private ImageButton btnBack;
    private Button btnGuardar;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datos_personales, container, false);

        // Enlazar vistas
        etNombre = v.findViewById(R.id.EditTextNombre);
        etDNI = v.findViewById(R.id.EditTextDNI);
        etDireccion = v.findViewById(R.id.EditTextDireccion);
        etCorreo = v.findViewById(R.id.EditTextCorreo);
        btnBack = v.findViewById(R.id.btn_retrocesoDatos);
        btnGuardar = v.findViewById(R.id.button);

        // Botón volver
        btnBack.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });

        // Obtener UID del usuario actual
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios").child(uid);
        cargarDatosUsuario();

        // Botón guardar
        btnGuardar.setOnClickListener(v1 -> guardarDatosUsuario());

        return v;
    }

    private void cargarDatosUsuario() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String dni = snapshot.child("dni").getValue(String.class);
                    String direccion = snapshot.child("direccion").getValue(String.class);

                    etNombre.setText(nombre);
                    etCorreo.setText(correo);
                    etDNI.setText(dni);
                    etDireccion.setText(direccion);
                } else {
                    Toast.makeText(getContext(), "No se encontraron datos para este usuario.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Error al leer datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatosUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String dni = etDNI.getText().toString().trim();

        // Validaciones básicas
        if (nombre.isEmpty()) {
            etNombre.setError("Introduce un nombre");
            etNombre.requestFocus();
            return;
        }

        if (dni.isEmpty()) {
            etDNI.setError("Introduce un DNI");
            etDNI.requestFocus();
            return;
        }

        if (correo.isEmpty()) {
            etCorreo.setError("Introduce un correo");
            etCorreo.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo no válido");
            etCorreo.requestFocus();
            return;
        }

        // Guardar en Firebase
        userRef.child("nombre").setValue(nombre);
        userRef.child("correo").setValue(correo);
        userRef.child("direccion").setValue(direccion);
        userRef.child("dni").setValue(dni);

        Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
    }
}
