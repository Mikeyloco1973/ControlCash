package com.example.controlcash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.ViewHolder> {

    private final ArrayList<Compra> compras;
    private final OnDeleteClickListener deleteListener;
    private final OnItemClickListener itemClickListener;

    public interface OnDeleteClickListener {
        void onDelete(int id);
    }

    public interface OnItemClickListener {
        void onClick(int id);
    }

    public CompraAdapter(ArrayList<Compra> compras, OnDeleteClickListener deleteListener, OnItemClickListener itemClickListener) {
        this.compras = compras;
        this.deleteListener = deleteListener;
        this.itemClickListener = itemClickListener;
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

        holder.itemView.setOnClickListener(v -> itemClickListener.onClick(compra.id));
    }

    @Override
    public int getItemCount() {
        return compras.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvCantidad, tvDescuento, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvDescuento = itemView.findViewById(R.id.tvDescuento);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
