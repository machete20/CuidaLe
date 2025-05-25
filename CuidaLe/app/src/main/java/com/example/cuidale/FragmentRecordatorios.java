package com.example.cuidale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FragmentRecordatorios extends Fragment {

    private View v;
    private List<Recordatorio> listaRecordatorios = new ArrayList<>();

    private ImageButton atras;

    private ImageView menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_recordatorios, container, false);

        menu = v.findViewById(R.id.menuRecordatorios
        );

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        ImageButton addButton = v.findViewById(R.id.addButtonRecordatorios);
        atras = v.findViewById(R.id.btn_retrocesoRecordatorios);
        addButton.setOnClickListener(view -> mostrarDialogoNuevoRecordatorio());

        atras.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        cargarRecordatoriosDesdeFirebase();

        // Verifica si el permiso para notificaciones está concedido
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (requireActivity().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        return v;
    }

    // Maneja la respuesta a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Sin permiso para mostrar notificaciones", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarDialogoNuevoRecordatorio() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_recordatorio_input, null);

        LinearLayout layoutDialogo = dialogView.findViewById(R.id.layoutDialogo);
        layoutDialogo.setVisibility(View.GONE);  // Ocultar inicialmente

        EditText inputNombre = dialogView.findViewById(R.id.inputNombre);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build();

        timePicker.addOnPositiveButtonClickListener(view -> {
            // Mostrar el layout del diálogo
            layoutDialogo.setVisibility(View.VISIBLE);

            // Mostrar el diálogo con los campos de entrada
            new AlertDialog.Builder(getContext())
                    .setTitle("Nuevo Recordatorio")
                    .setView(dialogView)
                    .setPositiveButton("Añadir", (dialog, which) -> {
                        String nombre = inputNombre.getText().toString().trim();
                        String horaFormateada = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());

                        if (nombre.isEmpty()) {
                            Toast.makeText(getContext(), "Por favor completa el nombre", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Recordatorio nuevoRecordatorio = new Recordatorio(nombre, horaFormateada);
                        listaRecordatorios.add(nuevoRecordatorio);
                        guardarRecordatorioEnFirebase(nuevoRecordatorio);
                        Collections.sort(listaRecordatorios, (r1, r2) -> r1.getHora().compareTo(r2.getHora()));
                        actualizarListaRecordatorios(inflater);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        timePicker.addOnNegativeButtonClickListener(view ->
                Toast.makeText(getContext(), "Selección de hora cancelada", Toast.LENGTH_SHORT).show()
        );

        // Mostrar TimePicker al principio
        timePicker.show(getParentFragmentManager(), "time_picker");
    }

    private void guardarRecordatorioEnFirebase(Recordatorio recordatorio) {
        FirebaseDataManager.getInstance().guardarRecordatorio(recordatorio);
    }

    private void actualizarListaRecordatorios(LayoutInflater inflater) {
        LinearLayout lista = v.findViewById(R.id.listaRecordatorios);
        lista.removeAllViews();

        for (Recordatorio recordatorio : listaRecordatorios) {
            View cardView = inflater.inflate(R.layout.recordatorio_item, null);
            TextView textHora = cardView.findViewById(R.id.textoHora);
            TextView textNombre = cardView.findViewById(R.id.textoNombre);
            Switch recordatorioSwitch = cardView.findViewById(R.id.switchRecordatorio);  // Aquí obtenemos el Switch de cada recordatorio
            ImageButton deleteButton = cardView.findViewById(R.id.deleteButton);

            textHora.setText(recordatorio.getHora());
            textNombre.setText(recordatorio.getNombre());

            // Restaurar el estado del Switch desde SharedPreferences
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            boolean estadoSwitch = sharedPreferences.getBoolean("estado_switch_" + recordatorio.getNombre(), false);  // Guardar el estado del switch por recordatorio
            recordatorioSwitch.setChecked(estadoSwitch);

            // Accionar el switch cuando cambia
            recordatorioSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    programarAlarma(recordatorio);
                    Toast.makeText(getContext(), "Recordatorio activado", Toast.LENGTH_SHORT).show();
                } else {
                    cancelarAlarma(recordatorio);
                    Toast.makeText(getContext(), "Recordatorio desactivado", Toast.LENGTH_SHORT).show();
                }

                // Guardar el estado del switch en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("estado_switch_" + recordatorio.getNombre(), isChecked);
                editor.apply();
            });

            deleteButton.setOnClickListener(view -> {
                eliminarRecordatorio(recordatorio);
            });

            lista.addView(cardView);
        }
    }

    private void eliminarRecordatorio(Recordatorio recordatorio) {
        listaRecordatorios.remove(recordatorio);

        FirebaseDataManager.getInstance().eliminarRecordatorio(recordatorio.getId(), new FirebaseDataManager.OnRecordatorioEliminadoListener() {
            @Override
            public void onEliminado() {
                Toast.makeText(getContext(), "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
                cargarRecordatoriosDesdeFirebase(); // Recarga la lista desde Firebase
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarRecordatoriosDesdeFirebase() {
        FirebaseDataManager.getInstance().cargarRecordatorios(new FirebaseDataManager.OnRecordatoriosCargadosListener() {
            @Override
            public void onCargados(List<Recordatorio> recordatorios) {
                listaRecordatorios.clear();
                listaRecordatorios.addAll(recordatorios);
                Collections.sort(listaRecordatorios, (r1, r2) -> r1.getHora().compareTo(r2.getHora()));
                actualizarListaRecordatorios(LayoutInflater.from(getContext()));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para realizar la programación de la alarma
    @SuppressLint({"ScheduleExactAlarm", "MissingPermission"})
    private void realizarProgramacionAlarma(Recordatorio recordatorio) {
        String[] partes = recordatorio.getHora().split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Si la hora ya pasó, programar para mañana
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("mensaje", recordatorio.getNombre());
        intent.putExtra("hora", recordatorio.getHora()); // para reprogramar luego

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                recordatorio.getNombre().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(getContext(), "Alarma programada para " + recordatorio.getHora(), Toast.LENGTH_SHORT).show();
    }

    private void cancelarAlarma(Recordatorio recordatorio) {
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                recordatorio.getNombre().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private boolean isExactAlarmPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            return getContext().getSystemService(AlarmManager.class).canScheduleExactAlarms();
        }
        return true;
    }

    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            requestPermissionLauncher.launch(intent);  // Usamos el launcher en lugar de startActivityForResult
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {  // Código de solicitud único
            if (isExactAlarmPermissionGranted()) {
                // Si el permiso fue concedido, se programa la alarma
                Toast.makeText(getContext(), "Permiso concedido, programando alarma", Toast.LENGTH_SHORT).show();

                for (Recordatorio recordatorio : listaRecordatorios) {
                    programarAlarma(recordatorio);
                }
            } else {
                // Si el permiso no fue concedido
                Toast.makeText(getContext(), "Permiso de alarma exacta no concedido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restaurar el estado de todos los Switches
        actualizarListaRecordatorios(LayoutInflater.from(getContext()));
    }

    private ActivityResultLauncher<Intent> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (isExactAlarmPermissionGranted()) {
                    // Permiso concedido
                    Toast.makeText(getContext(), "Permiso concedido, programando alarma", Toast.LENGTH_SHORT).show();
                    for (Recordatorio recordatorio : listaRecordatorios) {
                        programarAlarma(recordatorio);
                    }
                } else {
                    // Permiso no concedido
                    Toast.makeText(getContext(), "Permiso de alarma exacta no concedido", Toast.LENGTH_SHORT).show();
                }
            });

    private void programarAlarma(Recordatorio recordatorio) {
        // Verificar si es necesario solicitar el permiso de alarmas exactas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isExactAlarmPermissionGranted()) {
                // Si el permiso no está concedido, solicita el permiso
                requestExactAlarmPermission();
            } else {
                // Si el permiso ya está concedido, programar la alarma
                realizarProgramacionAlarma(recordatorio);
            }
        } else {
            realizarProgramacionAlarma(recordatorio);
        }
    }
}
