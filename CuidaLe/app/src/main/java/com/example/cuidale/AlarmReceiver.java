package com.example.cuidale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Mostrar un Toast para confirmar que la alarma se activó
        String mensaje = intent.getStringExtra("mensaje");
        Toast.makeText(context, "¡Es hora de: " + mensaje + "!", Toast.LENGTH_LONG).show();

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_recordatorios")
                .setSmallIcon(R.drawable.logo)  // Cambia el icono según tu diseño
                .setContentTitle("Recordatorio de: " + mensaje)
                .setContentText("Es la hora de: " + mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Obtener el NotificationManager utilizando el contexto
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Verificar si el permiso de notificaciones ha sido concedido
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Si no tenemos el permiso, no podemos mostrar la notificación
            return;
        }

        // Mostrar la notificación
        notificationManager.notify(mensaje.hashCode(), builder.build());
    }
}
