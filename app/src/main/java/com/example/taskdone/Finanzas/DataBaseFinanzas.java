package com.example.taskdone.Finanzas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskdone.Preferences;

import java.util.ArrayList;

public class DataBaseFinanzas extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_GASTOS = "Gastos";
    private static final String COL_ID = "ID";
    private static final String COL_FECHA = "fecha";
    private static final String COL_TIPO_MONEDA = "tipo_moneda";
    private static final String COL_CANTIDAD = "cantidad";
    private static final String COL_MOTIVO = "motivo";
    private static final String COL_ESCENCIAL = "escencial";        // 0 FALSE - 1 TRUE
    private static final String COL_INGRESO = "ingreso";            // 0 FALSE - 1 TRUE

    private static final String TABLE_USER = "Usuario";
    private static final String COL_PESOS = "pesos";
    private static final String COL_DOLARES = "dolares";
    private static final String COL_EUROS = "euros";
    private static final String COL_DATOS_CARGADOS = "datos_cargados"; //Solo la primera vez será false



    Context context;

    public DataBaseFinanzas(Context context) {
        super(context, TABLE_GASTOS, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableGastos = "CREATE TABLE " + TABLE_GASTOS + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FECHA + " TEXT, " +
                COL_ESCENCIAL + " TEXT, " + COL_TIPO_MONEDA + " TEXT, " + COL_CANTIDAD + " TEXT, " + COL_MOTIVO + " TEXT, "
                + COL_INGRESO + " TEXT);";
        db.execSQL(createTableGastos);


        String createTableUser = "CREATE TABLE " + TABLE_USER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PESOS + " INTEGER, " +
                COL_DOLARES + " INTEGER, " + COL_EUROS + " INTEGER, " + COL_DATOS_CARGADOS + " TEXT);";
        db.execSQL(createTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GASTOS);
        onCreate(db);
    }

    public boolean addDataGastos(String fecha, String tipo_moneda, String cantidad, String motivo, String escencial, String ingreso){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FECHA, fecha);
        contentValues.put(COL_TIPO_MONEDA, tipo_moneda);
        contentValues.put(COL_CANTIDAD, cantidad);
        contentValues.put(COL_ESCENCIAL, escencial);
        contentValues.put(COL_MOTIVO, motivo);
        contentValues.put(COL_INGRESO, ingreso);

        long result = db.insert(TABLE_GASTOS, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            ContentValues dineroModificado = new ContentValues();
            Cursor data = getDataUser();
            int id = 0;
            int pesos = 0;
            int dolares=0;
            int euros = 0;
            while (data.moveToNext()) {
                id = data.getInt(0);
                pesos = data.getInt(1);
                dolares = data.getInt(2);
                euros = data.getInt(3);
            }
            int cant = Integer.parseInt(cantidad);
            if(ingreso.equals("0")){
                cant = -cant;
            }
            switch (tipo_moneda){
                case "Pesos":
                    pesos = pesos + cant;
                    dineroModificado.put(COL_PESOS, pesos);
                    break;
                case "Dólares":
                    dolares = dolares + cant;
                    dineroModificado.put(COL_DOLARES, dolares);
                    break;
                case "Euros":
                    euros = euros + cant;
                    dineroModificado.put(COL_EUROS, euros);
                    break;
            }

            db.update(TABLE_USER, dineroModificado, "ID = ?", new String[]{Integer.toString(id)});


            return true;
        }
    }

    public Cursor getDataGastos(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GASTOS + " ORDER BY " + COL_FECHA + " DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public boolean addDataUser(int pesos, int dolares, int euros){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PESOS, pesos);
        contentValues.put(COL_DOLARES, dolares);
        contentValues.put(COL_EUROS, euros);
        contentValues.put(COL_DATOS_CARGADOS, "1");

        long result = db.insert(TABLE_USER, null, contentValues);

        if(result == -1){
            return false;
        }
        return true;
    }

    public Cursor getDataUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USER ;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


}
