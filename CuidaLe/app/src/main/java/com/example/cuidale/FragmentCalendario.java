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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

        menu = rootView.findViewById(R.id.menuPastillero);

        menu.setOnClickListener(v -> {
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

                        if (!allGranted) {
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

        // Establecer la fecha actual como seleccionada por defecto (ajustando a 00:00 hora local)
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        selectedDate = today.getTimeInMillis();
        calendarView.setDate(selectedDate, false, true);
        loadEventsForSelectedDate();

        return rootView;
    }



    // Convierte la fecha en un timestamp en milisegundos
    private long getDateInMillis(int year, int month, int dayOfMonth) {
        return new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
    }

    // Guarda un evento en SharedPreferences
    private void saveEventToFirebase(long date, String eventDetails) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDataManager.guardarEvento(userId, date, eventDetails);
    }

    // Carga los eventos desde SharedPreferences para la fecha seleccionada
    private void loadEventsForSelectedDate() {
        if (selectedDate == 0) return;

        containerLayout.removeAllViews();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDataManager.obtenerEventos(userId, selectedDate, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String event = eventSnapshot.getValue(String.class);
                    String eventId = eventSnapshot.getKey();

                    if (event != null && !event.isEmpty()) {
                        agregarEventoAUI(event, eventId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show();
            }
        });
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
                saveEventToFirebase(selectedDate, eventDetails);

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

    private void agregarEventoAUI(String event, String eventId) {
        LinearLayout eventLayout = new LinearLayout(getContext());
        eventLayout.setOrientation(LinearLayout.HORIZONTAL);
        eventLayout.setPadding(0, 16, 0, 16);
        eventLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView eventTextView = new TextView(getContext());
        eventTextView.setText(event);
        eventTextView.setTextSize(16);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(20, 20, 0, 20);
        eventTextView.setLayoutParams(textParams);

        ImageButton deleteButton = new ImageButton(getContext());
        deleteButton.setImageResource(android.R.drawable.ic_menu_delete);
        deleteButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        deleteButton.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDataManager.eliminarEvento(userId, selectedDate, eventId);
            containerLayout.removeView(eventLayout);
            deleteEventFromGoogleCalendar(event, selectedDate);
        });

        eventLayout.addView(eventTextView);
        eventLayout.addView(deleteButton);
        containerLayout.addView(eventLayout);
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

        if (selectedDate == 0) {
            Calendar today = Calendar.getInstance();
            selectedDate = today.getTimeInMillis();
            calendarView.setDate(selectedDate, false, true);
        }

    }
}
