package com.example.controlcash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnIniciarSesion, btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        btnIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, IniciarSesionActivity.class);
            startActivity(intent);
        });

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarseActivity.class);
            startActivity(intent);
        });
    }
}
