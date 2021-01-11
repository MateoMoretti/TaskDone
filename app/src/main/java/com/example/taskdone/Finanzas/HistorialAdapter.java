package com.example.taskdone.Finanzas;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskdone.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolderHistorial> {

    List<ItemHistorial> items;
    String fecha;
    String signo;
    String tipo;
    String cantidad;
    String motivo;
    NavController navController;
    Context context;

    public HistorialAdapter(List<ItemHistorial> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderHistorial onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);

        return new ViewHolderHistorial(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderHistorial holder, int position) {

        holder.signo.setText(items.get(position).signo);
        holder.tipo.setText(items.get(position).tipo);
        holder.cantidad.setText(items.get(position).cantidad);
        holder.motivo.setText(items.get(position).motivo);

        if(items.get(position).signo.equals("+")){
            holder.signo.setTextColor(context.getResources().getColor(R.color.verde_ingreso));
            holder.tipo.setTextColor(context.getResources().getColor(R.color.verde_ingreso));
            holder.cantidad.setTextColor(context.getResources().getColor(R.color.verde_ingreso));
        }
        else{
            holder.signo.setTextColor(context.getResources().getColor(R.color.rojo_egreso));
            holder.tipo.setTextColor(context.getResources().getColor(R.color.rojo_egreso));
            holder.cantidad.setTextColor(context.getResources().getColor(R.color.rojo_egreso));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolderHistorial extends RecyclerView.ViewHolder {
        TextView signo;
        TextView tipo;
        TextView cantidad;
        TextView motivo;


        public ViewHolderHistorial(@NonNull View itemView) {
            super(itemView);
            signo = itemView.findViewById(R.id.signo_ingreso);
            tipo = itemView.findViewById(R.id.txt_tipo);
            cantidad = itemView.findViewById(R.id.txt_cantidad);
            motivo = itemView.findViewById(R.id.txt_motivo);
        }


    }

}