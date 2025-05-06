package com.example.cuidale;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void onReceive(Context context, Intent intent) {
        String mensaje = intent.getStringExtra("mensaje");
        String hora = intent.getStringExtra("hora"); // necesario para reprogramar

        // Mostrar Toast
        Toast.makeText(context, "¡Es hora de: " + mensaje + "!", Toast.LENGTH_LONG).show();

        // Crear notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_recordatorios")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Recordatorio de: " + mensaje)
                .setContentText("Es la hora de: " + mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int notificationId = mensaje.hashCode();
        notificationManager.notify(notificationId, builder.build());

        // --- Reprogramar para el día siguiente ---
        String[] partes = hora.split(":");
        int horaInt = Integer.parseInt(partes[0]);
        int minutoInt = Integer.parseInt(partes[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, horaInt);
        calendar.set(Calendar.MINUTE, minutoInt);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Intent newIntent = new Intent(context, AlarmReceiver.class);
        newIntent.putExtra("mensaje", mensaje);
        newIntent.putExtra("hora", hora);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                mensaje.hashCode(),
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }
}

