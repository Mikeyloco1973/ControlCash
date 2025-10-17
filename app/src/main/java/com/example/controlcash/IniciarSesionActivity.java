package com.example.controlcash;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IniciarSesionActivity extends AppCompatActivity {

    private EditText etNombre, etPassword;
    private ImageButton btnTogglePassword;
    private Button btnIniciarSesion;
    private boolean mostrarPassword = false;
    private SQLite dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        etNombre = findViewById(R.id.etNombre);
        etPassword = findViewById(R.id.etPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        dbHelper = new SQLite(this);

        btnTogglePassword.setOnClickListener(v -> {
            mostrarPassword = !mostrarPassword;
            etPassword.setInputType(mostrarPassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String nombre = etNombre.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nombre.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE nombre = ? AND password = ?", new String[]{nombre, password});

        if (cursor.moveToFirst()) {
            getSharedPreferences("sesion", MODE_PRIVATE).edit().putString("usuario", nombre).apply();
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ListaComprasActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Nombre o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }
}
