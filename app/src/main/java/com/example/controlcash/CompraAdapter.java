package com.example.controlcash;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.ViewHolder> {

    private final ArrayList<Compra> compras;
    private final OnDeleteClickListener deleteListener;
    private final OnItemClickListener itemClickListener;
    private final String usuarioActual;

    public interface OnDeleteClickListener {
        void onDelete(int id);
    }

    public interface OnItemClickListener {
        void onClick(int id);
    }

    public CompraAdapter(ArrayList<Compra> compras, OnDeleteClickListener deleteListener, OnItemClickListener itemClickListener, String usuarioActual) {
        this.compras = compras;
        this.deleteListener = deleteListener;
        this.itemClickListener = itemClickListener;
        this.usuarioActual = usuarioActual;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compra, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Compra compra = compras.get(position);

        holder.tvNombre.setText(compra.nombre);
        holder.tvPrecio.setText("$" + String.format("%.2f", compra.precio));
        holder.tvCantidad.setText("x " + compra.cantidad);
        holder.tvDescuento.setText(compra.descuento + "%");
        holder.tvTotal.setText("$" + String.format("%.2f", compra.total));

        // Mostrar el nombre del usuario con estilo condicional
        if (compra.usuario != null && compra.usuario.equals(usuarioActual)) {
            holder.tvUsuarioCompra.setText("Usuario: " + compra.usuario);
            holder.tvUsuarioCompra.setTypeface(null, Typeface.BOLD);
            holder.tvUsuarioCompra.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.tvUsuarioCompra.setText("Usuario: " + compra.usuario);
            holder.tvUsuarioCompra.setTypeface(null, Typeface.NORMAL);
            holder.tvUsuarioCompra.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
        }

        holder.itemView.setOnClickListener(v -> itemClickListener.onClick(compra.id));
    }

    @Override
    public int getItemCount() {
        return compras.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvUsuarioCompra, tvPrecio, tvCantidad, tvDescuento, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvUsuarioCompra = itemView.findViewById(R.id.tvUsuarioCompra);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvDescuento = itemView.findViewById(R.id.tvDescuento);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
