package com.example.controlcash;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "compras.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_COMPRAS = "compras";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_PRECIO = "precio";
    public static final String COLUMN_CANTIDAD = "cantidad";
    public static final String COLUMN_SUBTOTAL = "subtotal";
    public static final String COLUMN_DESCUENTO = "descuento";
    public static final String COLUMN_TOTAL = "total";

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_COMPRAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_PRECIO + " REAL, " +
                COLUMN_CANTIDAD + " INTEGER, " +
                COLUMN_SUBTOTAL + " REAL, " +
                COLUMN_DESCUENTO + " REAL, " +
                COLUMN_TOTAL + " REAL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS);
        onCreate(db);
    }

    public boolean insertarCompra(String nombre, double precio, int cantidad, double subtotal, double descuento, double total) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_CANTIDAD, cantidad);
        values.put(COLUMN_SUBTOTAL, subtotal);
        values.put(COLUMN_DESCUENTO, descuento);
        values.put(COLUMN_TOTAL, total);

        long resultado = db.insert(TABLE_COMPRAS, null, values);
        return resultado != -1;
    }
}
