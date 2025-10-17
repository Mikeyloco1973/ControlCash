package com.example.controlcash;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "compras.db";
    private static final int DATABASE_VERSION = 5; // actualizado para reflejar cambios en la estructura

    // Tabla de compras
    public static final String TABLE_COMPRAS = "compras";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_PRECIO = "precio";
    public static final String COLUMN_CANTIDAD = "cantidad";
    public static final String COLUMN_SUBTOTAL = "subtotal";
    public static final String COLUMN_DESCUENTO = "descuento";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_LATITUD = "latitud";
    public static final String COLUMN_LONGITUD = "longitud";
    public static final String COLUMN_USUARIO = "usuario"; // campo para identificar al dueño de la compra

    // Tabla de usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NOMBRE = "nombre";
    public static final String COLUMN_USER_CORREO = "correo";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_TELEFONO = "telefono";

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COMPRAS = "CREATE TABLE " + TABLE_COMPRAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_PRECIO + " REAL, " +
                COLUMN_CANTIDAD + " INTEGER, " +
                COLUMN_SUBTOTAL + " REAL, " +
                COLUMN_DESCUENTO + " REAL, " +
                COLUMN_TOTAL + " REAL, " +
                COLUMN_LATITUD + " REAL, " +
                COLUMN_LONGITUD + " REAL, " +
                COLUMN_USUARIO + " TEXT)";
        db.execSQL(CREATE_COMPRAS);

        String CREATE_USUARIOS = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NOMBRE + " TEXT, " +
                COLUMN_USER_CORREO + " TEXT, " +
                COLUMN_USER_PASSWORD + " TEXT, " +
                COLUMN_USER_TELEFONO + " TEXT)";
        db.execSQL(CREATE_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina y recrea las tablas si hay cambios en la estructura
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // Inserta una compra con todos los campos, incluyendo el usuario
    public boolean insertarCompra(String nombre, double precio, int cantidad, double subtotal, double descuento, double total, double latitud, double longitud, String usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_CANTIDAD, cantidad);
        values.put(COLUMN_SUBTOTAL, subtotal);
        values.put(COLUMN_DESCUENTO, descuento);
        values.put(COLUMN_TOTAL, total);
        values.put(COLUMN_LATITUD, latitud);
        values.put(COLUMN_LONGITUD, longitud);
        values.put(COLUMN_USUARIO, usuario);

        long resultado = db.insert(TABLE_COMPRAS, null, values);
        return resultado != -1;
    }

    // Inserta un nuevo usuario
    public boolean insertarUsuario(String nombre, String correo, String password, String telefono) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NOMBRE, nombre);
        values.put(COLUMN_USER_CORREO, correo);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_TELEFONO, telefono);

        long resultado = db.insert(TABLE_USUARIOS, null, values);
        return resultado != -1;
    }
}
