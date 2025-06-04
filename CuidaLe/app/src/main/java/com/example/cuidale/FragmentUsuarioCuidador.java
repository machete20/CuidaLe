package com.example.cuidale;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentUsuarioCuidador extends Fragment {

    private View v;
    private ImageButton atras, btnAñadir;
    private ImageView menu;
    private TextView nomUsu, correoUsu, dniUsu;
    private Button cerrarSesion, datos;
    private LinearLayout contenedorPacientes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_usuariocuidador, container, false);

        contenedorPacientes = v.findViewById(R.id.containerPacientes);
        cargarPacientes();

        nomUsu = v.findViewById(R.id.nombreUsuario);
        correoUsu = v.findViewById(R.id.correoUsuario);
        dniUsu = v.findViewById(R.id.dniUsuario);
        btnAñadir = v.findViewById(R.id.añadirPaciente);
        menu = v.findViewById(R.id.menuDesplegableUsuario);
        datos = v.findViewById(R.id.cambiar);
        atras = v.findViewById(R.id.btn_retroceso);
        cerrarSesion = v.findViewById(R.id.btnLogoutC);

        menu.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        btnAñadir.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentUsuarioCuidador_to_agregarPacienteFragment);
        });

        datos.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentDatosPersonales);
        });

        atras.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        cerrarSesion.setOnClickListener(v -> {
            AuthManager manager = new AuthManager(requireContext());
            manager.cerrarSesion();
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentInicioSes, null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.fragmentInicioSes, true)
                            .build());
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        obtenerDatosUsuario();
    }

    private void obtenerDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Log.d("Firebase", "Usuario autenticado");
            String uid = user.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference ref = database.getReference("usuarios").child(uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String correo = snapshot.child("correo").getValue(String.class);
                        String dni = snapshot.child("dni").getValue(String.class);

                        requireActivity().runOnUiThread(() -> {
                            nomUsu.setText(nombre);
                            correoUsu.setText(correo);
                            dniUsu.setText(dni);
                        });
                    } else {
                        Toast.makeText(getContext(), "No se encontraron datos para este usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPacientes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios").child(uid).child("pacientes");

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                contenedorPacientes.removeAllViews();

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Paciente paciente = snapshot.getValue(Paciente.class);
                    if (paciente != null) {
                        paciente.setId(snapshot.getKey()); // asignar id
                        View card = crearVistaPaciente(paciente);
                        contenedorPacientes.addView(card, 0);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Error al cargar pacientes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View crearVistaPaciente(Paciente paciente) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View vista = inflater.inflate(R.layout.item_paciente, contenedorPacientes, false);

        TextView nombre = vista.findViewById(R.id.txtNombrePaciente);
        TextView localizacion = vista.findViewById(R.id.txtLocalizacionPaciente);
        ImageView imagen = vista.findViewById(R.id.imgPaciente);
        ImageButton btnEliminar = vista.findViewById(R.id.btnEliminarPaciente);

        nombre.setText(paciente.getNombre());
        localizacion.setText(paciente.getLocalizacion());
        imagen.setImageResource(R.drawable.defaultprofile);

        btnEliminar.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Eliminar al paciente \"" + paciente.getNombre() + "\"?")
                    .setPositiveButton("Sí", (dialog, which) -> eliminarPaciente(paciente.getId(), vista))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return vista;
    }

    private void eliminarPaciente(String pacienteId, View vistaPaciente) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("usuarios").child(uid).child("pacientes").child(pacienteId);

        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Paciente eliminado", Toast.LENGTH_SHORT).show();
                contenedorPacientes.removeView(vistaPaciente);
            } else {
                Toast.makeText(getContext(), "Error al eliminar paciente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
