package com.example.taskdone.Finanzas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.taskdone.Preferences;

import java.time.Instant;
import java.util.Date;

public class DataBaseFinanzas extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "Gastos";
    private static final String COL_ID = "ID";
    private static final String COL_FECHA = "fecha";
    private static final String COL_TIPO_MONEDA = "tipo_moneda";
    private static final String COL_CANTIDAD = "cantidad";
    private static final String COL_MOTIVO = "motivo";
    private static final String COL_ESCENCIAL = "escencial";        // 0 FALSE - 1 TRUE
    private static final String COL_INGRESO = "ingreso";            // 0 FALSE - 1 TRUE

    Context context;

    public DataBaseFinanzas(Context context) {
        super(context, TABLE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FECHA + " TEXT, " +
                COL_ESCENCIAL + " TEXT, " + COL_TIPO_MONEDA + " TEXT, " + COL_CANTIDAD + " TEXT, " + COL_MOTIVO + " TEXT, "
                + COL_INGRESO + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String fecha, String tipo_moneda, String cantidad, String motivo, String escencial, String ingreso){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FECHA, fecha);
        contentValues.put(COL_TIPO_MONEDA, tipo_moneda);
        contentValues.put(COL_CANTIDAD, cantidad);
        contentValues.put(COL_ESCENCIAL, escencial);
        contentValues.put(COL_MOTIVO, motivo);
        contentValues.put(COL_INGRESO, ingreso);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            int cant = Integer.parseInt(Preferences.getPreferenceString(context, tipo_moneda));
            if(ingreso.equals("0")){
                cant -= Integer.parseInt(cantidad);
            }
            else{
                cant += Integer.parseInt(cantidad);
            }
            switch (tipo_moneda){
                case "Pesos":
                    Preferences.savePreferenceString(context, Integer.toString(cant), Preferences.PREFERENCE_PESOS);
                    break;
                case "Dólares":
                    Preferences.savePreferenceString(context, Integer.toString(cant), Preferences.PREFERENCE_DOLARES);
                    break;
                case "Euros":
                    Preferences.savePreferenceString(context, Integer.toString(cant), Preferences.PREFERENCE_EUROS);
                    break;
            }
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_FECHA + " DESC";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
