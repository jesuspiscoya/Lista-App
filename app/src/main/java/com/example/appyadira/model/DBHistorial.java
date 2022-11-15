package com.example.appyadira.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHistorial extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "YadiraDB.db";
    public static final String DATABASE_TABLE_1 = "articulo";
    public static final String DATABASE_TABLE_2 = "historial";
    public static final String DATABASE_TABLE_3 = "detalle_historial";
    public static final int DATABASE_VERSION = 1;
    static String sql1 = "create table if not exists " + DATABASE_TABLE_1 + "(cod_art integer primary key autoincrement, " +
            "nombre text, precio real, cantidad integer, unidad text)";
    static String sql2 = "create table if not exists " + DATABASE_TABLE_2 + "(cod_hist integer primary key autoincrement, " +
            "titulo text, cantidad integer, total real, estado integer)";
    static String sql3 = "create table if not exists " + DATABASE_TABLE_3 + "(cod_deta_hist integer primary key autoincrement, " +
            "cod_hist integer, cod_art integer, foreign key(cod_hist) references " + DATABASE_TABLE_2 + "(cod_hist), " +
            "foreign key(cod_art) references " + DATABASE_TABLE_1 + "(cod_art))";

    public DBHistorial(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
