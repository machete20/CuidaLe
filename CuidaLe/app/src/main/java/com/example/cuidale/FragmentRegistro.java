package com.example.cuidale;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import android.content.SharedPreferences;
import android.content.Context;

import es.dmoral.toasty.Toasty;

public class FragmentRegistro extends Fragment {

    private Button registro;
    private View v;
    private EditText correo;
    private EditText user;
    private EditText password;
    private EditText confirmPassword;
    private EditText dni;
    private FirebaseAuth mAuth;
    private FirebaseDataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_registro, container, false);
        dataManager = FirebaseDataManager.getInstance(); // Inicializa FirebaseDataManager
        mAuth = FirebaseAuth.getInstance();  // Inicializa FirebaseAuth aquí

        registro = v.findViewById(R.id.bRegistroR);
        correo = v.findViewById(R.id.correoR);
        user = v.findViewById(R.id.usuarioR);
        password = v.findViewById(R.id.contrasenaR);
        confirmPassword = v.findViewById(R.id.repiteContrasenaR);
        dni = v.findViewById(R.id.dniR);

        registro.setOnClickListener(view -> {
            String correoText = correo.getText().toString().trim();
            String passwordText = password.getText().toString();
            String confirmPasswordText = confirmPassword.getText().toString();
            String dniText = dni.getText().toString().trim();
            String userText = user.getText().toString();

            // Validación de entrada
            if (correoText.isEmpty() || !isValidEmail(correoText)) {
                Toasty.error(requireContext(), "Por favor, introduce un correo electrónico válido.", Toast.LENGTH_SHORT, true).show();
                return;
            }

            if (passwordText.isEmpty() || passwordText.length() < 6) {
                Toasty.error(requireContext(), "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT, true).show();
                return;
            }

            if (!passwordText.equals(confirmPasswordText)) {
                Toasty.error(requireContext(), "Error, las contraseñas no coinciden", Toast.LENGTH_SHORT, true).show();
                return;
            }

            if (dniText.isEmpty() || !isValidDNI(dniText)) {
                Toasty.error(requireContext(), "Por favor, introduce un DNI válido.", Toast.LENGTH_SHORT, true).show();
                return;
            }

            // Deshabilitar el botón durante el proceso de registro
            registro.setEnabled(false);

            // Verificar si el DNI ya está registrado en la base de datos
            dataManager.verificarYRegistrarUsuario(dniText, userText, correoText, new FirebaseDataManager.OnDniCheckListener() {
                @Override
                public void onDniExist() {
                    Toasty.error(requireContext(), "Este DNI ya está registrado.", Toast.LENGTH_SHORT, true).show();
                    registro.setEnabled(true);
                }

                @Override
                public void onDniDoesNotExist() {
                    mAuth.createUserWithEmailAndPassword(correoText, passwordText)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Log.i("Registro", "✅ Usuario registrado en Firebase");

                                        Toasty.success(requireContext(), "Usuario creado. Guardando datos...", Toast.LENGTH_SHORT, true).show();

                                        FirebaseDataManager.Usuario usuario = new FirebaseDataManager.Usuario(userText, correoText, dniText);
                                        dataManager.insertarUsuario(user.getUid(), usuario, new FirebaseDataManager.OnUserInsertedListener() {
                                            @Override
                                            public void onSuccess() {
                                                FirebaseDatabase.getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
                                                        .getReference("usuariosPorUid").child(user.getUid()).setValue(dniText);

                                                SharedPreferences prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                prefs.edit().putString("dni", dniText).apply();

                                                Toasty.success(requireContext(), "Registro completado. Ahora inicia sesión.", Toast.LENGTH_SHORT, true).show();
                                                NavController navController = Navigation.findNavController(v);
                                                navController.navigate(R.id.fragmentInicioSes);
                                            }

                                            @Override
                                            public void onFailure(String errorMessage) {
                                                Toasty.error(requireContext(), "Error al guardar los datos del usuario: " + errorMessage, Toast.LENGTH_SHORT, true).show();
                                            }
                                        });
                                    }
                                } else {
                                    Toasty.error(requireContext(), "Error el DNI puede estar ya registrado", Toast.LENGTH_SHORT, true).show();
                                }
                                registro.setEnabled(true);
                            });
                }
            });
        });

        return v;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidDNI(String dni) {
        if (dni.length() != 9) return false;
        String numeros = dni.substring(0, 8);
        if (!numeros.matches("[0-9]+")) return false;
        char letra = dni.charAt(8);
        if (!Character.isLetter(letra)) return false;
        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
        int numero = Integer.parseInt(numeros);
        char letraCalculada = letras[numero % 23];
        return Character.toUpperCase(letra) == letraCalculada;
    }
}
