package com.example.cuidale;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    public AuthManager(Context context) {
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    // Método para iniciar sesión
    public void iniciarSesion(String email, String password, final AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveSession(true); // Guardar estado de sesión
                        callback.onSuccess(user);
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidUserException) {
                            callback.onError("Usuario no encontrado");
                        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            callback.onError("Credenciales inválidas");
                        } else {
                            callback.onError("Error desconocido: " + exception.getMessage());
                        }
                    }
                });
    }

    // Método para registrar un nuevo usuario
    public void registrarUsuario(String email, String password, final AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveSession(true); // Guardar estado de sesión
                        callback.onSuccess(user);
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            callback.onError("Error al registrar: " + exception.getMessage());
                        } else {
                            callback.onError("Error desconocido");
                        }
                    }
                });
    }

    // Método para cerrar sesión
    public void cerrarSesion() {
        mAuth.signOut();
        saveSession(false); // Actualizar estado de sesión al cerrar
    }

    // Guardar el estado de sesión en SharedPreferences
    private void saveSession(boolean isLoggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    // Verificar si el usuario está autenticado
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    // Método para obtener el usuario actual autenticado
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Interfaz de callback para manejar los resultados de la autenticación
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }
}
