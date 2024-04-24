package com.ugb.tiendaproductosonline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class lista_cindy extends AppCompatActivity {

    //VARIABLES
    Bundle parametros = new Bundle();
    FloatingActionButton btnIragregar;
    ListView lts;
    Cursor cProductos;
    DB_yaritza dbProductos;
    productos misProductos;
    final ArrayList<productos> alProductos=new ArrayList<productos>();
    final ArrayList<productos> alProductosCopy=new ArrayList<productos>();
    JSONArray datosJSON;
    JSONObject jsonObject;
    obtenerDatosServidor datosServidor;
    detectarInternet di;
    int posicion=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_cindy);

        //cambiar color barra estado
        cambiarColorBarraEstado(getResources().getColor(R.color.darkblue));

        dbProductos = new DB_yaritza(lista_cindy.this,"",null,1);


        //Boton para cambiar de activitys, ir a agregar
        btnIragregar = findViewById(R.id.btnAbrirNuevosProductos);
        btnIragregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion", "nuevo");
                irAgregar(parametros);
            }
        });
        try{
            di = new detectarInternet(getApplicationContext());
            if(di.hayConexionInternet()){ //online
                obtenerDatosProductosServidor();
                sincronizar();
            }else{
                obtenerProductos();//offline
            }
        }catch (Exception e){
            mostrarMsg("Error al detectar si hay conexion "+ e.getMessage());
        }
        buscarProductos();

    } //fin ONCREATE

    //AGREGAR PRIVATE VOIDS
    private void sincronizar(){
        try{
            cProductos = dbProductos.pendienteSincronizar();
            if(cProductos.moveToFirst()){ //hay registros pendientes de sincronizar
                jsonObject = new JSONObject();
                mostrarMsg("Sincronizando...");
                do{
                    if(cProductos.getString(0).length()>0 && cProductos.getString(1).length()>0){
                        jsonObject.put("_id", cProductos.getString(0));
                        jsonObject.put("_rev", cProductos.getString(1));

                    }
                    jsonObject.put("idProducto", cProductos.getString(2));
                    jsonObject.put("codigo", cProductos.getString(3));
                    jsonObject.put("nombre", cProductos.getString(4));
                    jsonObject.put("marca", cProductos.getString(5));
                    jsonObject.put("costo", cProductos.getString(6));
                    jsonObject.put("stock", cProductos.getString(7));
                    jsonObject.put("ganancia", cProductos.getString(8));
                    jsonObject.put("descripcion", cProductos.getString(9));
                    jsonObject.put("imgproducto", cProductos.getString(10));
                    jsonObject.put("actualizado", "si");

                    enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                    String respuesta = objGuardarDatosServidor.execute(jsonObject.toString()).get();

                    JSONObject respuestaJSONObject = new JSONObject(respuesta);
                    if (respuestaJSONObject.getBoolean("ok")) {
                        DB_yaritza db = new DB_yaritza(getApplicationContext(), "", null, 1);
                        String[] datos = new String[]{
                                respuestaJSONObject.getString("id"),
                                respuestaJSONObject.getString("rev"),
                                jsonObject.getString("idProducto"),
                                jsonObject.getString("codigo"),
                                jsonObject.getString("nombre"),
                                jsonObject.getString("marca"),
                                jsonObject.getString("costo"),
                                jsonObject.getString("stock"),
                                jsonObject.getString("ganancia"),
                                jsonObject.getString("descripcion"),
                                jsonObject.getString("imgproducto"),
                                jsonObject.getString("actualizado")
                        };
                        respuesta = db.administrar_productos("modificar", datos);
                        if (!respuesta.equals("ok")) {
                            mostrarMsg(respuesta);
                        }
                    } else{
                        mostrarMsg("Error al sincronizar en servidor: " + respuesta);
                    }
                }while (cProductos.moveToNext());
            }
        }catch (Exception e){
            mostrarMsg("Error al sincronizar: "+e.getMessage());
        }
    }

    private void obtenerDatosProductosServidor(){
        try{
            datosServidor = new obtenerDatosServidor();
            String data = datosServidor.execute().get();
            jsonObject = new JSONObject(data);
            datosJSON = jsonObject.getJSONArray("rows");
            mostrarDatosProductos();
        }catch (Exception e){
            mostrarMsg("Error al obtener datos desde el servidor: "+ e.getMessage());
        }
    }

    private void mostrarDatosProductos(){
        try{
            if( datosJSON.length()>0 ){
                lts = findViewById(R.id.ltsProductos);

                alProductos.clear();
                alProductosCopy.clear();

                JSONObject misDatosJSONObject;
                for (int i=0; i<datosJSON.length(); i++){
                    misDatosJSONObject = datosJSON.getJSONObject(i).getJSONObject("value");
                    misProductos = new productos(
                            misDatosJSONObject.getString("_id"),
                            misDatosJSONObject.getString("_rev"),
                            misDatosJSONObject.getString("idProducto"),
                            misDatosJSONObject.getString("codigo"),
                            misDatosJSONObject.getString("nombre"),
                            misDatosJSONObject.getString("marca"),
                            misDatosJSONObject.getString("costo"),
                            misDatosJSONObject.getString("stock"),
                            misDatosJSONObject.getString("ganancia"),
                            misDatosJSONObject.getString("descripcion"),
                            misDatosJSONObject.getString("imgproducto")
                    );
                    alProductos.add(misProductos);
                }
                adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alProductos);
                lts.setAdapter(adImagenes);
                alProductosCopy.addAll(alProductos);

                registerForContextMenu(lts);
            }else{
                mostrarMsg("No hay datos que mostrar.");
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos: "+e.getMessage());
        }
    }

    //MENU
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            posicion = info.position;
            menu.setHeaderTitle(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("nombre"));
        }catch (Exception e){
            mostrarMsg("Error al mostrar el menu: "+ e.getMessage());
        }
    }

    //Agregar, modificar y eliminar
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()){
                case R.id.mnxAgregar:
                    parametros.putString("accion", "nuevo");
                    irAgregar(parametros);
                    break;
                case R.id.mnxModificar:
                    parametros.putString("accion","modificar");
                    parametros.putString("productos", datosJSON.getJSONObject(posicion).toString());
                    irAgregar(parametros);
                    break;
                case R.id.mnxEliminar:
                    eliminarProductos();
                    break;
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error en menu: "+ e.getMessage());
            return super.onContextItemSelected(item);
        }

    }


    //Eliminar productos
    private void eliminarProductos(){
        try {
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(lista_cindy.this);
            confirmacion.setTitle("Esta seguro de Eliminar a: ");
            confirmacion.setMessage(datosJSON.getJSONObject(posicion).getJSONObject("value").getString("nombre"));
            confirmacion.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        String respuesta = dbProductos.administrar_productos("eliminar", new String[]{"", "", datosJSON.getJSONObject(posicion).getJSONObject("value").getString("idProducto")});
                        if (respuesta.equals("ok")) {
                            mostrarMsg("Producto eliminado con exito.");
                            obtenerProductos();
                        } else {
                            mostrarMsg("Error al eliminar el producto: " + respuesta);
                        }
                    }catch (Exception e){
                        mostrarMsg("Error al eliminar Datos: "+e.getMessage());
                    }
                }
            });
            confirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            confirmacion.create().show();
        }catch (Exception e){
            mostrarMsg("Error al eliminar: "+ e.getMessage());
        }
    }

    //obtenet productos tienda
    private void obtenerProductos(){
        try{
            dbProductos = new DB_yaritza(lista_cindy.this, "", null, 1);
            cProductos = dbProductos.consultar_productos();
            if ( cProductos.moveToFirst() ){
                datosJSON = new JSONArray();
                do{
                    jsonObject = new JSONObject();
                    JSONObject jsonObjectValue = new JSONObject();
                    jsonObject.put("_id",cProductos.getString(0));
                    jsonObject.put("_rev",cProductos.getString(1));
                    jsonObject.put("idProducto",cProductos.getString(2));
                    jsonObject.put("codigo",cProductos.getString(3));
                    jsonObject.put("nombre",cProductos.getString(4));
                    jsonObject.put("marca",cProductos.getString(5));
                    jsonObject.put("costo",cProductos.getString(6));
                    jsonObject.put("stock",cProductos.getString(7));
                    jsonObject.put("ganancia",cProductos.getString(8));
                    jsonObject.put("descripcion",cProductos.getString(9));
                    jsonObject.put("imgproducto",cProductos.getString(10));

                    jsonObjectValue.put("value", jsonObject);
                    datosJSON.put(jsonObjectValue);
                    //alProductos.add(misProductos);
                }while(cProductos.moveToNext());
                mostrarDatosProductos();
            }else{
                mostrarMsg("No hay productos que mostrar");
            }
        }catch (Exception e){
            mostrarMsg("Error al obtener productos: "+ e.getMessage());
        }
    }

    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //Buscar productos skincare
    private void buscarProductos(){

        TextView tempVal;
        tempVal = findViewById(R.id.txt_busqueda);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    alProductos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if( valor.length()<=0 ){
                        alProductos.addAll(alProductosCopy);
                    }else{
                        for( productos producto : alProductosCopy ){

                            String codigo = producto.getCodigo();
                            String nombre = producto.getNombre();
                            String marca = producto.getMarca();
                            String costo = producto.getCosto();
                            String stock = producto.getStock();
                            String ganancia = producto.getGanancia();
                            String descripcion = producto.getDescripcion();

                            if( codigo.toLowerCase().trim().contains(valor) ||
                                    nombre.toLowerCase().trim().contains(valor) ||
                                    marca.trim().contains(valor) ||
                                    costo.trim().toLowerCase().contains(valor) ||
                                    stock.trim().toLowerCase().contains(valor) ||
                                    ganancia.trim().toLowerCase().contains(valor) ||
                                    descripcion.trim().toLowerCase().contains(valor) ){
                                alProductos.add(producto);
                            }
                        }
                        adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alProductos);
                        lts.setAdapter(adImagenes);
                    }
                }catch (Exception e){
                    mostrarMsg("Error al buscar: "+e.getMessage() );
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Metodo para ir al activity de agregar
    private void irAgregar(Bundle parametros){
        Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
        abrirVentana.putExtras(parametros); //abrir actividad y modificar
        startActivity(abrirVentana);

    }

    //Metodo para cambiar el color de la barra de estado
    private void cambiarColorBarraEstado(int color) {
        // Verificar si la versión del SDK es Lollipop o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    } //fin cambiar colorbarraestado


} //fin lista_productos cindy