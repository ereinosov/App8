package com.uteq.software.app8.model;

public class Subcategoria {

    public static final int ID_TODAS = -1;

    private final int id;
    private final int categoriaId;
    private final String descripcion;

    public Subcategoria(int id, int categoriaId, String descripcion) {
        this.id = id;
        this.categoriaId = categoriaId;
        this.descripcion = descripcion;
    }

    public static Subcategoria todas() {
        return new Subcategoria(ID_TODAS, ID_TODAS, "Todas");
    }

    public int getId() {
        return id;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
