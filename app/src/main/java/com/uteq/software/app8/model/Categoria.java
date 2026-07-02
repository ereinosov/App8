package com.uteq.software.app8.model;

public class Categoria {

    public static final int ID_TODAS = -1;

    private final int id;
    private final String descripcion;

    public Categoria(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public static Categoria todas() {
        return new Categoria(ID_TODAS, "Todas");
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
