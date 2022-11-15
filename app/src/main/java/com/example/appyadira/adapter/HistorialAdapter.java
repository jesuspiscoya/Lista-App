package com.example.appyadira.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appyadira.R;
import com.example.appyadira.dao.HistorialDao;
import com.example.appyadira.interfaces.OnItemListener;
import com.example.appyadira.model.Historial;

import java.util.ArrayList;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolderLista> {
    private ArrayList<Historial> arrayHistorial;
    private Context context;
    private HistorialDao histDao;
    private OnItemListener onItemListener;

    public HistorialAdapter(ArrayList<Historial> arrayHistorial, Context context, OnItemListener onItemListener) {
        this.arrayHistorial = arrayHistorial;
        this.context = context;
        this.onItemListener = onItemListener;
        histDao = new HistorialDao(context);
        histDao.abrirConexion();
    }

    @Override
    public ViewHolderLista onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_historial, parent, false);
        return new HistorialAdapter.ViewHolderLista(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(ViewHolderLista holder, int position) {
        if (arrayHistorial.get(position).getCantidad() > 1)
            holder.cantidad.setText(arrayHistorial.get(position).getCantidad() + " Artículos");
        else
            holder.cantidad.setText(arrayHistorial.get(position).getCantidad() + " Artículo");

        holder.titulo.setText(arrayHistorial.get(position).getTitulo());
        holder.total.setText("Total: S/ " + arrayHistorial.get(position).getTotal());
        holder.card.setAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_left));
        holder.finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishHistorial(position);
            }
        });

        if (arrayHistorial.get(position).getEstado() == 1) {
            holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(R.color.finalizar));
            holder.finalizar.setBackgroundColor(holder.finalizar.getContext().getResources().getColor(R.color.finalizar));
            holder.finalizar.setVisibility(View.GONE);
            holder.eliminar.setVisibility(View.VISIBLE);
        } else {
            holder.card.setCardBackgroundColor(holder.card.getContext().getResources().getColor(R.color.white));
            holder.finalizar.setBackgroundColor(holder.finalizar.getContext().getResources().getColor(R.color.white));
            holder.finalizar.setVisibility(View.VISIBLE);
            holder.eliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayHistorial.size();
    }

    public class ViewHolderLista extends RecyclerView.ViewHolder {
        RelativeLayout historial;
        TextView titulo, cantidad, total;
        Button finalizar, eliminar;
        CardView card;
        OnItemListener onItemListener;

        public ViewHolderLista(View itemView, OnItemListener onItemListener) {
            super(itemView);
            historial = itemView.findViewById(R.id.rlHistorial);
            titulo = itemView.findViewById(R.id.txtTitulo);
            cantidad = itemView.findViewById(R.id.txtCantidad);
            total = itemView.findViewById(R.id.txtTotal);
            finalizar = itemView.findViewById(R.id.btnFinalizar);
            eliminar = itemView.findViewById(R.id.btnEliminar);
            card = itemView.findViewById(R.id.card);
            this.onItemListener = onItemListener;
            historial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(arrayHistorial, getAdapterPosition());
                }
            });
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onEliminarClick(arrayHistorial, getAdapterPosition());
                }
            });
        }
    }

    public void finishHistorial(int position) {
        new AlertDialog.Builder(context)
                .setTitle("FINALIZAR")
                .setMessage("¿Está seguro de finalizar " + arrayHistorial.get(position).getTitulo() + "?")
                .setCancelable(false)
                .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        arrayHistorial.get(position).setEstado(1);
                        histDao.modificarHistorial(arrayHistorial.get(position));
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create().show();
    }
}
