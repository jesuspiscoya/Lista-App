package com.example.appyadira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.appyadira.model.DBHistorial;

import java.util.ArrayList;

public class DetalleDao {
    SQLiteDatabase conexion;
    DBHistorial dbHistorial;

    public DetalleDao(Context context) {
        dbHistorial = new DBHistorial(context);
    }

    public void abrirConexion() {
        conexion = dbHistorial.getWritableDatabase();
    }

    public void insertarDetalle(int codHistorial, ArrayList<Integer> codArticulo) {
        for (int i = 0; i < codArticulo.size(); i++) {
            ContentValues valDetalle = new ContentValues();
            valDetalle.put("cod_hist", codHistorial);
            valDetalle.put("cod_art", codArticulo.get(i));
            conexion.insert(dbHistorial.DATABASE_TABLE_3, null, valDetalle);
        }
    }
}
