package com.example.cuidale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private Button bIni;
    private Button bRegi;
    private EditText usu;
    private EditText pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bIni = findViewById(R.id.bInicio);
        bRegi = findViewById(R.id.bRegistro);
        usu = findViewById(R.id.usuario);
        pass =findViewById(R.id.contrasena);
    }

    public void pantallaRegistro(View v){
        Intent intent = new Intent(MainActivity.this, Registro.class);
        startActivity(intent);
    }
    public void inicioSesion(View v){
        String usuario = usu.getText().toString();
        String contrasena = pass.getText().toString();


        if(usuario.isEmpty() || contrasena.isEmpty()){
            Toasty.error(this, getString(R.string.errorVacioIS)).show();
        }else{
            Intent intentp = new Intent(MainActivity.this, pantallaPrincip.class);
            startActivity(intentp);

        }
    }
}
