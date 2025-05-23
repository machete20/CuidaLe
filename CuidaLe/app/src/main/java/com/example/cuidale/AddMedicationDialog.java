package com.example.cuidale;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class AddMedicationDialog extends DialogFragment {

    public interface OnMedicationAddedListener {
        void onMedicationAdded(Medication medicamento);
    }

    private OnMedicationAddedListener listener;

    public AddMedicationDialog(OnMedicationAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_medicine, null);

        EditText nombreInput = view.findViewById(R.id.editNombre);
        EditText horaInput = view.findViewById(R.id.editHora);

        horaInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (TimePicker view1, int hourOfDay, int minute1) -> {
                        String horaFinal = String.format("%02d:%02d", hourOfDay, minute1);
                        horaInput.setText(horaFinal);
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(view);

        view.findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String nombre = nombreInput.getText().toString().trim();
            String hora = horaInput.getText().toString().trim();

            if (!nombre.isEmpty() && !hora.isEmpty()) {
                Medication medicamento = new Medication(hora, nombre, false);
                listener.onMedicationAdded(medicamento);
                dismiss();
            }
        });

        return dialog;
    }
}
