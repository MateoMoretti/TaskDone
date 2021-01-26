package com.example.taskdone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DataBase extends SQLiteOpenHelper {

    private static final String TABLE_GASTO = "Gasto";
    private static final String COL_ID = "ID";
    private static final String COL_FECHA = "fecha";                // string
    private static final String COL_TOTAL_GASTO = "total_gasto";    // int
    private static final String COL_MOTIVO = "motivo";              // string
    private static final String COL_INGRESO = "ingreso";            // string
    private static final String COL_ID_MONEDA = "id_moneda";        // int
    private static final String COL_ID_USUARIO = "id_usuario";      // int

    private static final String TABLE_USUARIO = "Usuario";
    private static final String COL_USERNAME = "usuario";
    private static final String COL_PASSWORD = "contraseña";

    private static final String TABLE_TAG = "Tag";
    private static final String COL_NOMBRE = "nombre";               //string


    private static final String TABLE_TAG_USUARIO = "Tag_Usuario";
    private static final String COL_ID_TAG = "id_tag";                //int
    //private static final String COL_ID_USUARIO = "id_usuario";      //int  definido arriba

    private static final String TABLE_TAG_GASTO = "Tag_Gasto";
    //private static final String COL_ID_TAG = "id_tag";              //int  definido arriba
    private static final String COL_ID_GASTO = "id_gasto";            //int


    private static final String TABLE_MONEDA = "Moneda";
    //private static final String COL_ID_MONEDA_CANTIDAD               //int  definido arriba
    //private static final String COL_NOMBRE = "nombre";               // definido arriba
    private static final String COL_CANTIDAD = "cantidad";             // int
    private static final String COL_SIMBOLO = "simbolo";               // string
    //private static final String COL_ID_USUARIO = "id_usuario";       //int  definido arriba


    Context context;

    public DataBase(Context context) {
        super(context, "DATABASE", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableGastos = "CREATE TABLE " + TABLE_GASTO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FECHA + " TEXT, " +
                COL_TOTAL_GASTO + " REAL, " + COL_MOTIVO + " TEXT, " + COL_INGRESO + " TEXT, "
                + COL_ID_MONEDA + " INTEGER, " + COL_ID_USUARIO + " INTEGER, "
                + " FOREIGN KEY ("+COL_ID_MONEDA+") REFERENCES "+TABLE_MONEDA+"("+COL_ID+"), "
                + " FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"));";
        db.execSQL(createTableGastos);


        String createTableUser = "CREATE TABLE " + TABLE_USUARIO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USERNAME + " TEXT, "+ COL_PASSWORD + " TEXT);";
        db.execSQL(createTableUser);


        String createTableTag = "CREATE TABLE " + TABLE_TAG + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NOMBRE + " TEXT);";
        db.execSQL(createTableTag);


        String createTableTagUsuario = "CREATE TABLE " + TABLE_TAG_USUARIO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_TAG + " INTEGER, " +
                COL_ID_USUARIO + " INTEGER, "  + " FOREIGN KEY ("+COL_ID_TAG+") REFERENCES "+TABLE_TAG+"("+COL_ID+"), " +
                " FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"));";
        db.execSQL(createTableTagUsuario);

        String createTableTagGasto = "CREATE TABLE " + TABLE_TAG_GASTO + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ID_TAG + " INTEGER, " +
                COL_ID_GASTO + " INTEGER, "  + " FOREIGN KEY ("+COL_ID_TAG+") REFERENCES "+TABLE_TAG+"("+COL_ID+"), " +
                " FOREIGN KEY ("+COL_ID_GASTO+") REFERENCES "+ TABLE_GASTO +"("+COL_ID+"));";
        db.execSQL(createTableTagGasto);


        String createTableMonedaCantidad = "CREATE TABLE " + TABLE_MONEDA + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE + " TEXT, "  + COL_CANTIDAD + " REAL, " + COL_SIMBOLO + " TEXT, " + COL_ID_USUARIO + " INTEGER, " +"FOREIGN KEY ("+COL_ID_USUARIO+") REFERENCES "+TABLE_USUARIO+"("+COL_ID+"));";
        db.execSQL(createTableMonedaCantidad);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GASTO);
        onCreate(db);
    }

    public boolean addGastos(String fecha, String nombre_moneda, Float total_gasto, String motivo, List<String> tags, String ingreso){
        SQLiteDatabase db = this.getWritableDatabase();

        DataBase dataBase = new DataBase(context);
        Cursor moneda = dataBase.getMonedaIdByNombre(nombre_moneda);
        int id_moneda = 0;
        float cantidad = 0;
        while (moneda.moveToNext()){
            id_moneda = moneda.getInt(0);
            cantidad = moneda.getFloat(2);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FECHA, fecha);
        contentValues.put(COL_TOTAL_GASTO, total_gasto);
        contentValues.put(COL_MOTIVO, motivo);
        contentValues.put(COL_INGRESO, ingreso);
        contentValues.put(COL_ID_MONEDA, id_moneda);
        contentValues.put(COL_ID_USUARIO, UsuarioSingleton.getInstance().getID());

        long result = db.insert(TABLE_GASTO, null, contentValues);

        if(result == -1){
            return false;
        }
        else{
            if(ingreso.equals("0")){
                total_gasto = -total_gasto;
            }

            cantidad += total_gasto;

            updateDineroUser(id_moneda, cantidad);

            for(String t:tags){
                Cursor tag = getTagByNombre(t);
                int id_tag = 0;
                while (tag.moveToNext()) {
                    id_tag = tag.getInt(0);
                }
                boolean b = addTagGasto(id_tag, (int)result);
                if(!b){
                    return false;
                }
            }

            return true;
        }
    }

    // Devuelve -> fecha, cantidad, motivo, ingreso, nombreMoneda, simboloMoneda
    public Cursor getGastosBySessionUser(String desde, String hasta){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT g. "+COL_ID+", g."+COL_FECHA+", g."+COL_TOTAL_GASTO+", g."+COL_MOTIVO+", g."+COL_INGRESO
                +", mc."+COL_NOMBRE+", mc."+COL_SIMBOLO
                +" FROM "+TABLE_GASTO+" AS g INNER JOIN "+ TABLE_MONEDA +" AS mc "
                +"ON g."+ COL_ID_MONEDA +" = mc."+COL_ID+" AND g."+COL_FECHA+" BETWEEN '"+desde+"' AND '"+hasta+"'"
                +" INNER JOIN "+TABLE_USUARIO+" AS u "
                +"ON u."+COL_ID+" = g."+COL_ID_USUARIO+" AND u."+COL_ID+" = '"+UsuarioSingleton.getInstance().getID()+"'"
                +" ORDER BY g."+COL_FECHA+" DESC";
        return db.rawQuery(query, null);
    }

    //Devuelve cantidadGastos, total, nombreMoneda
    public Cursor getTotalGastosBetweenFechasGroupByMonedas(String desde, String hasta, String ingreso){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT COUNT(g."+COL_TOTAL_GASTO+"), SUM(g."+COL_TOTAL_GASTO+"), mc."+COL_NOMBRE+", mc."+COL_SIMBOLO
                +" FROM "+TABLE_GASTO+" AS g INNER JOIN "+ TABLE_MONEDA +" AS mc "
                +"ON g."+ COL_ID_MONEDA +" = mc."+COL_ID+" AND g."+COL_INGRESO+" = '"+ingreso+"' AND g."+COL_FECHA+" BETWEEN '"+desde+"' AND '"+hasta+"'"
                +" INNER JOIN "+TABLE_USUARIO+" AS u "
                +"ON u."+COL_ID+" = g."+COL_ID_USUARIO+" AND u."+COL_ID+" = '"+UsuarioSingleton.getInstance().getID()+"'"
                +" GROUP BY mc."+COL_NOMBRE;
        return db.rawQuery(query, null);
    }

    public Cursor getTotalGastosGroupByTagIngresoMoneda(String desde, String hasta){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT COUNT(g."+COL_TOTAL_GASTO+"), SUM(g."+COL_TOTAL_GASTO+"), mc."+COL_NOMBRE+", mc."+COL_SIMBOLO+", t."+COL_NOMBRE+", g."+COL_INGRESO
                +" FROM "+TABLE_GASTO+" AS g INNER JOIN "+ TABLE_MONEDA +" AS mc "
                +"ON g."+ COL_ID_MONEDA +" = mc."+COL_ID+" AND g."+COL_FECHA+" BETWEEN '"+desde+"' AND '"+hasta+"'"
                +" INNER JOIN "+ TABLE_TAG_GASTO +" AS tg "
                +"ON g."+ COL_ID +" = tg."+COL_ID_GASTO
                +" INNER JOIN "+TABLE_USUARIO+" AS u "
                +"ON u."+COL_ID+" = g."+COL_ID_USUARIO+" AND u."+COL_ID+" = '"+UsuarioSingleton.getInstance().getID()+"'"
                +" INNER JOIN "+TABLE_TAG+" AS t "
                +"ON t."+COL_ID+" = tg."+COL_ID_TAG
                +" GROUP BY g."+COL_INGRESO+", t."+COL_NOMBRE+", mc."+COL_ID;
        return db.rawQuery(query, null);
    }

    public Cursor getMonedaIdByNombre(String nombre_moneda){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MONEDA + " WHERE " + COL_NOMBRE +"='"+nombre_moneda+"'"
                + " AND " + COL_ID_USUARIO +"='"+ UsuarioSingleton.getInstance().getID()+"'";
        return db.rawQuery(query, null);
    }



    public boolean addUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USUARIO, null, contentValues);

        return result != -1;
    }


    public void updateDineroUser(int id_moneda, float cantidad){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CANTIDAD, cantidad);


        db.update(TABLE_MONEDA, contentValues, "ID = ?", new String[]{Integer.toString(id_moneda)});
    }
/*
    public Cursor getAllUsers(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getUserById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE ID='" + id +"'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }*/

    public Cursor getUserByUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE usuario='" + username +"'";
        return db.rawQuery(query, null);
    }

    public Cursor getUserByUsernameAndPassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE usuario='" + username + "' AND contraseña='" + password +"'";
        return db.rawQuery(query, null);
    }




    public boolean addTag(String tag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOMBRE, tag);

        long result = db.insert(TABLE_TAG, null, contentValues);

        if(result == -1){
            return false;
        }
        return addTagUsuario((int)result, UsuarioSingleton.getInstance().getID());
    }

   /* public Cursor getAllTags(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TAG ;
        return db.rawQuery(query, null);
    }*/

    public Cursor getTagsByUserId(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + TABLE_TAG + ".* FROM " + TABLE_TAG + ", " + TABLE_TAG_USUARIO + " WHERE " + TABLE_TAG_USUARIO + "." +COL_ID_USUARIO +"='" + id + "' AND "
                + TABLE_TAG +".ID=" + TABLE_TAG_USUARIO + "." + COL_ID_TAG;
        return db.rawQuery(query, null);
    }

    public Cursor getTagByNombre(String nombre){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TAG + " WHERE " + COL_NOMBRE + " = '" + nombre + "'";
        return db.rawQuery(query, null);
    }

    private boolean addTagUsuario(int tag, int id_usuario){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_TAG, tag);
        contentValues.put(COL_ID_USUARIO, id_usuario);  //Por ahora solo el 0, ya que solo permito una única cuenta

        long result = db.insert(TABLE_TAG_USUARIO, null, contentValues);

        return result != 1;
    }

    private boolean addTagGasto(int tag, int id_gasto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_TAG, tag);
        contentValues.put(COL_ID_GASTO, id_gasto);  //Por ahora solo el 0, ya que solo permito una única cuenta

        long result = db.insert(TABLE_TAG_GASTO, null, contentValues);

        return result != -1;
    }


    public boolean addMonedaCantidad(String moneda, float cantidad, String simbolo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOMBRE, moneda);
        contentValues.put(COL_CANTIDAD, cantidad);
        contentValues.put(COL_SIMBOLO, simbolo);
        contentValues.put(COL_ID_USUARIO, UsuarioSingleton.getInstance().getID());

        long result = db.insert(TABLE_MONEDA, null, contentValues);

        return result != -1;
    }

/*
    public Cursor getAllMonedas(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MONEDA;
        Cursor data = db.rawQuery(query, null);
        return data;

    }*/

    public Cursor getMonedasByUserId(int id_usuario){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MONEDA + " WHERE " + TABLE_MONEDA +"."+COL_ID_USUARIO + " = " + id_usuario;
        return db.rawQuery(query, null);
    }
}
