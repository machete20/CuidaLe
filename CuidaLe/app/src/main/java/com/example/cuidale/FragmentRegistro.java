package com.example.cuidale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class FragmentRegistro extends Fragment {

    private Button registro;
    private View v;
    private EditText correo;
    private EditText user;
    private EditText password;
    private EditText confirmPassword;
    private EditText dni;
    private AuthManager manager;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_registro, container, false);
        manager = new AuthManager(requireContext()); // Pasar el contexto
        mAuth = FirebaseAuth.getInstance(); // Instanciar FirebaseAuth

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

            // Intentar registrar usuario
            mAuth.createUserWithEmailAndPassword(correoText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // El registro fue exitoso
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toasty.success(requireContext(), "Registro completado. Ahora inicia sesión.", Toast.LENGTH_SHORT, true).show();
                            NavController navController = Navigation.findNavController(v);
                            navController.navigate(R.id.fragmentInicioSes); // Navegar a la pantalla de inicio de sesión
                        } else {
                            // Si el correo ya está registrado, manejar el error
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                // El correo ya está registrado
                                Toasty.error(requireContext(), "Este correo ya está registrado.", Toast.LENGTH_SHORT, true).show();
                            } else {
                                // Cualquier otro error
                                Toasty.error(requireContext(), "Error al crear la cuenta: " + task.getException().getMessage(), Toast.LENGTH_SHORT, true).show();
                            }
                            // Rehabilitar el botón en caso de error
                            registro.setEnabled(true);
                        }
                    });
        });

        return v;
    }

    // Método para validar el formato del correo electrónico
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método para validar el formato del DNI
    private boolean isValidDNI(String dni) {
        // Verificar que el DNI tenga 9 caracteres
        if (dni.length() != 9) {
            return false;
        }

        // Verificar que los primeros 8 caracteres sean números
        String numeros = dni.substring(0, 8);
        if (!numeros.matches("[0-9]+")) {
            return false;
        }

        // Verificar que el último carácter sea una letra
        char letra = dni.charAt(8);
        if (!Character.isLetter(letra)) {
            return false;
        }

        // Lista de letras que corresponden a los números del 0 al 22 (cálculo del DNI español)
        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};

        // Convertir los primeros 8 caracteres a un número entero
        int numero = Integer.parseInt(numeros);

        // Calcular la letra correspondiente
        char letraCalculada = letras[numero % 23];

        // Verificar si la letra calculada es la misma que la letra final del DNI
        return Character.toUpperCase(letra) == letraCalculada;
    }

}
