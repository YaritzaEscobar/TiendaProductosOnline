package com.ugb.tiendaproductosonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class adaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<productos> datosProductosArrayList;
    productos misProductos;
    LayoutInflater layoutInflater;

    public adaptadorImagenes(Context context, ArrayList<productos> datosProductosArrayList) {
        this.context = context;
        this.datosProductosArrayList = datosProductosArrayList;
    }
    @Override
    public int getCount() {
        return datosProductosArrayList.size();
    }
    @Override
    public Object getItem(int i) {
        return datosProductosArrayList.get(i);
    }
    @Override
    public long getItemId(int i) {
        return i; //Long.parseLong(datosProductosArrayList.get(i).getIdProducto());
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);
        try{
            misProductos = datosProductosArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblCodigo);
            tempVal.setText(misProductos.getCodigo());

            tempVal = itemView.findViewById(R.id.lblNombre);
            tempVal.setText(misProductos.getNombre());

            tempVal = itemView.findViewById(R.id.lblMarca);
            tempVal.setText(misProductos.getMarca());

            tempVal= itemView.findViewById(R.id.lblCosto);
            tempVal.setText(misProductos.getCosto());

            tempVal= itemView.findViewById(R.id.lblStock);
            tempVal.setText(misProductos.getStock());

            tempVal= itemView.findViewById(R.id.lblGanancia);
            tempVal.setText(misProductos.getGanancia());

            tempVal = itemView.findViewById(R.id.lblDescripcion);
            tempVal.setText(misProductos.getDescripcion());

            //Imagen----
            CircleImageView circleImageView = itemView.findViewById(R.id.imgProductoListVista);
            Bitmap bitmap = BitmapFactory.decodeFile(misProductos.getImgProducto());
            circleImageView.setImageBitmap(bitmap);

            // Calcular el precio
            double costo = Double.parseDouble(misProductos.getCosto());
            double ganancia = Double.parseDouble(misProductos.getGanancia());
            double precio = calcularPrecio(costo, ganancia);

            // Mostrar el precio en la vista
            tempVal = itemView.findViewById(R.id.lblPrecio);
            tempVal.setText(String.format("$%.2f", precio));


        }catch (Exception e){
            Toast.makeText(context, "Error al mostrar datos: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return itemView;
    }

    // Función para calcular el precio
    private double calcularPrecio(double costo, double ganancia) {
        return costo * (1 + (ganancia / 100));
    }

}
