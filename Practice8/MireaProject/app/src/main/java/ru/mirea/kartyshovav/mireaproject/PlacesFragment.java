package ru.mirea.kartyshovav.mireaproject;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class PlacesFragment extends Fragment {

    private MapView mapView;
    private IMapController mapController;

    private final GeoPoint[] places = {
            new GeoPoint(55.781805, 37.425625),  // Серебряный бор
            new GeoPoint(55.748302, 37.483761),  // Парк Фили
            new GeoPoint(55.669546, 37.370242)   // Парк Мещерский
    };

    private final String[] names = {
            "Серебряный бор",
            "Парк Фили",
            "Парк Мещерский"
    };

    private final String[] addresses = {
            "Москва, Серебряный Бор",
            "Москва, ул. Большая Филёвская, 22",
            "Москва, посёлок Мещерский"
    };

    private final String[] descriptions = {
            "Лесопарк на искусственном острове, пляжи, экотропы",
            "Пейзажный парк с историей, усадьба Нарышкиных",
            "Лесопарк с прудами, лыжероллерная трасса"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация
        Configuration.getInstance().load(requireContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()));

        // Настройка карты
        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        mapView.setZoomRounding(true);
        mapView.setBuiltInZoomControls(false);

        // Управление камерой
        mapController = mapView.getController();
        mapController.setZoom(12.0);

        double centerLat = (55.781805 + 55.748302 + 55.669546) / 3;
        double centerLon = (37.425625 + 37.483761 + 37.370242) / 3;
        mapController.setCenter(new GeoPoint(centerLat, centerLon));

        // местоположение
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(requireContext()), mapView);
        locationOverlay.enableMyLocation(); // Данные с gps
        mapView.getOverlays().add(locationOverlay);

        // Компас
        CompassOverlay compassOverlay = new CompassOverlay(requireContext(),
                new InternalCompassOrientationProvider(requireContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Шкала масштаба
        addScaleBar();

        // Добавляем маркеры
        addMarkers();

        // Настройка кнопок зума
        setupZoomButtons(view);
    }

    private void addMarkers() {
        for (int i = 0; i < places.length; i++) {
            Marker marker = new Marker(mapView);
            marker.setPosition(places[i]);
            marker.setTitle(names[i]);
            marker.setSubDescription(addresses[i]);

            marker.setIcon(ResourcesCompat.getDrawable(getResources(),
                    android.R.drawable.star_big_on, null));

            final int index = i;
            marker.setOnMarkerClickListener((m, v) -> {
                showPlaceInfo(names[index], addresses[index], descriptions[index]);
                return true;
            });
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }

    private void showPlaceInfo(String name, String address, String description) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(name)
                .setMessage("" + address + "\n\n" + description)
                .setPositiveButton("OK", null)
                .show();
    }

    private void addScaleBar() {
        final Context context = requireContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    // Кнопки для изменения масштаба
    private void setupZoomButtons(View view) {
        Button btnZoomIn = view.findViewById(R.id.btn_zoom_in);
        Button btnZoomOut = view.findViewById(R.id.btn_zoom_out);

        btnZoomIn.setOnClickListener(v -> {
            mapController.zoomIn();
        });

        btnZoomOut.setOnClickListener(v -> {
            mapController.zoomOut();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }
}