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
    private static final DatabaseReference usuariosRef = FirebaseDatabase
            .getInstance("https://cuidale-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("usuarios");


    // Constructor privado (Singleton)
    private FirebaseDataManager() {
        String databaseUrl = "https://cuidale-default-rtdb.europe-west1.firebasedatabase.app";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
    }

    public static synchronized FirebaseDataManager getInstance() {
        if (instancia == null) {
            instancia = new FirebaseDataManager();
        }
        return instancia;
    }

    // === USUARIOS ===

    public void verificarYRegistrarUsuario(String dni, String nombre, String correo, OnDniCheckListener listener) {
        usuariosRef.child(dni).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listener.onDniExist();
                } else {
                    listener.onDniDoesNotExist();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseDataManager", "❌ Error al verificar el DNI: " + error.getMessage());
            }
        });
    }

    public void insertarUsuario(String id, Usuario usuario, OnUserInsertedListener listener) {
        usuariosRef.child(id).setValue(usuario)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }



    public interface OnDniCheckListener {
        void onDniExist();
        void onDniDoesNotExist();
    }

    public interface OnUserInsertedListener {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // === RECORDATORIOS ===

    public void guardarRecordatorio(Recordatorio recordatorio) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = usuariosRef.child(uid).child("recordatorios");
        String key = ref.push().getKey();
        if (key != null) {
            recordatorio.setId(key);
            ref.child(key).setValue(recordatorio)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDataManager", "✅ Recordatorio guardado"))
                    .addOnFailureListener(e -> Log.e("FirebaseDataManager", "❌ Error: " + e.getMessage()));
        } else {
            Log.e("FirebaseDataManager", "❌ Error generando clave para recordatorio");
        }
    }

    public void cargarRecordatorios(OnRecordatoriosCargadosListener listener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usuariosRef.child(uid).child("recordatorios").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Recordatorio> lista = new ArrayList<>();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Recordatorio r = snap.getValue(Recordatorio.class);
                    if (r != null) {
                        r.setId(snap.getKey());
                        lista.add(r);
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
        usuariosRef.child(uid).child("recordatorios").child(idRecordatorio).removeValue()
                .addOnSuccessListener(aVoid -> listener.onEliminado())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    public interface OnRecordatoriosCargadosListener {
        void onCargados(List<Recordatorio> recordatorios);
        void onError(String error);
    }

    public interface OnRecordatorioEliminadoListener {
        void onEliminado();
        void onError(String error);
    }

    // === EVENTOS DE CALENDARIO ===

    public static void guardarEvento(String userId, long fecha, String evento) {
        usuariosRef.child(userId).child(String.valueOf(fecha)).push().setValue(evento);
    }

    public static void eliminarEvento(String userId, long fecha, String eventoId) {
        usuariosRef.child(userId).child(String.valueOf(fecha)).child(eventoId).removeValue();
    }

    public static void obtenerEventos(String userId, long fecha, ValueEventListener listener) {
        usuariosRef.child(userId).child(String.valueOf(fecha)).addListenerForSingleValueEvent(listener);
    }


    // === MODELO DE USUARIO ===

    public static class Usuario {
        public String nombre;
        public String correo;
        public String dni;

        public Usuario() {} // Requerido por Firebase

        public Usuario(String nombre, String correo, String dni) {
            this.nombre = nombre;
            this.correo = correo;
            this.dni = dni;
        }
    }



    // === PACIENTES ===

    public void guardarPaciente(Paciente pacientes) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = usuariosRef.child(uid).child("pacientes");
        String key = ref.push().getKey();
        if (key != null) {
            pacientes.setId(key);
            ref.child(key).setValue(pacientes)
                    .addOnSuccessListener(aVoid -> Log.d("FirebaseDataManager", "✅ Paciente guardado"))
                    .addOnFailureListener(e -> Log.e("FirebaseDataManager", "❌ Error: " + e.getMessage()));
        } else {
            Log.e("FirebaseDataManager", "❌ Error generando clave para paciente");
        }
    }
}
