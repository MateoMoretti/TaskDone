package com.example.taskdone.Agenda;


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


public class DiaAdapter extends RecyclerView.Adapter<DiaAdapter.ViewHolderDia> {

    List<DiaItem> items;
    String ano;
    String mes;
    NavController navController;
    Context context;

    public DiaAdapter(List<DiaItem> items, String ano, String mes, Context context) {
        this.items = items;
        this.ano = ano;
        this.mes = mes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderDia onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_dias, parent, false);

        return new ViewHolderDia(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderDia holder, int position) {

        holder.nombre_dia.setText(items.get(position).nombre_dia);
        holder.numero_dia.setText(items.get(position).numero_dia);
        holder.descripcion.setText(items.get(position).descripcion);



        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date_hoy = new Date();
        String fecha_hoy = formatter.format(date_hoy);

        int ano_hoy = Integer.parseInt(fecha_hoy.substring(6,10));
        int mes_hoy = Integer.parseInt(fecha_hoy.substring(3,5));
        int dia_hoy = Integer.parseInt(fecha_hoy.substring(0,2));

        if(ano_hoy > Integer.parseInt(ano)){
            holder.activo.setImageResource(android.R.drawable.presence_invisible);
        }
        else if(ano_hoy < Integer.parseInt(ano)){
            holder.activo.setImageResource(android.R.drawable.presence_online);
        }

        else {
            if (mes_hoy > Integer.parseInt(mes)) {
                holder.activo.setImageResource(android.R.drawable.presence_invisible);
            } else if (mes_hoy < Integer.parseInt(mes)) {
                holder.activo.setImageResource(android.R.drawable.presence_online);
            } else {
                if (dia_hoy > Integer.parseInt(items.get(position).numero_dia)) {
                    holder.activo.setImageResource(android.R.drawable.presence_invisible);
                } else if (dia_hoy < Integer.parseInt(items.get(position).numero_dia)) {
                    holder.activo.setImageResource(android.R.drawable.presence_online);
                } else {
                    holder.activo.setImageResource(android.R.drawable.presence_away);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void avanzar(){

        Intent intent = new Intent(context, ActivityAgenda.class);
        context.startActivity(intent);

        //Ojo que nunca cierro el activity main
    }


    public class ViewHolderDia extends RecyclerView.ViewHolder {
        ImageView activo;
        TextView nombre_dia;
        TextView numero_dia;
        TextView descripcion;


        public ViewHolderDia(@NonNull View itemView) {
            super(itemView);
            activo = itemView.findViewById(R.id.dia_activo);
            nombre_dia = itemView.findViewById(R.id.nombre_dia);
            numero_dia = itemView.findViewById(R.id.numero_dia);
            descripcion = itemView.findViewById(R.id.descripcion);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    avanzar();
                }
            });
        }


    }

}