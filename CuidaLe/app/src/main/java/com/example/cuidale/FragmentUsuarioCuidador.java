package com.example.cuidale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

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
    private TextView nomUsu;
    private TextView correoUsu;
    private TextView dniUsu;
    private Button cerrarSesion;
    private Button datos;
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

        menu.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        btnAñadir.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentUsuarioCuidador_to_agregarPacienteFragment);
        });

        datos=v.findViewById(R.id.cambiar);

        datos.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentDatosPersonales);
        });

        atras = v.findViewById(R.id.btn_retroceso);

        atras.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });

        cerrarSesion = v.findViewById(R.id.btnLogoutC);

        cerrarSesion.setOnClickListener(v -> {
            // Cerrar la sesión
            AuthManager manager = new AuthManager(requireContext()); // Pasar el contexto
            manager.cerrarSesion();
            // Obtener el NavController y navegar al fragmento de inicio de sesión
            NavController navController = Navigation.findNavController(v);
            // Usar popUpTo para asegurarse de que no se pueda volver atrás al fragmento previo
            navController.navigate(R.id.fragmentInicioSes, null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.fragmentInicioSes, true) // PopUp hasta el fragmento de inicio sesión
                            .build());
        });

        return v;
    }

    public void onStart() {
        super.onStart();
        // Llamar a obtenerDatosUsuario cada vez que el fragmento se haga visible
        obtenerDatosUsuario();
    }

    private void obtenerDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Verificar si el usuario está autenticado
        if (user != null) {
            Log.d("Firebase", "Usuario autenticado"); // Verifica si el usuario está autenticado
            String uid = user.getUid();

            // Actualizamos la URL para la base de datos de Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference ref = database.getReference("usuarios").child(uid);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d("Firebase", "Datos encontrados en Firebase"); // Si los datos existen

                        // Si los datos del usuario existen en la base de datos
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        String correo = snapshot.child("correo").getValue(String.class);
                        String dni = snapshot.child("dni").getValue(String.class);

                        // Asegurarse de actualizar las vistas con los datos obtenidos en el hilo principal
                        requireActivity().runOnUiThread(() -> {
                            nomUsu.setText(nombre);
                            correoUsu.setText(correo);
                            dniUsu.setText(dni);
                        });
                    } else {
                        // Si no se encuentran datos para este usuario
                        Log.d("Firebase", "No se encontraron datos para este usuario");
                        Toast.makeText(getContext(), "No se encontraron datos para este usuario", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Si ocurre un error al consultar la base de datos
                    Log.e("Firebase", "Error al consultar los datos: " + error.getMessage());
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Si el usuario no está autenticado
            Log.d("Firebase", "No hay usuario autenticado");
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
                contenedorPacientes.removeAllViews(); // Limpia antes de añadir

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Paciente paciente = snapshot.getValue(Paciente.class);
                    if (paciente != null) {
                        View card = crearVistaPaciente(paciente);
                        contenedorPacientes.addView(card, 0); // Añadir arriba
                    }
                }
            }
        });
    }

    private View crearVistaPaciente(Paciente paciente) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Inflamos un layout XML individual para cada paciente (ver paso 3)
        View vista = inflater.inflate(R.layout.item_paciente, contenedorPacientes, false);

        TextView nombre = vista.findViewById(R.id.txtNombrePaciente);
        TextView localizacion = vista.findViewById(R.id.txtLocalizacionPaciente);
        ImageView imagen = vista.findViewById(R.id.imgPaciente);

        nombre.setText(paciente.getNombre());
        localizacion.setText(paciente.getLocalizacion());

        // Puedes poner imagen por defecto o de Firebase Storage si quieres
        imagen.setImageResource(R.drawable.defaultprofile);

        return vista;
    }
}


