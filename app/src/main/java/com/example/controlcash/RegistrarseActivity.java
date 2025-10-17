package com.example.controlcash;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrarseActivity extends AppCompatActivity {

    private EditText etNombre, etCorreo, etPassword, etRepeatPassword, etTelefono;
    private ImageButton btnTogglePassword, btnToggleRepeat;
    private Button btnRegistrarse;
    private boolean mostrarPassword = false;
    private boolean mostrarRepeat = false;
    private SQLite dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        etTelefono = findViewById(R.id.etTelefono);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnToggleRepeat = findViewById(R.id.btnToggleRepeat);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        dbHelper = new SQLite(this);

        btnTogglePassword.setOnClickListener(v -> {
            mostrarPassword = !mostrarPassword;
            etPassword.setInputType(mostrarPassword ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnToggleRepeat.setOnClickListener(v -> {
            mostrarRepeat = !mostrarRepeat;
            etRepeatPassword.setInputType(mostrarRepeat ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etRepeatPassword.setSelection(etRepeatPassword.getText().length());
        });

        btnRegistrarse.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repetir = etRepeatPassword.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || repetir.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repetir)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telefono.matches("\\d{8,15}")) {
            Toast.makeText(this, "Número telefónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el correo ya existe
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE correo = ?", new String[]{correo});
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        cursor.close();

        // Insertar usuario
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("correo", correo);
        values.put("password", password);
        values.put("telefono", telefono);

        long resultado = db.insert("usuarios", null, values);
        if (resultado != -1) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, IniciarSesionActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show();
        }
    }
}
