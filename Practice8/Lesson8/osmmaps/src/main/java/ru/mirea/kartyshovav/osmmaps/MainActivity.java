package ru.mirea.kartyshovav.osmmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private MapView mapView;

    private final GeoPoint[] places = {
            new GeoPoint(55.715742, 37.553728),  // Лужники
            new GeoPoint(55.819734, 37.611641),  // Останкинская башня
            new GeoPoint(55.777782, 37.793786)   // Измайлово
    };

    private final String[] names = {
            "Стадион Лужники",
            "Останкинская телебашня",
            "Лесопарк Измайлово"
    };

    private final String[] descriptions = {
            "Крупнейший спортивный комплекс России",
            "Телебашня высотой 540 метров",
            "Популярное место для прогулок"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }

        // Настройка карты
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        // Центр карты
        IMapController mapController = mapView.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(places[0]);

        // Слой местоположения
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(this), mapView
        );
        locationOverlay.enableMyLocation(); // Получаем GPS данные
        mapView.getOverlays().add(locationOverlay);

        // Компас
        CompassOverlay compassOverlay = new CompassOverlay(this,
                new InternalCompassOrientationProvider(this), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        // Шкала масштаба
        addScaleBar();

        // Маркеры
        addMarkers();
    }

    private void addMarkers() {
        for (int i = 0; i < places.length; i++) {
            Marker marker = new Marker(mapView);
            marker.setPosition(places[i]);
            marker.setTitle(names[i]);
            marker.setSubDescription(descriptions[i]);
            marker.setIcon(ResourcesCompat.getDrawable(getResources(),
                    android.R.drawable.star_big_on, null));

            final String name = names[i];
            final String desc = descriptions[i];
            marker.setOnMarkerClickListener((m, v) -> {
                Toast.makeText(this, name + "\n" + desc, Toast.LENGTH_LONG).show();
                return true;
            });
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate();
    }

    private void addScaleBar() {
        final Context context = this.getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView); // Текущий масштаб
        scaleBarOverlay.setCentred(true); // По центру
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10); // Позиция
        mapView.getOverlays().add(scaleBarOverlay);
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