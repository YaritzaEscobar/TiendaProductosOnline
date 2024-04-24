package com.ugb.tiendaproductosonline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB_yaritza extends SQLiteOpenHelper {
    private  static  final String dbname = "db_productos";
    private  static final int v=1;
    private static  final String SQldb = "CREATE TABLE productos(id text, rev text, idProducto text, codigo text, nombre text, marca text, costo text, stock text, ganancia text, descripcion text, imgproducto text, actualizado text)";

    public DB_yaritza(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQldb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //para actualizar la base de datos
    }

    //ADMINISTRAR LOS PRODUCTOS SKINCARE
    public String administrar_productos(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            if (accion.equals("nuevo")){
                db.execSQL("INSERT INTO productos(id,rev,idProducto,codigo,nombre,marca,costo,stock,ganancia,descripcion,imgproducto,actualizado) VALUES('"+ datos[0] +"', '"+ datos[1] +"', '"+
                        datos[2] +"', '"+ datos[3] +"','"+ datos[4] +"','"+ datos[5] +"','"+ datos[6] +"','"+ datos[7] +"', '"+ datos[8] +"','"+ datos[9] +"', '"+ datos[10] +"',  '"+datos[11]+"' )");
            } else if (accion.equals("modificar")) {
                db.execSQL("UPDATE productos SET id='"+ datos[0] +"',rev='"+ datos[1] +"',codigo='"+ datos[3] +"',nombre='"+ datos[4] +"',marca='"+ datos[5] +"',costo='"+ datos[6] +
                        "',stock='"+ datos[7] +"',ganancia='"+ datos[8] +"',descripcion='"+ datos[9] + "',imgproducto='"+ datos[10] +"',actualizado='"+ datos[11] +   "' WHERE idProducto='"+ datos[2] +"'");
            } else if (accion.equals("eliminar")) {
                db.execSQL("DELETE FROM productos WHERE idProducto='"+ datos[2] +"'");
            }
            return "ok";
        } catch (Exception e){
            return  "Error: "+ e.getMessage();

        }
    }

    //CURSOS CONSULTAR PRODUCTOS
    public Cursor consultar_productos(){
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos ORDER BY nombre", null);
        return cursor;
    }

    public Cursor pendienteSincronizar(){
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from productos where actualizado='no'", null);
        return cursor;
    }

}
