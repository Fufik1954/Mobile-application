package ru.mirea.kartyshovav.yandexdriver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private static final int REQUEST_CODE_PERMISSION = 200;
    private final Point ROUTE_START_LOCATION = new Point(55.757226, 37.427861);
    private final Point ROUTE_END_LOCATION = new Point(55.711517, 37.544390);
    private final Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude() + ROUTE_END_LOCATION.getLatitude()) / 2,
            (ROUTE_START_LOCATION.getLongitude() + ROUTE_END_LOCATION.getLongitude()) / 2
    );
    private final int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF00BBBB, 0xFF0000FF};

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация MapKit
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        checkPermissions();

        mapView = findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(new CameraPosition(SCREEN_CENTER, 10, 0, 0));

        // Инициализация роутера для построения маршрутов
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED);
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        // Добавляем маркер на конечную точку
        addMarkerOnEndPoint();

        // Отправляем запрос на построение маршрута
        submitRequest();
    }

    // Проверка разрешений на геолокацию
    private void checkPermissions() {
        int coarsePermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int finePermissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (coarsePermissionStatus != PackageManager.PERMISSION_GRANTED ||
                finePermissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION);
        }
    }

    // Добавление маркера на конечную точку
    private void addMarkerOnEndPoint() {
        PlacemarkMapObject marker = mapView.getMap().getMapObjects().addPlacemark(
                ROUTE_END_LOCATION,
                ImageProvider.fromResource(this, android.R.drawable.star_big_on)
        );

        marker.setZIndex(100f);
        marker.setDraggable(false);

        marker.addTapListener((mapObject, point) -> {
            // Показываем Toast
            Toast.makeText(MainActivity.this,
                    "Любимое место\nКоординаты: " +
                            ROUTE_END_LOCATION.getLatitude() + ", " +
                            ROUTE_END_LOCATION.getLongitude(),
                    Toast.LENGTH_LONG).show();
            return true;
        });
    }

    // Отправка запроса на построение маршрутов
    private void submitRequest() {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        drivingOptions.setRoutesCount(4); // Количество альтернативных путей

        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        // Устновка начальной и конечной точки
        requestPoints.add(new RequestPoint(ROUTE_START_LOCATION, RequestPointType.WAYPOINT, null, null, null));
        requestPoints.add(new RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null, null, null));
        // Отправка запрос на сервер
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
    }

    // Обработка успешного получения маршрутов
    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        mapObjects.clear();
        addMarkerOnEndPoint();

        for (int i = 0; i < list.size(); i++) {
            int color = colors[i % colors.length];
            mapObjects.addPolyline(list.get(i).getGeometry()).setStrokeColor(color);
        }
        Toast.makeText(this, "Найдено маршрутов: " + list.size(), Toast.LENGTH_SHORT).show();
    }

    // Обработка ошибки при построении маршрутов
    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        Toast.makeText(this, "Ошибка: " + error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}