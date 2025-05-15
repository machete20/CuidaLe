package com.example.cuidale;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import es.dmoral.toasty.Toasty;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;



public class FragmentInicioSes extends Fragment {
    private View v;
    private Button b1;
    private Button b2;
    private EditText user;
    private EditText password;
    private AuthManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_inicio_ses, container, false);
        manager = new AuthManager(requireContext()); // Pasar el contexto

        b1 = v.findViewById(R.id.bInicio);
        b2 = v.findViewById(R.id.bRegistro);
        user = v.findViewById(R.id.usuario);
        password = v.findViewById(R.id.contrasena);

        return v; // Retorna la vista aquí
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Verificar si el usuario ya está logueado
        if (manager.isLoggedIn()) {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.fragmentPantPrinc); // Navegar a la pantalla principal
        }

        // Manejo de la acción de inicio de sesión
        b1.setOnClickListener(v -> {
            String email = user.getText().toString();
            String pass = password.getText().toString();

            // Validar los campos de entrada
            if (email.isEmpty() || pass.isEmpty()) {
                Toasty.warning(requireContext(), "Por favor ingrese correo y contraseña", Toast.LENGTH_SHORT, true).show();
                return;
            }

            manager.iniciarSesion(email, pass, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser firebaseUser) {

                    FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
                    if (userFirebase != null) {
                        String uid = userFirebase.getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("usuariosPorUid").child(uid);

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String dni = snapshot.getValue(String.class);
                                if (dni != null) {
                                    SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                    prefs.edit().putString("dni", dni).apply();

                                    Toasty.info(requireContext(), "DNI cargado correctamente", Toast.LENGTH_SHORT, true).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("InicioSesion", "Error obteniendo DNI: " + error.getMessage());
                            }
                        });
                    }
                    Toasty.success(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT, true).show();
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.fragmentPantPrinc); // Navegar a la pantalla principal
                }

                @Override
                public void onError(String error) {
                    // Mejorar manejo de errores: Mostrar el mensaje de error específico
                    Toasty.error(requireContext(), error, Toast.LENGTH_SHORT, true).show();
                }
            });
        });

        // Manejo de la acción de registro
        b2.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_fragmentInicioSes_to_fragmentRegistro); // Navegar al fragmento de registro
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Verificar si el usuario ya está autenticado al iniciar el fragmento
        if (manager.isLoggedIn()) {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.fragmentPantPrinc); // Navegar a la pantalla principal si está logueado
        }
    }
}


