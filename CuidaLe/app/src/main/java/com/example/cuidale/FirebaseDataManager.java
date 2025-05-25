package com.example.cuidale;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataManager {
    private static FirebaseDataManager instancia;
    private DatabaseReference databaseReference;

    // Constructor privado (Singleton)
    private FirebaseDataManager() {
        String databaseUrl = "https://cuidale-default-rtdb.europe-west1.firebasedatabase.app";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        databaseReference = database.getReference("usuarios");
    }

    // Método para obtener la única instancia de la clase
    public static synchronized FirebaseDataManager getInstance() {
        if (instancia == null) {
            instancia = new FirebaseDataManager();
        }
        return instancia;
    }

    // Método para verificar si el DNI existe y proceder con la inserción
    public void verificarYRegistrarUsuario(String dni, String nombre, String correo, OnDniCheckListener listener) {
        // Verificar si el DNI ya existe
        databaseReference.child(dni).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Si ya existe un usuario con el mismo DNI
                if (dataSnapshot.exists()) {
                    listener.onDniExist();  // Callback para indicar que el DNI ya existe
                } else {
                    // El DNI no existe, proceder con la inserción
                    listener.onDniDoesNotExist();  // Callback cuando el DNI no existe y se debe registrar el usuario
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // En caso de error al consultar la base de datos
                Log.e("FirebaseDataManager", "❌ Error al verificar el DNI: " + databaseError.getMessage());
            }
        });
    }

    // Método para insertar los datos del usuario en Firebase Database
    public void insertarUsuario(String id, Usuario usuario, OnUserInsertedListener listener) {
        // Intentar insertar el usuario en la base de datos
        databaseReference.child(id).setValue(usuario)
                .addOnSuccessListener(aVoid -> {
                    // Si la inserción es exitosa
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Si la inserción falla
                    listener.onFailure(e.getMessage());
                });
    }

    // Interfaz para manejar el callback cuando el DNI ya existe
    public interface OnDniCheckListener {
        void onDniExist();
        void onDniDoesNotExist();  // Callback cuando el DNI no existe y se ha registrado al usuario
    }

    // Interfaz para manejar el resultado de la inserción del usuario
    public interface OnUserInsertedListener {
        void onSuccess();  // Cuando la inserción es exitosa
        void onFailure(String errorMessage);  // Cuando hay un error al insertar los datos
    }

    public void guardarRecordatorio(Recordatorio recordatorio) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recordatoriosRef = databaseReference.child(uid).child("recordatorios");

        String key = recordatoriosRef.push().getKey();
        recordatorio.setId(key);  // Asignamos el id generado por Firebase

        if (key != null) {
            recordatoriosRef.child(key).setValue(recordatorio)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDataManager", "✅ Recordatorio guardado con ID"))
                    .addOnFailureListener(e -> Log.e("FirebaseDataManager", "❌ Error al guardar recordatorio: " + e.getMessage()));
        } else {
            Log.e("FirebaseDataManager", "❌ No se pudo generar la clave para el recordatorio");
        }
    }

    public interface OnRecordatoriosCargadosListener {
        void onCargados(List<Recordatorio> recordatorios);
        void onError(String error);
    }

    public void cargarRecordatorios(OnRecordatoriosCargadosListener listener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference recordatoriosRef = databaseReference.child(uid).child("recordatorios");

        recordatoriosRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Recordatorio> lista = new ArrayList<>();
                DataSnapshot snapshot = task.getResult();

                for (DataSnapshot recordatorioSnap : snapshot.getChildren()) {
                    Recordatorio recordatorio = recordatorioSnap.getValue(Recordatorio.class);
                    if (recordatorio != null) {
                        recordatorio.setId(recordatorioSnap.getKey());  // Asignar ID Firebase
                        lista.add(recordatorio);
                    }
                }
                listener.onCargados(lista);
            } else {
                listener.onError("Error al cargar recordatorios");
            }
        });
    }


    public void eliminarRecordatorio(String idRecordatorio, OnRecordatorioEliminadoListener listener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference recordatorioRef = databaseReference.child(uid).child("recordatorios").child(idRecordatorio);

        recordatorioRef.removeValue()
                .addOnSuccessListener(aVoid -> listener.onEliminado())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }


    public interface OnRecordatorioEliminadoListener {
        void onEliminado();
        void onError(String error);
    }

    // Clase interna para estructurar los datos
    public static class Usuario {
        public String nombre;
        public String correo;
        public String dni;

        public Usuario() {
            // Constructor vacío necesario para Firebase
        }

        public Usuario(String nombre, String correo, String dni) {
            this.nombre = nombre;
            this.correo = correo;
            this.dni = dni;
        }
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
