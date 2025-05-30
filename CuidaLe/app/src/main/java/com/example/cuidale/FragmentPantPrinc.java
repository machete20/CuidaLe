package com.example.cuidale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FragmentPantPrinc extends Fragment {

    private ImageView cuenta;
    private ImageView menu;
    private CardView recetas;
    private CardView farmacias;
    private CardView pastillero;
    private CardView recordatorios;
    private CardView historial;
    private CardView calendario;

    private LinearLayout usuario;

    private TextView nomUsu;
    private TextView dirUsu;
    private TextView dniUsu;

    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pant_princ,container,false);

        nomUsu = v.findViewById(R.id.nomUsuario);
        dirUsu = v.findViewById(R.id.dirUsuario);
        dniUsu = v.findViewById(R.id.dniUsuario);

        cuenta = v.findViewById(R.id.cuentaFarmacias);

        cuenta.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        menu = v.findViewById(R.id.menuPastillero);

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_fragmentPantPrinc_to_fragmentMenuDesplegable);
        });

        usuario = v.findViewById(R.id.UsuarioInicio);

        usuario.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentUsuarioCuidador);
        });

        recetas = v.findViewById(R.id.CardRecetas);

        recetas.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentRecetas);
        });

        farmacias = v.findViewById(R.id.CardFarmacias);

        farmacias.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentFarmarcias);
        });

        pastillero = v.findViewById(R.id.CardPastillero);

        pastillero.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPastillero);
        });

        recordatorios = v.findViewById(R.id.CardRecordatorios);

        recordatorios.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentRecordatorios);
        });

        historial = v.findViewById(R.id.CardHistorial);

        historial.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentHistorial);
        });

        calendario = v.findViewById(R.id.CardCalendario);

        calendario.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentCalendario);
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
                        String dir = snapshot.child("direccion").getValue(String.class);

                        // Asegurarse de actualizar las vistas con los datos obtenidos en el hilo principal
                        requireActivity().runOnUiThread(() -> {
                            nomUsu.setText(nombre);
                            dirUsu.setText(dir);
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
}