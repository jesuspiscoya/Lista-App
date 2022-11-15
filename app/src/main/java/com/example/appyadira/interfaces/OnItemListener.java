package com.example.appyadira.interfaces;

import com.example.appyadira.model.Historial;

import java.util.ArrayList;

public interface OnItemListener {
    void onItemClick(ArrayList<Historial> arrayHistorial, int position);
    void onEliminarClick(ArrayList<Historial> arrayHistorial, int position);
}
