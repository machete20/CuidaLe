package com.example.cuidale;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import es.dmoral.toasty.Toasty;

public class Registro extends AppCompatActivity {

    private EditText correo;
    private EditText usuario;
    private EditText contrasena;
    private EditText repiteContra;
    private EditText dni;
    private Button bRegistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        correo = findViewById(R.id.correoR);
        usuario = findViewById(R.id.usuarioR);
        contrasena = findViewById(R.id.contrasenaR);
        repiteContra = findViewById(R.id.repiteContrasenaR);
        dni = findViewById(R.id.dniR);
        bRegistro = findViewById(R.id.bRegistroR);
    }

    public void registrarme(View v){
        String cor = correo.getText().toString();
        String usu = usuario.getText().toString();
        String contra = contrasena.getText().toString();
        String repite = repiteContra.getText().toString();
        String dn = dni.getText().toString();

        if(cor.isEmpty() || usu.isEmpty() || contra.isEmpty() || repite.isEmpty() || dn.isEmpty()){
            Toasty.error(this, getString(R.string.errorVacioR)).show();
        }else{
            if(!contra.equalsIgnoreCase(repite)){
                Toasty.error(this, getString(R.string.errorContra)).show();
            }
        }

    }

}