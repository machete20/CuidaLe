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
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FragmentRecordatorios extends Fragment {

    private View v;
    private List<Recordatorio> listaRecordatorios = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_recordatorios, container, false);

        ImageButton addButton = v.findViewById(R.id.addButtonRecordatorios);
        addButton.setOnClickListener(view -> mostrarDialogoNuevoRecordatorio());

        cargarRecordatoriosDesdePreferences();

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
        EditText inputNombre = dialogView.findViewById(R.id.inputNombre);

        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .build();

        timePicker.show(getParentFragmentManager(), "time_picker");

        timePicker.addOnPositiveButtonClickListener(view -> inputNombre.setEnabled(true));
        timePicker.addOnNegativeButtonClickListener(view -> Toast.makeText(getContext(), "Selección de hora cancelada", Toast.LENGTH_SHORT).show());

        new AlertDialog.Builder(getContext())
                .setTitle("Nuevo Recordatorio")
                .setView(dialogView)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    String nombre = inputNombre.getText().toString().trim();
                    String horaFormateada = String.format("%02d:%02d", timePicker.getHour(), timePicker.getMinute());

                    if (nombre.isEmpty() || horaFormateada.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor completa ambos campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Recordatorio nuevoRecordatorio = new Recordatorio(nombre, horaFormateada);
                    listaRecordatorios.add(nuevoRecordatorio);
                    guardarRecordatorioEnPreferences(nuevoRecordatorio);
                    Collections.sort(listaRecordatorios, (r1, r2) -> r1.getHora().compareTo(r2.getHora()));
                    actualizarListaRecordatorios(inflater);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarRecordatorioEnPreferences(Recordatorio recordatorio) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("recordatorios_pref", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> recordatoriosSet = new HashSet<>(sharedPreferences.getStringSet("recordatorios", new HashSet<>()));
        String nuevoRecordatorio = recordatorio.getNombre() + "|||" + recordatorio.getHora();
        recordatoriosSet.add(nuevoRecordatorio);

        editor.putStringSet("recordatorios", recordatoriosSet);
        editor.apply();
    }

    private void eliminarRecordatorioDePreferences(Recordatorio recordatorio) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("recordatorios_pref", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> recordatoriosSet = new HashSet<>(sharedPreferences.getStringSet("recordatorios", new HashSet<>()));
        String clave = recordatorio.getNombre() + "|||" + recordatorio.getHora();

        if (recordatoriosSet.contains(clave)) {
            recordatoriosSet.remove(clave);
            editor.remove("recordatorios");
            editor.apply();  // aplicar eliminación
            editor.putStringSet("recordatorios", recordatoriosSet);
            editor.apply();  // aplicar nuevo set
        }
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
        eliminarRecordatorioDePreferences(recordatorio);
        actualizarListaRecordatorios(LayoutInflater.from(getContext()));
        Toast.makeText(getContext(), "Recordatorio eliminado", Toast.LENGTH_SHORT).show();
    }

    private void cargarRecordatoriosDesdePreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("recordatorios_pref", getContext().MODE_PRIVATE);
        Set<String> recordatoriosSet = sharedPreferences.getStringSet("recordatorios", new HashSet<>());
        listaRecordatorios.clear();

        for (String recordatorioString : recordatoriosSet) {
            String[] partes = recordatorioString.split("\\|\\|\\|");
            if (partes.length == 2) {
                listaRecordatorios.add(new Recordatorio(partes[0], partes[1]));
            }
        }

        Collections.sort(listaRecordatorios, (r1, r2) -> r1.getHora().compareTo(r2.getHora()));
        actualizarListaRecordatorios(LayoutInflater.from(getContext()));
    }

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
            // Para versiones anteriores a Android 12, se puede programar la alarma directamente
            realizarProgramacionAlarma(recordatorio);
        }
    }


    // Método para realizar la programación de la alarma
    @SuppressLint({"ScheduleExactAlarm", "MissingPermission"})
    private void realizarProgramacionAlarma(Recordatorio recordatorio) {
        String[] partes = recordatorio.getHora().split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        // Crear una instancia del calendario
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // Establecer la hora y los minutos a la hora configurada por el usuario
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hora);
        calendar.set(java.util.Calendar.MINUTE, minuto);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);  // Asegurarse de que los milisegundos también estén a cero

        // Si la hora ya pasó para hoy, establecer la alarma para mañana
        if (calendar.before(java.util.Calendar.getInstance())) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        // Crear la Intent para la alarma
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("mensaje", recordatorio.getNombre());

        // Crear el PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                recordatorio.getNombre().hashCode(),  // ID único para cada recordatorio
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Obtener el servicio de AlarmManager
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        // Programar la alarma con la hora exacta que se ha calculado
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),                  // Hora de inicio
                AlarmManager.INTERVAL_DAY,                  // Repetir cada día
                pendingIntent
        );

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "canal_recordatorios")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Recordatorio de: " + recordatorio.getNombre())
                .setContentText("No te olvides de: " + recordatorio.getNombre())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setWhen(calendar.getTimeInMillis());  // Establece la hora cuando se va a mostrar la notificación

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(recordatorio.getNombre().hashCode(), builder.build());

        // Mostrar un mensaje de confirmación
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

    private void crearNotificacion(Recordatorio recordatorio) {
        // Verificar si tenemos el permiso para emitir notificaciones en Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (requireActivity().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permiso para notificaciones no concedido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = new Intent(getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "canal_recordatorios")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Recordatorio: " + recordatorio.getNombre())
                .setContentText("Es hora de tu recordatorio: " + recordatorio.getNombre())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis() + getDelayInMillis(recordatorio.getHora()));  // Establece el tiempo de la alarma

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(recordatorio.getNombre().hashCode(), builder.build());
    }

    private long getDelayInMillis(String hora) {
        String[] partes = hora.split(":");
        int horaInt = Integer.parseInt(partes[0]);
        int minutoInt = Integer.parseInt(partes[1]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, horaInt);
        calendar.set(java.util.Calendar.MINUTE, minutoInt);
        calendar.set(java.util.Calendar.SECOND, 0);

        if (calendar.before(java.util.Calendar.getInstance())) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // Si la hora ya pasó, añade un día
        }

        return calendar.getTimeInMillis() - System.currentTimeMillis();  // Devuelve el delay
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

    private void guardarEstadoSwitch(boolean estado) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("estado_switch", estado);  // Guardamos el estado del switch
        editor.apply();
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

}
