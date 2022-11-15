package com.example.appyadira.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appyadira.R;
import com.example.appyadira.adapter.ArticuloAdapter;
import com.example.appyadira.adapter.HistorialAdapter;
import com.example.appyadira.dao.ArticuloDao;
import com.example.appyadira.dao.DetalleDao;
import com.example.appyadira.dao.HistorialDao;
import com.example.appyadira.interfaces.OnItemListener;
import com.example.appyadira.model.Articulo;
import com.example.appyadira.model.Historial;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HistorialFragment extends Fragment implements OnItemListener {
    private RecyclerView recyclerHistorial;
    private RecyclerView.Adapter adaptadorHistorial;
    private SwipeRefreshLayout swipe;
    private HistorialDao histDao;
    private ArticuloDao artDao;
    private DetalleDao detaDao;
    private AlertDialog alertDialog;
    private View alertView;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_historial, container, false);
        alertDialog = new AlertDialog.Builder(getContext(), R.style.Theme_AlertDialog).create();
        alertView = getLayoutInflater().inflate(R.layout.alertdialog_historial, null);
        alertDialog.setView(alertView);
        recyclerViewHistorial();
        refresh();
        return root;
    }

    public void recyclerViewHistorial() {
        histDao = new HistorialDao(getContext());
        histDao.abrirConexion();
        artDao = new ArticuloDao(getContext());
        artDao.abrirConexion();
        detaDao = new DetalleDao(getContext());
        detaDao.abrirConexion();
        recyclerHistorial = root.findViewById(R.id.rvHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adaptadorHistorial = new HistorialAdapter(histDao.listarHistorial(), getContext(), this);
        recyclerHistorial.setAdapter(adaptadorHistorial);
    }

    public void refresh() {
        swipe = root.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerViewHistorial();
                swipe.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(ArrayList<Historial> arrayHistorial, int position) {
        alertDialogArticulos(arrayHistorial, position);
        getTotal();
    }

    @Override
    public void onEliminarClick(ArrayList<Historial> arrayHistorial, int position) {
        deleteHistorial(arrayHistorial, position);
    }

    ArrayList<Articulo> arrayArticulos;
    TextInputEditText nombre, precio, cantidad;
    TextInputLayout valNombre, valPrecio, valCantidad, valUnidad;
    AutoCompleteTextView unidad;
    Button plus, cancel, agregar, actualizar;
    TextView historial, total;
    LinearLayout lyActualizar;
    RecyclerView recyclerArticulos;
    RecyclerView.Adapter adaptadorArticulo;
    String[] itemsUnidad = {"UN", "TIRA", "KG", "DOCENA", "PACK"};
    int pos;

    private void alertDialogArticulos(ArrayList<Historial> arrayHistorial, int position) {
        pos = position;
        alertDialog.show();
        historial = alertDialog.findViewById(R.id.txtHistorial);
        nombre = alertDialog.findViewById(R.id.txtNombre);
        precio = alertDialog.findViewById(R.id.txtPrecio);
        cantidad = alertDialog.findViewById(R.id.txtCantidad);
        unidad = alertDialog.findViewById(R.id.spnUnidad);
        total = alertDialog.findViewById(R.id.txtTotal);
        valNombre = alertDialog.findViewById(R.id.valNombre);
        valPrecio = alertDialog.findViewById(R.id.valPrecio);
        valCantidad = alertDialog.findViewById(R.id.valCantidad);
        valUnidad = alertDialog.findViewById(R.id.valUnidad);
        plus = alertDialog.findViewById(R.id.btnPlus);
        cancel = alertDialog.findViewById(R.id.btnCancel);
        agregar = alertDialog.findViewById(R.id.btnAgregar);
        actualizar = alertDialog.findViewById(R.id.btnActualizar);
        lyActualizar = alertDialog.findViewById(R.id.lyActualizar);
        recyclerArticulos = alertDialog.findViewById(R.id.rvArticulos);
        arrayArticulos = artDao.listarArticulos(arrayHistorial.get(position).getCod());
        adaptadorArticulo = new ArticuloAdapter(arrayArticulos, getContext());

        setLayoutHeight();

        lyActualizar.setVisibility(View.GONE);
        if (arrayHistorial.get(position).getEstado() == 1)
            plus.setVisibility(View.GONE);
        else
            plus.setVisibility(View.VISIBLE);
        historial.setText(arrayHistorial.get(position).getTitulo());
        recyclerArticulos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerArticulos.setAdapter(adaptadorArticulo);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearText();
                plus.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                lyActualizar.setVisibility(View.VISIBLE);
                agregar.setVisibility(View.VISIBLE);
                actualizar.setVisibility(View.GONE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                lyActualizar.setVisibility(View.GONE);
            }
        });
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    ArrayList<Integer> codArticulo = new ArrayList<>();
                    plus.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                    lyActualizar.setVisibility(View.GONE);
                    arrayArticulos.add(new Articulo(nombre.getText().toString(), Double.parseDouble(precio.getText().toString()),
                            Integer.parseInt(cantidad.getText().toString()), unidad.getText().toString()));
                    artDao.insertarArtitulo(arrayArticulos.get(arrayArticulos.size() - 1));
                    codArticulo.add(artDao.lastCodArticulo());
                    detaDao.insertarDetalle(arrayHistorial.get(pos).getCod(), codArticulo);
                    histDao.modificarHistorial(new Historial(arrayHistorial.get(pos).getCod(), arrayHistorial.get(pos).getTitulo(), arrayArticulos.size(), getTotal(), 0));
                    adaptadorArticulo.notifyItemInserted(arrayArticulos.size());
                    setLayoutHeight();
                    recyclerViewHistorial();
                    clearText();
                } else
                    showToast("Completa los campos requeridos", FancyToast.ERROR);
            }
        });
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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (arrayHistorial.get(pos).getEstado() == 1)
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                actionSwipe(viewHolder, direction, arrayHistorial);
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

    public void actionSwipe(RecyclerView.ViewHolder viewHolder, int direction, ArrayList<Historial> arrayHistorial) {
        int posicion = viewHolder.getAdapterPosition();

        switch (direction) {
            case ItemTouchHelper.LEFT:
                if (arrayArticulos.size() > 1) {
                    plus.setVisibility(View.VISIBLE);
                    lyActualizar.setVisibility(View.GONE);
                    Articulo borrarArticulo = arrayArticulos.get(posicion);
                    arrayArticulos.remove(posicion);
                    artDao.eliminarArticulo(borrarArticulo.getCod());
                    histDao.modificarHistorial(new Historial(arrayHistorial.get(pos).getCod(), arrayHistorial.get(pos).getTitulo(), arrayArticulos.size(), getTotal(), 0));
                    adaptadorArticulo.notifyItemRemoved(posicion);
                    setLayoutHeight();
                    recyclerViewHistorial();
                    Snackbar snackbar = Snackbar.make(recyclerArticulos, borrarArticulo.getNombre(), Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            arrayArticulos.add(posicion, borrarArticulo);
                            artDao.deshacerArtitulo(borrarArticulo);
                            histDao.modificarHistorial(new Historial(arrayHistorial.get(pos).getCod(), arrayHistorial.get(pos).getTitulo(), arrayArticulos.size(), getTotal(), 0));
                            adaptadorArticulo.notifyItemInserted(posicion);
                            recyclerViewHistorial();
                        }
                    });
                    snackbar.show();
                } else {
                    deleteHistorial2(arrayHistorial);
                    adaptadorArticulo.notifyItemChanged(posicion);
                }
                break;

            case ItemTouchHelper.RIGHT:
                cancel.setVisibility(View.VISIBLE);
                plus.setVisibility(View.GONE);
                agregar.setVisibility(View.GONE);
                actualizar.setVisibility(View.VISIBLE);
                lyActualizar.setVisibility(View.VISIBLE);
                nombre.setText(arrayArticulos.get(posicion).getNombre());
                precio.setText(String.valueOf(arrayArticulos.get(posicion).getPrecio()));
                cantidad.setText(String.valueOf(arrayArticulos.get(posicion).getCantidad()));
                unidad.setText(arrayArticulos.get(posicion).getUnidad());
                ArrayAdapter arrayUnidad = new ArrayAdapter<String>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, itemsUnidad);
                unidad.setAdapter(arrayUnidad);
                adaptadorArticulo.notifyItemChanged(posicion);

                valNombre.setErrorEnabled(false);
                valPrecio.setErrorEnabled(false);
                valCantidad.setErrorEnabled(false);
                valUnidad.setErrorEnabled(false);
                break;
        }

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    plus.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                    lyActualizar.setVisibility(View.GONE);
                    arrayArticulos.get(posicion).setNombre(nombre.getText().toString());
                    arrayArticulos.get(posicion).setPrecio(Double.parseDouble(precio.getText().toString()));
                    arrayArticulos.get(posicion).setCantidad(Integer.parseInt(cantidad.getText().toString()));
                    arrayArticulos.get(posicion).setUnidad(unidad.getText().toString());
                    artDao.modificarArticulo(arrayArticulos.get(posicion));
                    histDao.modificarHistorial(new Historial(arrayHistorial.get(pos).getCod(), arrayHistorial.get(pos).getTitulo(), arrayArticulos.size(), getTotal(), 0));
                    adaptadorArticulo.notifyItemChanged(posicion);
                    recyclerViewHistorial();
                    clearText();
                } else
                    showToast("Completa los campos requeridos", FancyToast.ERROR);
            }
        });
    }

    public void clearText() {
        nombre.setText("");
        precio.setText("");
        cantidad.setText("");
        unidad.setText("");
        nombre.clearFocus();
        precio.clearFocus();
        cantidad.clearFocus();
        valNombre.setErrorEnabled(false);
        valPrecio.setErrorEnabled(false);
        valCantidad.setErrorEnabled(false);
        valUnidad.setErrorEnabled(false);

        ArrayAdapter arrayUnidad = new ArrayAdapter<String>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, itemsUnidad);
        unidad.setAdapter(arrayUnidad);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nombre.getWindowToken(), 0);
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

    public double getTotal() {
        double monto = 0;
        for (int i = 0; i < arrayArticulos.size(); i++)
            monto += arrayArticulos.get(i).getPrecio() * arrayArticulos.get(i).getCantidad();
        total.setText("TOTAL: S/ " + Double.parseDouble(String.format("%.2f", monto)));
        return Double.parseDouble(String.format("%.2f", monto));
    }

    public void showToast(String msg, int type) {
        Toast toast = FancyToast.makeText(getContext(), msg, FancyToast.LENGTH_SHORT, type, false);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 40);
        toast.show();
    }

    public void deleteHistorial(ArrayList<Historial> arrayHistorial, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("ELIMINAR")
                .setMessage("¿Está seguro de eliminar " + arrayHistorial.get(position).getTitulo() + "?")
                .setCancelable(false)
                .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        histDao.eliminarHistorial(arrayHistorial.get(position).getCod());
                        arrayHistorial.remove(position);
                        adaptadorHistorial.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();
    }

    public void deleteHistorial2(ArrayList<Historial> arrayHistorial) {
        new AlertDialog.Builder(getContext())
                .setTitle("ELIMINAR LISTA")
                .setMessage("¿Está seguro de eliminar " + arrayHistorial.get(pos).getTitulo() + "?")
                .setCancelable(false)
                .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        histDao.eliminarHistorial(arrayHistorial.get(pos).getCod());
                        arrayHistorial.remove(pos);
                        alertDialog.dismiss();
                        recyclerViewHistorial();
                    }
                })
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();
    }

    public void setLayoutHeight() {
        if (arrayArticulos.size() > 6) {
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1300);
            recyclerArticulos.setLayoutParams(lparams);
        } else {
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            recyclerArticulos.setLayoutParams(lparams);
        }
    }
}