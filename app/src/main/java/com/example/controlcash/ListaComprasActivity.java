package com.example.controlcash;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListaComprasActivity extends AppCompatActivity {

    private TextView tvBienvenida, tvAhorros, tvSubtotal, tvTotal;
    private RecyclerView recyclerCompras;
    private Button btnAgregarCompra;
    private ImageButton btnCerrarSesion;
    private SQLite dbHelper;
    private CompraAdapter adapter;
    private ArrayList<Compra> listaCompras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);

        tvBienvenida = findViewById(R.id.tvBienvenida);
        tvAhorros = findViewById(R.id.tvAhorros);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        recyclerCompras = findViewById(R.id.recyclerCompras);
        btnAgregarCompra = findViewById(R.id.btnAgregarCompra);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        String usuario = getSharedPreferences("sesion", MODE_PRIVATE).getString("usuario", null);
        if (usuario != null) {
            tvBienvenida.setText("Bienvenido, " + usuario);
        } else {
            startActivity(new Intent(this, IniciarSesionActivity.class));
            finish();
        }

        btnAgregarCompra.setOnClickListener(v -> {
            startActivity(new Intent(this, AgregarCompraActivity.class));
        });

        btnCerrarSesion.setOnClickListener(v -> {
            getSharedPreferences("sesion", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        dbHelper = new SQLite(this);
        recyclerCompras.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCompras();
    }

    private void cargarCompras() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLite.TABLE_COMPRAS, null);

        listaCompras = new ArrayList<>();
        double subtotal = 0, total = 0, ahorros = 0;

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLite.COLUMN_ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(SQLite.COLUMN_NOMBRE));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_PRECIO));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(SQLite.COLUMN_CANTIDAD));
            double sub = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_SUBTOTAL));
            double desc = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_DESCUENTO));
            double tot = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLite.COLUMN_TOTAL));

            subtotal += sub;
            total += tot;
            ahorros += (sub - tot);

            listaCompras.add(new Compra(id, nombre, precio, cantidad, sub, desc, tot));
        }

        cursor.close();

        adapter = new CompraAdapter(listaCompras, this::eliminarCompra, this::editarCompra);
        recyclerCompras.setAdapter(adapter);

        tvSubtotal.setText("Subtotal de compras: $" + String.format("%.2f", subtotal));
        tvTotal.setText("Total de compras: $" + String.format("%.2f", total));
        tvAhorros.setText("Ahorros: $" + String.format("%.2f", ahorros));

        configurarSwipeParaEliminar();
    }

    private void eliminarCompra(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = db.delete(SQLite.TABLE_COMPRAS, "id = ?", new String[]{String.valueOf(id)});
        if (result > 0) {
            cargarCompras();
        }
    }

    private void editarCompra(int id) {
        Intent intent = new Intent(this, EditarCompraActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void configurarSwipeParaEliminar() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Compra compra = listaCompras.get(position);
                eliminarCompra(compra.id);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                paint.setColor(Color.RED);

                if (dX < 0) {
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom(), paint);

                    Drawable icon = ContextCompat.getDrawable(ListaComprasActivity.this, R.drawable.ic_delete);
                    if (icon != null) {
                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerCompras);
    }
}
