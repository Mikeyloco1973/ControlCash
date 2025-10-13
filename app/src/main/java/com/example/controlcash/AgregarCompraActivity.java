package com.example.controlcash;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarCompraActivity extends AppCompatActivity {

    private EditText etNombre, etPrecio, etCantidad, etDescuento;
    private Switch switchDescuento;
    private TextView tvSubtotal, tvTotal;
    private Button btnCalcular, btnGuardar;
    private SQLite dbHelper;

    private double subtotal = 0;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_compra);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etCantidad = findViewById(R.id.etCantidad);
        etDescuento = findViewById(R.id.etDescuento);
        switchDescuento = findViewById(R.id.switchDescuento);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnGuardar = findViewById(R.id.btnGuardar);

        dbHelper = new SQLite(this);

        switchDescuento.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etDescuento.setVisibility(isChecked ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        btnCalcular.setOnClickListener(v -> calcularCompra());
        btnGuardar.setOnClickListener(v -> guardarCompra());
    }

    private void calcularCompra() {
        String precioStr = etPrecio.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String descuentoStr = etDescuento.getText().toString().trim();

        if (precioStr.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Completa precio y cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int cantidad = Integer.parseInt(cantidadStr);
        subtotal = precio * cantidad;

        double descuento = switchDescuento.isChecked() && !descuentoStr.isEmpty()
                ? Double.parseDouble(descuentoStr)
                : 0;

        if (descuento > 100) {
            Toast.makeText(this, "El descuento no puede ser mayor a 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        total = subtotal * (1 - descuento / 100);

        tvSubtotal.setText("Subtotal: $" + String.format("%.2f", subtotal));
        tvTotal.setText("Total: $" + String.format("%.2f", total));
        tvSubtotal.setVisibility(android.view.View.VISIBLE);
        tvTotal.setVisibility(android.view.View.VISIBLE);
    }

    private void guardarCompra() {
        String nombre = etNombre.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String descuentoStr = etDescuento.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int cantidad = Integer.parseInt(cantidadStr);
        double subtotal = precio * cantidad;

        double descuento = switchDescuento.isChecked() && !descuentoStr.isEmpty()
                ? Double.parseDouble(descuentoStr)
                : 0;

        if (descuento > 100) {
            Toast.makeText(this, "El descuento no puede ser mayor a 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = subtotal * (1 - descuento / 100);

        boolean insertado = dbHelper.insertarCompra(nombre, precio, cantidad, subtotal, descuento, total);

        if (insertado) {
            Toast.makeText(this, "Compra guardada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}
