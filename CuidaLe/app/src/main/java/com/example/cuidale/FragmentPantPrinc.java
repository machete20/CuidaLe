package com.example.cuidale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentPantPrinc extends Fragment {

    private ImageView cuenta, menu;
    private CardView recetas, farmacias, pastillero, recordatorios, historial, calendario;
    private LinearLayout usuario;
    private TextView nomUsu, dirUsu;
    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pant_princ, container, false);

        nomUsu = v.findViewById(R.id.nomUsuario);
        dirUsu = v.findViewById(R.id.dirUsuario);
        cuenta = v.findViewById(R.id.cuentaFarmacias);
        menu = v.findViewById(R.id.menuPastillero);
        usuario = v.findViewById(R.id.UsuarioInicio);
        recetas = v.findViewById(R.id.CardRecetas);
        farmacias = v.findViewById(R.id.CardFarmacias);
        pastillero = v.findViewById(R.id.CardPastillero);
        recordatorios = v.findViewById(R.id.CardRecordatorios);
        historial = v.findViewById(R.id.CardHistorial);
        calendario = v.findViewById(R.id.CardCalendario);

        cuenta.setOnClickListener(this::navegar);
        menu.setOnClickListener(v -> navegar(v, R.id.action_fragmentPantPrinc_to_fragmentMenuDesplegable));
        usuario.setOnClickListener(this::navegar);
        recetas.setOnClickListener(v -> navegar(v, R.id.fragmentRecetas));
        farmacias.setOnClickListener(v -> navegar(v, R.id.fragmentFarmarcias));
        pastillero.setOnClickListener(v -> navegar(v, R.id.fragmentPastillero));
        recordatorios.setOnClickListener(v -> navegar(v, R.id.fragmentRecordatorios));
        historial.setOnClickListener(v -> navegar(v, R.id.fragmentHistorial));
        calendario.setOnClickListener(v -> navegar(v, R.id.fragmentCalendario));

        // 👇 Nuevo bloque: Recibir datos desde Bundle si los hay
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("nombrePaciente") && bundle.containsKey("ubicacionPaciente")) {
            String nombre = bundle.getString("nombrePaciente");
            String ubicacion = bundle.getString("ubicacionPaciente");

            nomUsu.setText(nombre != null ? nombre : "Sin nombre");
            dirUsu.setText(ubicacion != null ? ubicacion : "Sin dirección");
        } else {
            // Si no hay datos en el bundle, cargamos desde Firebase como respaldo
            obtenerDatosUsuario();
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Si no se recibió bundle, recargar desde Firebase
        if (getArguments() == null) {
            obtenerDatosUsuario();
        }
    }

    private void navegar(View v) {
        NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.fragmentUsuarioCuidador);
    }

    private void navegar(View v, int destino) {
        NavController navController = Navigation.findNavController(v);
        navController.navigate(destino);
    }

    private void obtenerDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference refCuidador = database.getReference("usuarios").child(uid);

            refCuidador.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshotCuidador) {
                    if (snapshotCuidador.exists()) {
                        String pacienteUID = snapshotCuidador.child("pacienteAsignadoUID").getValue(String.class);

                        if (pacienteUID != null && !pacienteUID.isEmpty()) {
                            DatabaseReference refPaciente = database.getReference("usuarios").child(pacienteUID);
                            refPaciente.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshotPaciente) {
                                    if (snapshotPaciente.exists()) {
                                        String nombrePaciente = snapshotPaciente.child("nombre").getValue(String.class);
                                        String direccionPaciente = snapshotPaciente.child("direccion").getValue(String.class);

                                        requireActivity().runOnUiThread(() -> {
                                            nomUsu.setText(nombrePaciente != null ? nombrePaciente : "Sin nombre");
                                            dirUsu.setText(direccionPaciente != null ? direccionPaciente : "Sin dirección");
                                        });
                                    } else {
                                        mostrarToast("No se encontraron datos del paciente");
                                        requireActivity().runOnUiThread(() -> {
                                            nomUsu.setText("Sin paciente asignado");
                                            dirUsu.setText("");
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    mostrarToast("Error al cargar paciente: " + error.getMessage());
                                }
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                nomUsu.setText("Sin paciente asignado");
                                dirUsu.setText("");
                            });
                        }
                    } else {
                        mostrarToast("No se encontraron datos del cuidador");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mostrarToast("Error al consultar cuidador: " + error.getMessage());
                }
            });
        } else {
            mostrarToast("No hay usuario autenticado");
        }
    }

    private void mostrarToast(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}
