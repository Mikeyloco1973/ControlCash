package com.example.controlcash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AgregarCompraActivity extends AppCompatActivity {

    private EditText etNombre, etPrecio, etCantidad, etDescuento;
    private Switch switchDescuento;
    private TextView tvSubtotal, tvTotal;
    private Button btnCalcular, btnGuardar;
    private SQLite dbHelper;

    private double subtotal = 0;
    private double total = 0;

    private GoogleMap mMap;
    private LatLng ubicacionCompra;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        switchDescuento.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etDescuento.setVisibility(isChecked ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        btnCalcular.setOnClickListener(v -> calcularCompra());
        btnGuardar.setOnClickListener(v -> guardarCompra());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mostrarUbicacionActual();
                mMap.setOnMapClickListener(latLng -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación de compra"));
                    ubicacionCompra = latLng;
                });
            });
        }
    }

    private void mostrarUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng actual = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(actual, 15));
                mMap.addMarker(new MarkerOptions().position(actual).title("Ubicación actual"));
                ubicacionCompra = actual;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarUbicacionActual();
        }
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

        if (ubicacionCompra == null) {
            Toast.makeText(this, "Selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show();
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
        double latitud = ubicacionCompra.latitude;
        double longitud = ubicacionCompra.longitude;

        // ✅ Obtener usuario logueado
        String usuario = getSharedPreferences("sesion", MODE_PRIVATE).getString("usuario", null);
        if (usuario == null) {
            Toast.makeText(this, "Sesión no válida. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Guardar compra asociada al usuario
        boolean insertado = dbHelper.insertarCompra(nombre, precio, cantidad, subtotal, descuento, total, latitud, longitud, usuario);

        if (insertado) {
            Toast.makeText(this, "Compra guardada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}
