package com.example.appyadira.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appyadira.adapter.ArticuloAdapter;
import com.example.appyadira.dao.ArticuloDao;
import com.example.appyadira.dao.DetalleDao;
import com.example.appyadira.dao.HistorialDao;
import com.example.appyadira.R;
import com.example.appyadira.model.Articulo;
import com.example.appyadira.model.Historial;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NuevoFragment extends Fragment {
    private RecyclerView recyclerArticulos;
    private RecyclerView.Adapter adaptadorArticulo;
    private TextInputEditText titulo, nombre, precio, cantidad;
    private TextInputLayout valTitulo, valNombre, valPrecio, valCantidad, valUnidad;
    private AutoCompleteTextView unidad;
    private Button agregar, actualizar, guardar;
    private TextView total;
    private String[] itemsUnidad = {"UN", "TIRA", "KG", "DOCENA", "PACK"};
    private ArrayList<Articulo> arrayArticulo;
    private ArticuloDao artDao;
    private HistorialDao histDao;
    private DetalleDao detaDao;
    private double monto;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nuevo, container, false);

        arrayArticulo = new ArrayList<>();
        artDao = new ArticuloDao(getContext());
        artDao.abrirConexion();
        histDao = new HistorialDao(getContext());
        histDao.abrirConexion();
        detaDao = new DetalleDao(getContext());
        detaDao.abrirConexion();
        recyclerArticulos = root.findViewById(R.id.rvArticulos);
        titulo = root.findViewById(R.id.txtTitulo);
        nombre = root.findViewById(R.id.txtNombre);
        precio = root.findViewById(R.id.txtPrecio);
        cantidad = root.findViewById(R.id.txtCantidad);
        unidad = root.findViewById(R.id.spnUnidad);
        agregar = root.findViewById(R.id.btnAgregar);
        actualizar = root.findViewById(R.id.btnActualizar);
        guardar = root.findViewById(R.id.btnGuardar);
        total = root.findViewById(R.id.txtTotal);
        valTitulo = root.findViewById(R.id.valTitulo);
        valNombre = root.findViewById(R.id.valNombre);
        valPrecio = root.findViewById(R.id.valPrecio);
        valCantidad = root.findViewById(R.id.valCantidad);
        valUnidad = root.findViewById(R.id.valUnidad);

        spinnerUnidad();
        recyclerViewNuevo();

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    arrayArticulo.add(new Articulo(nombre.getText().toString(), Double.parseDouble(precio.getText().toString()),
                            Integer.parseInt(cantidad.getText().toString()), unidad.getText().toString()));
                    adaptadorArticulo.notifyItemInserted(arrayArticulo.size());
                    clearText();
                    setTotal();
                } else
                    showToast("Completa los campos requeridos", FancyToast.ERROR);
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    arrayArticulo.get(position).setNombre(nombre.getText().toString());
                    arrayArticulo.get(position).setPrecio(Double.parseDouble(precio.getText().toString()));
                    arrayArticulo.get(position).setCantidad(Integer.parseInt(cantidad.getText().toString()));
                    arrayArticulo.get(position).setUnidad(unidad.getText().toString());
                    adaptadorArticulo.notifyItemChanged(position);
                    actualizar.setVisibility(View.GONE);
                    agregar.setVisibility(View.VISIBLE);
                    clearText();
                    setTotal();
                } else
                    showToast("Completa los campos requeridos", FancyToast.ERROR);
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titulo.getText().toString().isEmpty()) {
                    valTitulo.setError("Ingresa un título.");
                    showToast("Completa el campo requerido", FancyToast.ERROR);
                } else if (arrayArticulo.isEmpty()) {
                    valTitulo.setErrorEnabled(false);
                    showToast("Debe agregar artículos", FancyToast.ERROR);
                } else {
                    valTitulo.setErrorEnabled(false);
                    ArrayList<Integer> codArticulo = new ArrayList<>();

                    for (int i = 0; i < arrayArticulo.size(); i++) {
                        artDao.insertarArtitulo(arrayArticulo.get(i));
                        codArticulo.add(artDao.lastCodArticulo());
                    }

                    histDao.insertarHistorial(new Historial(titulo.getText().toString(), arrayArticulo.size(), monto));
                    detaDao.insertarDetalle(histDao.lastCodHistorial(), codArticulo);
                    titulo.setText("");
                    titulo.clearFocus();
                    arrayArticulo.clear();
                    actualizar.setVisibility(View.GONE);
                    agregar.setVisibility(View.VISIBLE);
                    total.setVisibility(View.GONE);
                    clearText();
                    adaptadorArticulo.notifyDataSetChanged();
                    showToast("Se ha grabado correctamente!", FancyToast.SUCCESS);
                }
            }
        });
        return root;
    }

    public void spinnerUnidad() {
        ArrayAdapter arrayEscuela = new ArrayAdapter<String>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, itemsUnidad);
        unidad.setAdapter(arrayEscuela);
        unidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre.clearFocus();
                precio.clearFocus();
                cantidad.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nombre.getWindowToken(), 0);
            }
        });
    }

    public void recyclerViewNuevo() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerArticulos.setLayoutManager(linearLayoutManager);
        adaptadorArticulo = new ArticuloAdapter(arrayArticulo, getContext());
        recyclerArticulos.setAdapter(adaptadorArticulo);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                actionSwipe(viewHolder, direction);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .setSwipeLeftActionIconTint(ContextCompat.getColor(getContext(), R.color.white))
                        .addSwipeLeftLabel("ELIMINAR")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary))
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .setSwipeRightActionIconTint(ContextCompat.getColor(getContext(), R.color.white))
                        .addSwipeRightLabel("MODIFICAR")
                        .setSwipeRightLabelColor(ContextCompat.getColor(getContext(), R.color.white))
                        .addCornerRadius(1, 15)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerArticulos);
    }

    public void setTotal() {
        double monto = 0;
        for (int i = 0; i < arrayArticulo.size(); i++)
            monto += arrayArticulo.get(i).getPrecio() * arrayArticulo.get(i).getCantidad();
        this.monto = Double.parseDouble(String.format("%.2f", monto));
        total.setVisibility(View.VISIBLE);
        total.setText("TOTAL: S/ " + this.monto);
    }

    public boolean validate() {
        boolean val = true;

        if (nombre.getText().toString().isEmpty()) {
            valNombre.setError("Ingresa un nombre.");
            val = false;
        } else
            valNombre.setErrorEnabled(false);

        if (precio.getText().toString().isEmpty()) {
            valPrecio.setError("Ingrese un precio.");
            val = false;
        } else
            valPrecio.setErrorEnabled(false);

        if (cantidad.getText().toString().isEmpty()) {
            valCantidad.setError("Ingrese una cantidad");
            val = false;
        } else
            valCantidad.setErrorEnabled(false);

        if (unidad.getText().toString().isEmpty()) {
            valUnidad.setError("Seleccione una unidad.");
            val = false;
        } else
            valUnidad.setErrorEnabled(false);

        return val;
    }

    public void clearText() {
        nombre.setText("");
        precio.setText("");
        cantidad.setText("");
        unidad.setText("");
        nombre.clearFocus();
        precio.clearFocus();
        cantidad.clearFocus();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nombre.getWindowToken(), 0);
    }

    public void showToast(String msg, int type) {
        Toast toast = FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, type, false);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 210);
        toast.show();
    }

    public void actionSwipe(RecyclerView.ViewHolder viewHolder, int direction) {
        position = viewHolder.getAdapterPosition();

        switch (direction) {
            case ItemTouchHelper.LEFT:
                Articulo borrarArticulo = arrayArticulo.get(position);
                arrayArticulo.remove(position);
                adaptadorArticulo.notifyItemRemoved(position);
                actualizar.setVisibility(View.GONE);
                agregar.setVisibility(View.VISIBLE);
                clearText();
                setTotal();

                if (arrayArticulo.isEmpty())
                    total.setVisibility(View.GONE);

                Snackbar.make(recyclerArticulos, borrarArticulo.getNombre(), Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrayArticulo.add(position, borrarArticulo);
                        adaptadorArticulo.notifyItemInserted(position);
                        setTotal();
                    }
                }).show();
                break;

            case ItemTouchHelper.RIGHT:
                nombre.setText(arrayArticulo.get(position).getNombre());
                precio.setText(String.valueOf(arrayArticulo.get(position).getPrecio()));
                cantidad.setText(String.valueOf(arrayArticulo.get(position).getCantidad()));
                unidad.setText(arrayArticulo.get(position).getUnidad());
                spinnerUnidad();
                agregar.setVisibility(View.INVISIBLE);
                actualizar.setVisibility(View.VISIBLE);
                adaptadorArticulo.notifyItemChanged(position);

                valNombre.setErrorEnabled(false);
                valPrecio.setErrorEnabled(false);
                valCantidad.setErrorEnabled(false);
                valUnidad.setErrorEnabled(false);
                break;
        }
    }
}