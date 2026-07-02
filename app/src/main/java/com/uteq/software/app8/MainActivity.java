package com.uteq.software.app8;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.slider.Slider;
import com.uteq.software.app8.api.ApiCallback;
import com.uteq.software.app8.api.TurismoApiService;
import com.uteq.software.app8.model.Categoria;
import com.uteq.software.app8.model.LugarTuristico;
import com.uteq.software.app8.model.Subcategoria;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mapa;
    Double lat, lng;
    float radio;
    Circle circulo = null;

    Slider sliderRadio;
    EditText txtLatitud, txtLongitud;
    Spinner spinnerCategoria, spinnerSubcategoria;
    ArrayList<Marker> markers = new ArrayList<Marker>();

    TurismoApiService apiService;
    ArrayAdapter<Categoria> categoriaAdapter;
    ArrayAdapter<Subcategoria> subcategoriaAdapter;

    List<LugarTuristico> lugaresCompletos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Punto Inicial
        lat = -1.02313;
        lng = -79.459561;
        radio = 1;

        apiService = new TurismoApiService(this);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        sliderRadio = findViewById(R.id.sliderRadio);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerSubcategoria = findViewById(R.id.spinnerSubcategoria);

        configurarSpinners();
        cargarCategorias();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sliderRadio.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                radio = slider.getValue();
                updateInterfaz();
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                radio = slider.getValue();
                updateInterfaz();
            }
        });
    }

    private void configurarSpinners() {
        categoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<Categoria>());
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(categoriaAdapter);

        subcategoriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<Subcategoria>());
        subcategoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategoriaAdapter.add(Subcategoria.todas());
        spinnerSubcategoria.setAdapter(subcategoriaAdapter);

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Categoria seleccionada = (Categoria) parent.getItemAtPosition(position);
                if (seleccionada == null || seleccionada.getId() == Categoria.ID_TODAS) {
                    subcategoriaAdapter.clear();
                    subcategoriaAdapter.add(Subcategoria.todas());
                    subcategoriaAdapter.notifyDataSetChanged();
                    aplicarFiltro();
                } else {
                    cargarSubcategorias(seleccionada.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSubcategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void cargarCategorias() {
        apiService.getCategorias(new ApiCallback<List<Categoria>>() {
            @Override
            public void onSuccess(List<Categoria> result) {
                categoriaAdapter.clear();
                categoriaAdapter.add(Categoria.todas());
                categoriaAdapter.addAll(result);
                categoriaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSubcategorias(int categoriaId) {
        apiService.getSubcategorias(categoriaId, new ApiCallback<List<Subcategoria>>() {
            @Override
            public void onSuccess(List<Subcategoria> result) {
                subcategoriaAdapter.clear();
                subcategoriaAdapter.add(Subcategoria.todas());
                subcategoriaAdapter.addAll(result);
                subcategoriaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.getUiSettings().setZoomControlsEnabled(true);

        LatLng initialPos = new LatLng(lat, lng);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPos, 15));

        mapa.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = mapa.getCameraPosition().target;
                lat = center.latitude;
                lng = center.longitude;
                updateInterfaz();
            }
        });

        mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        updateInterfaz();
    }

    private void updateInterfaz() {
        txtLatitud.setText(String.format(Locale.US, "%.4f", lat));
        txtLongitud.setText(String.format(Locale.US, "%.4f", lng));

        if (circulo != null) {
            circulo.remove();
            circulo = null;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radio * 100)
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50, 150, 50, 50));

        circulo = mapa.addCircle(circleOptions);

        apiService.getLugares(lat, lng, radio, new ApiCallback<List<LugarTuristico>>() {
            @Override
            public void onSuccess(List<LugarTuristico> result) {
                lugaresCompletos = result;
                aplicarFiltro();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltro() {
        if (mapa == null) return;

        for (Marker marker : markers) marker.remove();
        markers.clear();

        Categoria categoriaSel = (Categoria) spinnerCategoria.getSelectedItem();
        Subcategoria subcategoriaSel = (Subcategoria) spinnerSubcategoria.getSelectedItem();

        for (LugarTuristico lugar : lugaresCompletos) {
            boolean coincideCategoria = categoriaSel == null || categoriaSel.getId() == Categoria.ID_TODAS
                    || lugar.getCategoriaId() == categoriaSel.getId();
            boolean coincideSubcategoria = subcategoriaSel == null || subcategoriaSel.getId() == Subcategoria.ID_TODAS
                    || lugar.getSubcategoriaId() == subcategoriaSel.getId();

            if (coincideCategoria && coincideSubcategoria) {
                markers.add(mapa.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(lugar.getLat(), lugar.getLng()))
                                .title(lugar.getNombre())
                ));
            }
        }
    }
}
