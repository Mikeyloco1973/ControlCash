package com.example.controlcash;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class EditarCompraActivity extends AppCompatActivity {

    private EditText etNombre, etPrecio, etCantidad, etDescuento;
    private Switch switchDescuento;
    private TextView tvSubtotal, tvTotal;
    private Button btnCalcular, btnActualizar;
    private SQLite dbHelper;
    private int compraId;

    private GoogleMap mMap;
    private LatLng ubicacionCompra;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_compra);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etCantidad = findViewById(R.id.etCantidad);
        etDescuento = findViewById(R.id.etDescuento);
        switchDescuento = findViewById(R.id.switchDescuento);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnActualizar = findViewById(R.id.btnActualizar);

        dbHelper = new SQLite(this);
        compraId = getIntent().getIntExtra("id", -1);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        cargarCompra();

        switchDescuento.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etDescuento.setVisibility(isChecked ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        btnCalcular.setOnClickListener(v -> calcularCompra());
        btnActualizar.setOnClickListener(v -> actualizarCompra());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                mostrarUbicacionEnMapa();
                mMap.setOnMapClickListener(latLng -> {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación editada"));
                    ubicacionCompra = latLng;
                });
            });
        }
    }

    private void cargarCompra() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLite.TABLE_COMPRAS + " WHERE id = ?", new String[]{String.valueOf(compraId)});
        if (cursor.moveToFirst()) {
            etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_NOMBRE)));
            etPrecio.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_PRECIO))));
            etCantidad.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(SQLite.COLUMN_CANTIDAD))));
            double descuento = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_DESCUENTO));
            if (descuento > 0) {
                switchDescuento.setChecked(true);
                etDescuento.setVisibility(android.view.View.VISIBLE);
                etDescuento.setText(String.valueOf(descuento));
            }

            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_LATITUD));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_LONGITUD));
            ubicacionCompra = new LatLng(lat, lng);
        }
        cursor.close();
    }

    private void mostrarUbicacionEnMapa() {
        if (ubicacionCompra != null && mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCompra, 15));
            mMap.addMarker(new MarkerOptions().position(ubicacionCompra).title("Ubicación guardada"));
        } else {
            obtenerUbicacionActual();
        }
    }

    private void obtenerUbicacionActual() {
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
            obtenerUbicacionActual();
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
        double subtotal = precio * cantidad;
        double descuento = switchDescuento.isChecked() && !descuentoStr.isEmpty()
                ? Double.parseDouble(descuentoStr)
                : 0;

        if (descuento > 100) {
            Toast.makeText(this, "El descuento no puede ser mayor a 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = subtotal * (1 - descuento / 100);

        tvSubtotal.setText("Subtotal: $" + String.format("%.2f", subtotal));
        tvTotal.setText("Total: $" + String.format("%.2f", total));
        tvSubtotal.setVisibility(android.view.View.VISIBLE);
        tvTotal.setVisibility(android.view.View.VISIBLE);
    }

    private void actualizarCompra() {
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SQLite.COLUMN_NOMBRE, nombre);
        values.put(SQLite.COLUMN_PRECIO, precio);
        values.put(SQLite.COLUMN_CANTIDAD, cantidad);
        values.put(SQLite.COLUMN_SUBTOTAL, subtotal);
        values.put(SQLite.COLUMN_DESCUENTO, descuento);
        values.put(SQLite.COLUMN_TOTAL, total);
        values.put(SQLite.COLUMN_LATITUD, latitud);
        values.put(SQLite.COLUMN_LONGITUD, longitud);

        int result = db.update(SQLite.TABLE_COMPRAS, values, "id = ?", new String[]{String.valueOf(compraId)});
        if (result > 0) {
            Toast.makeText(this, "Compra actualizada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }
}
