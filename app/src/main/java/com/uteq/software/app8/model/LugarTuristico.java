package com.uteq.software.app8.model;

public class LugarTuristico {

    private final int id;
    private final String nombre;
    private final double lat;
    private final double lng;
    private final int categoriaId;
    private final int subcategoriaId;

    public LugarTuristico(int id, String nombre, double lat, double lng, int categoriaId, int subcategoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.lat = lat;
        this.lng = lng;
        this.categoriaId = categoriaId;
        this.subcategoriaId = subcategoriaId;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public int getSubcategoriaId() {
        return subcategoriaId;
    }
}
