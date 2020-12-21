package com.example.taskdone.Finanzas;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskdone.Agenda.ActivityAgenda;
import com.example.taskdone.R;

import java.util.ArrayList;
import java.util.List;


public class ListaTagsAdapter extends RecyclerView.Adapter<ListaTagsAdapter.ViewHolderAdapter> {

    List<ItemTag> items;
    ArrayList<String> tags_elegidos;

    Context context;

    public ListaTagsAdapter(List<ItemTag> items, ArrayList<String> tags_bundle, Context context) {
        this.items = items;
        this.context = context;
        tags_elegidos = new ArrayList<>();
        if(tags_bundle != null){
            this.tags_elegidos = tags_bundle;
        }
    }

    @NonNull
    @Override
    public ViewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);

        return new ViewHolderAdapter(view);
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapter holder, int position) {

        holder.tag.setText(items.get(position).nombre_tag);
        if(tags_elegidos.contains(items.get(position).nombre_tag)){
            holder.tag.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolderAdapter extends RecyclerView.ViewHolder {
        CheckBox tag;

        public ViewHolderAdapter(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.check_tag);
            tag.setOnClickListener(v -> TagToList(tag));
            itemView.setOnClickListener(v -> checkear(tag));
        }

        private void checkear(CheckBox tag){
            if(tag.isChecked()){
                tag.setChecked(false);
                TagToList(tag);
            }
            else{
                tag.setChecked(true);
                TagToList(tag);
            }
        }
    }

    public void TagToList(CheckBox tag){
        if(tag.isChecked() && !tags_elegidos.contains(tag.getText().toString())){
            tags_elegidos.add(tag.getText().toString());
        }
        else{
            tags_elegidos.remove(tag.getText().toString());
        }
    }


    public ArrayList<String> getTagsElegidos() {
        return tags_elegidos;
    }
}