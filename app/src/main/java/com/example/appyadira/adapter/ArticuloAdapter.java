package com.example.appyadira.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appyadira.R;
import com.example.appyadira.model.Articulo;

import java.util.ArrayList;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ViewHolderNuevo> {
    private ArrayList<Articulo> arrayArticulo;
    private Context context;

    public ArticuloAdapter(ArrayList<Articulo> arrayArticulo, Context context) {
        this.arrayArticulo = arrayArticulo;
        this.context = context;
    }

    @Override
    public ArticuloAdapter.ViewHolderNuevo onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_articulo, parent, false);
        return new ViewHolderNuevo(view);
    }

    @Override
    public void onBindViewHolder(ArticuloAdapter.ViewHolderNuevo holder, int position) {
        holder.nombre.setText(arrayArticulo.get(position).getCantidad() + " " + arrayArticulo.get(position).getUnidad() + " " + arrayArticulo.get(position).getNombre());
        holder.precio.setText("S/ " + arrayArticulo.get(position).getPrecio());
        holder.subtotal.setText("S/ " + String.format("%.2f", arrayArticulo.get(position).getPrecio() * arrayArticulo.get(position).getCantidad()));
        holder.card.setAnimation(AnimationUtils.loadAnimation(context, R.anim.transition_left));
    }

    @Override
    public int getItemCount() {
        return arrayArticulo.size();
    }

    public class ViewHolderNuevo extends RecyclerView.ViewHolder {
        TextView nombre, precio, subtotal;
        CardView card;

        public ViewHolderNuevo(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.txtNombre);
            precio = itemView.findViewById(R.id.txtPrecio);
            subtotal = itemView.findViewById(R.id.txtSubTotal);
            card = itemView.findViewById(R.id.card);
        }
    }
}
