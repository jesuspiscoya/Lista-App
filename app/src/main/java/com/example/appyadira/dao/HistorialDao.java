package com.example.appyadira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appyadira.model.DBHistorial;
import com.example.appyadira.model.Historial;

import java.util.ArrayList;

public class HistorialDao {
    SQLiteDatabase conexion;
    DBHistorial dbHistorial;

    public HistorialDao(Context context) {
        dbHistorial = new DBHistorial(context);
    }

    public void abrirConexion() {
        conexion = dbHistorial.getWritableDatabase();
    }

    public void insertarHistorial(Historial historial) {
        ContentValues valHistorial = new ContentValues();
        valHistorial.put("titulo", historial.getTitulo());
        valHistorial.put("cantidad", historial.getCantidad());
        valHistorial.put("total", historial.getTotal());
        valHistorial.put("estado", 0);
        conexion.insert(dbHistorial.DATABASE_TABLE_2, null, valHistorial);
    }

    public void modificarHistorial(Historial historial) {
        ContentValues valHistorial = new ContentValues();
        valHistorial.put("titulo", historial.getTitulo());
        valHistorial.put("cantidad", historial.getCantidad());
        valHistorial.put("total", historial.getTotal());
        valHistorial.put("estado", historial.getEstado());
        conexion.update(dbHistorial.DATABASE_TABLE_2, valHistorial, "cod_hist=" + historial.getCod(), null);
    }

    public void eliminarHistorial(int cod) {
        conexion.delete(dbHistorial.DATABASE_TABLE_2, "cod_hist=" + cod, null);
    }

    public ArrayList<Historial> listarHistorial() {
        Cursor c;
        ArrayList<Historial> arrayHistorial = new ArrayList<>();
        String sql = "select * from " + dbHistorial.DATABASE_TABLE_2 + " order by cod_hist desc";
        c = conexion.rawQuery(sql, null);

        //LEER EL CURSOR FILA POR FILA
        while (c.moveToNext()) {
            Historial historial = new Historial(c.getInt(0), c.getString(1), c.getInt(2), c.getDouble(3), c.getInt(4));
            arrayHistorial.add(historial);
        }

        return arrayHistorial;
    }

    public int lastCodHistorial() {
        int cod = 0;
        String sql = "select cod_hist from " + dbHistorial.DATABASE_TABLE_2 + " order by cod_hist desc limit 1";
        Cursor c = conexion.rawQuery(sql, null);

        if (c.moveToLast())
            cod = c.getInt(0);

        return cod;
    }
}
