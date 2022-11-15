package com.example.appyadira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.appyadira.model.Articulo;
import com.example.appyadira.model.DBHistorial;

import java.util.ArrayList;

public class ArticuloDao {
    SQLiteDatabase conexion;
    DBHistorial dbHistorial;

    public ArticuloDao(Context context) {
        dbHistorial = new DBHistorial(context);
    }

    public void abrirConexion() {
        conexion = dbHistorial.getWritableDatabase();
    }

    public void insertarArtitulo(Articulo articulo) {
        ContentValues valArticulo = new ContentValues();
        valArticulo.put("nombre", articulo.getNombre());
        valArticulo.put("precio", articulo.getPrecio());
        valArticulo.put("cantidad", articulo.getCantidad());
        valArticulo.put("unidad", articulo.getUnidad());
        conexion.insert(dbHistorial.DATABASE_TABLE_1, null, valArticulo);
    }

    public void modificarArticulo(Articulo articulo) {
        ContentValues valArticulo = new ContentValues();
        valArticulo.put("nombre", articulo.getNombre());
        valArticulo.put("precio", articulo.getPrecio());
        valArticulo.put("cantidad", articulo.getCantidad());
        valArticulo.put("unidad", articulo.getUnidad());
        conexion.update(dbHistorial.DATABASE_TABLE_1, valArticulo, "cod_art=" + articulo.getCod(), null);
    }

    public void eliminarArticulo(int cod) {
        conexion.delete(dbHistorial.DATABASE_TABLE_1, "cod_art=" + cod, null);
    }

    public ArrayList<Articulo> listarArticulos(int cod) {
        ArrayList<Articulo> arrayArticulo = new ArrayList<>();
        String sql = "select art.cod_art, art.nombre, art.precio, art.cantidad, art.unidad from " + dbHistorial.DATABASE_TABLE_1 + " art " +
                "inner join " + dbHistorial.DATABASE_TABLE_3 + " deta on deta.cod_art=art.cod_art " +
                "inner join " + dbHistorial.DATABASE_TABLE_2 + " hist on hist.cod_hist=deta.cod_hist " +
                "where hist.cod_hist=?";
        String cad[] = {"" + cod};
        Cursor c = conexion.rawQuery(sql, cad);

        //LEER EL CURSOR FILA POR FILA
        while (c.moveToNext()) {
            Articulo articulo = new Articulo(c.getInt(0), c.getString(1), c.getDouble(2), c.getInt(3), c.getString(4));
            arrayArticulo.add(articulo);
        }

        return arrayArticulo;
    }

    public int lastCodArticulo() {
        int cod = 0;
        String sql = "select cod_art from " + dbHistorial.DATABASE_TABLE_1 + " order by cod_art desc limit 1";
        Cursor c = conexion.rawQuery(sql, null);

        if (c.moveToLast())
            cod = c.getInt(0);

        return cod;
    }

    public void deshacerArtitulo(Articulo articulo) {
        ContentValues valArticulo = new ContentValues();
        valArticulo.put("cod_art", articulo.getCod());
        valArticulo.put("nombre", articulo.getNombre());
        valArticulo.put("precio", articulo.getPrecio());
        valArticulo.put("cantidad", articulo.getCantidad());
        valArticulo.put("unidad", articulo.getUnidad());
        conexion.insert(dbHistorial.DATABASE_TABLE_1, null, valArticulo);
    }
}
