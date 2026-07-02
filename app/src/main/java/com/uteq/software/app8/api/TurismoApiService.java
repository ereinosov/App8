package com.uteq.software.app8.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.uteq.software.app8.model.Categoria;
import com.uteq.software.app8.model.LugarTuristico;
import com.uteq.software.app8.model.Subcategoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TurismoApiService {

    private static final String BASE_URL = "http://35.153.103.86/turismo10022025";

    private final RequestQueue requestQueue;

    public TurismoApiService(Context context) {
        requestQueue = ApiClient.getInstance(context).getRequestQueue();
    }

    public void getCategorias(final ApiCallback<List<Categoria>> callback) {
        String url = BASE_URL + "/categoria/getlistadoCB";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Categoria> categorias = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            categorias.add(new Categoria(item.getInt("id"), item.getString("descripcion")));
                        }
                    } catch (JSONException e) {
                        callback.onError("Error al leer las categorías");
                        return;
                    }
                    callback.onSuccess(categorias);
                },
                error -> callback.onError("Error al obtener categorías: " + error.getMessage())
        );
        requestQueue.add(request);
    }

    public void getSubcategorias(int categoriaId, final ApiCallback<List<Subcategoria>> callback) {
        String url = BASE_URL + "/subcategoria/getlistadoCB/" + categoriaId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Subcategoria> subcategorias = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            subcategorias.add(new Subcategoria(
                                    item.getInt("id"),
                                    item.getInt("categoria_id"),
                                    item.getString("descripcion")
                            ));
                        }
                    } catch (JSONException e) {
                        callback.onError("Error al leer las subcategorías");
                        return;
                    }
                    callback.onSuccess(subcategorias);
                },
                error -> callback.onError("Error al obtener subcategorías: " + error.getMessage())
        );
        requestQueue.add(request);
    }

    public void getLugares(double lat, double lng, float radio, final ApiCallback<List<LugarTuristico>> callback) {
        String url = BASE_URL + "/lugar_turistico/json_getlistadoMapa?lat=" + lat + "&lng=" + lng + "&radio=" + (radio / 10.0);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    List<LugarTuristico> lugares = new ArrayList<>();
                    try {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            lugares.add(new LugarTuristico(
                                    item.getInt("id"),
                                    item.getString("nombre"),
                                    item.getDouble("lat"),
                                    item.getDouble("lng"),
                                    item.optInt("categoria_id", -1),
                                    item.optInt("subcategoria_id", -1)
                            ));
                        }
                    } catch (JSONException e) {
                        callback.onError("Error al leer los lugares turísticos");
                        return;
                    }
                    callback.onSuccess(lugares);
                },
                error -> callback.onError("Error al obtener lugares turísticos: " + error.getMessage())
        );
        requestQueue.add(request);
    }
}
