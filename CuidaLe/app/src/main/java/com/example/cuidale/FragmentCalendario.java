package com.example.cuidale;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultCallback;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class FragmentCalendario extends Fragment {

    private CalendarView calendarView;
    private LinearLayout containerLayout;
    private long selectedDate;

    private ImageButton atras;

    private ImageView menu;
    private androidx.activity.result.ActivityResultLauncher<String[]> requestPermissionLauncher;

    public FragmentCalendario() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout del fragmento
        View rootView = inflater.inflate(R.layout.fragment_calendario, container, false);

        menu = rootView.findViewById(R.id.menuDesplegableCalendario);

        menu.setOnClickListener(v->{
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentMenuDesplegable);
        });

        // Inicializamos el ActivityResultLauncher aquí
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        // Verificamos si los permisos fueron concedidos
                        boolean allGranted = true;
                        for (Boolean isGranted : result.values()) {
                            if (!isGranted) {
                                allGranted = false;
                                break;
                            }
                        }

                        if (allGranted) {

                        } else {
                            Toast.makeText(getContext(), "Permisos denegados", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        checkAndRequestCalendarPermissions(); // Verificamos y solicitamos permisos

        atras = rootView.findViewById(R.id.btn_retrocesoCalendario);

        atras.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.fragmentPantPrinc);
        });

        // Referencias a las vistas
        calendarView = rootView.findViewById(R.id.calendarView);
        containerLayout = rootView.findViewById(R.id.containerLayout);

        // Listener para manejar la selección de fecha
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = getDateInMillis(year, month, dayOfMonth); // Guardar la fecha seleccionada
            loadEventsForSelectedDate(); // Cargar los eventos de la fecha seleccionada
        });

        // Botón para agregar un evento (en la esquina inferior derecha)
        ImageButton addEventButton = rootView.findViewById(R.id.addButtonCalendario);
        addEventButton.setOnClickListener(v -> showEventDialog(selectedDate)); // Mostrar el diálogo de evento cuando se presiona el botón

        return rootView;
    }

    // Convierte la fecha en un timestamp en milisegundos
    private long getDateInMillis(int year, int month, int dayOfMonth) {
        return new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
    }

    // Guarda un evento en SharedPreferences
    private void saveEventToPreferences(long date, String eventDetails) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EventosCalendario", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Recuperar los eventos existentes para esa fecha
        String existingEvents = sharedPreferences.getString(String.valueOf(date), "");

        // Añadir el nuevo evento a los eventos existentes
        String updatedEvents = existingEvents + eventDetails + ";;";

        // Guardar la cadena de eventos para esa fecha
        editor.putString(String.valueOf(date), updatedEvents);
        editor.apply();  // Aplica los cambios
    }

    // Elimina un evento de SharedPreferences y de la vista
    private void deleteEventFromPreferences(long date, String eventDetailsToDelete) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EventosCalendario", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Recuperar los eventos existentes para esa fecha
        String existingEvents = sharedPreferences.getString(String.valueOf(date), "");

        // Eliminar el evento específico de la lista
        String updatedEvents = existingEvents.replace(eventDetailsToDelete + ";;", "");

        // Guardar la lista de eventos actualizada
        editor.putString(String.valueOf(date), updatedEvents);
        editor.apply();  // Aplica los cambios
    }

    // Carga los eventos desde SharedPreferences para la fecha seleccionada
    private void loadEventsForSelectedDate() {
        if (selectedDate == 0) {
            return;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EventosCalendario", getActivity().MODE_PRIVATE);
        String eventString = sharedPreferences.getString(String.valueOf(selectedDate), "");

        // Limpiar los TextViews actuales para evitar duplicados
        containerLayout.removeAllViews();

        // Convertir la cadena de eventos en una lista
        String[] eventArray = eventString.split(";;");

        // Crear un TextView para cada evento y agregarlo al LinearLayout
        for (String event : eventArray) {
            if (!event.isEmpty()) {
                // Crear el contenedor para el evento (LinearLayout horizontal)
                LinearLayout eventLayout = new LinearLayout(getContext());
                eventLayout.setOrientation(LinearLayout.HORIZONTAL);
                eventLayout.setPadding(0, 16, 0, 16);
                eventLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // Crear el TextView con el evento
                TextView eventTextView = new TextView(getContext());
                eventTextView.setText(event);
                eventTextView.setTextSize(16);
                // Establecer márgenes de 20px con el borde de la pantalla
                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f // Esto hace que el TextView ocupe todo el espacio disponible
                );
                int marginInPx = 20; // Márgenes de 20px
                textParams.setMargins(marginInPx, marginInPx, 0, marginInPx); // Establecer márgenes a la izquierda y derecha
                eventTextView.setLayoutParams(textParams);

                // Crear el botón de eliminar
                ImageButton deleteButton = new ImageButton(getContext());
                deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
                deleteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                deleteButton.setLayoutParams(buttonParams);

                // Eliminar evento al hacer clic en el botón
                deleteButton.setOnClickListener(v -> {
                    // Eliminar de SharedPreferences
                    deleteEventFromPreferences(selectedDate, event);

                    // También eliminar de Google Calendar
                    deleteEventFromGoogleCalendar(event, selectedDate);

                    // Eliminar evento de la vista
                    containerLayout.removeView(eventLayout);

                    Toast.makeText(getActivity(), "Evento eliminado", Toast.LENGTH_SHORT).show();
                });

                // Agregar el TextView y el botón de eliminar al layout horizontal
                eventLayout.addView(eventTextView);
                eventLayout.addView(deleteButton);

                // Agregar el layout con el evento a la vista
                containerLayout.addView(eventLayout);
            }
        }
    }

    // Muestra un cuadro de diálogo para ingresar los detalles del evento
    private void showEventDialog(long selectedDate) {
        if (selectedDate == 0) {
            Toast.makeText(getActivity(), "Por favor, selecciona una fecha primero.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea un diálogo para ingresar el evento
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Agregar Evento");

        // Vista para ingresar el evento
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_evento, null);
        EditText eventTitleEditText = dialogView.findViewById(R.id.eventTitleEditText);
        EditText eventDescriptionEditText = dialogView.findViewById(R.id.eventDescriptionEditText);

        builder.setView(dialogView);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String title = eventTitleEditText.getText().toString();
            String description = eventDescriptionEditText.getText().toString();

            if (!title.isEmpty()) {
                String eventDetails = "Evento: " + title + "\nDescripción: " + description;

                // Guardar en SharedPreferences
                saveEventToPreferences(selectedDate, eventDetails);

                // Mostrar en el layout como antes
                loadEventsForSelectedDate(); // Refrescamos la vista de eventos

                Toast.makeText(getActivity(), "Evento agregado", Toast.LENGTH_SHORT).show();

                // ABRIR CALENDARIO DE GOOGLE
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.TITLE, title);
                intent.putExtra(CalendarContract.Events.DESCRIPTION, description);

                // Crear Calendar para el inicio en la zona local
                Calendar startCal = Calendar.getInstance();
                startCal.setTimeInMillis(selectedDate);
                startCal.set(Calendar.HOUR_OF_DAY, 0);
                startCal.set(Calendar.MINUTE, 0);
                startCal.set(Calendar.SECOND, 0);
                startCal.set(Calendar.MILLISECOND, 0);

                // Crear Calendar para el final (día siguiente)
                Calendar endCal = (Calendar) startCal.clone();
                endCal.add(Calendar.DAY_OF_MONTH, 1);

                // Usar esos valores en el intent
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCal.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

                intent.setPackage("com.google.android.calendar"); // Fuerza abrir Google Calendar

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "No se pudo abrir Google Calendar. ¿Está instalado?", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getActivity(), "Debe ingresar un título", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void deleteEventFromGoogleCalendar(String eventDetails, long dateMillis) {
        String titleToDelete = "";
        if (eventDetails.startsWith("Evento: ")) {
            int endIndex = eventDetails.indexOf("\n");
            if (endIndex != -1) {
                titleToDelete = eventDetails.substring(8, endIndex); // Extrae el título
            }
        }

        if (titleToDelete.isEmpty()) return;

        // Rango del día completo
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(dateMillis);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        long startMillis = start.getTimeInMillis();

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 1);
        long endMillis = end.getTimeInMillis();

        String selection = "((title = ?) AND (dtstart >= ?) AND (dtstart < ?))";
        String[] selectionArgs = new String[] {
                titleToDelete,
                Long.toString(startMillis),
                Long.toString(endMillis)
        };

        try {
            int rowsDeleted = getActivity().getContentResolver().delete(
                    CalendarContract.Events.CONTENT_URI,
                    selection,
                    selectionArgs
            );

            if (rowsDeleted > 0) {
                //Toast.makeText(getActivity(), "Evento eliminado de Google Calendar", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getActivity(), "Evento no encontrado en Google Calendar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //Toast.makeText(getActivity(), "Error al eliminar evento de Google Calendar", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAndRequestCalendarPermissions() {
        // Verificar si tenemos los permisos de calendario
        String[] permissions = {
                android.Manifest.permission.READ_CALENDAR,
                android.Manifest.permission.WRITE_CALENDAR
        };

        requestPermissionLauncher.launch(permissions);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadEventsForSelectedDate();  // Cargar eventos desde SharedPreferences para la fecha seleccionada
    }
}
