package com.ugb.tiendaproductosonline;

public class productos{
    String _id;
    String _rev;
    String idProducto;
    String codigo;
    String nombre;
    String marca;
    String costo;
    String stock;
    String ganancia;
    String descripcion;
    String imgproducto;

    public productos(String _id, String _rev, String idProducto, String codigo, String nombre, String marca,
                     String costo, String stock, String ganancia, String descripcion, String imgproducto) {
        this._id = _id;
        this._rev = _rev;
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.costo = costo;
        this.stock = stock;
        this.ganancia = ganancia;
        this.descripcion = descripcion;
        this.imgproducto = imgproducto;

    }

    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_rev() {
        return _rev;
    }
    public void set_rev(String _rev) {
        this._rev = _rev;
    }
    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getGanancia() {
        return ganancia;
    }

    public void setGanancia(String ganancia) {
        this.ganancia = ganancia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImgProducto() {
        return imgproducto;
    }

    public void setImgProducto(String imgproducto) {
        this.imgproducto = imgproducto;
    }


}