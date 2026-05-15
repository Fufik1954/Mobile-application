package ru.mirea.kartyshovav.mireaproject;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настройка тулбара
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Шторка
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Контейнер для фрагментов
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view);

        // Контроллер
        NavController navController = navHostFragment.getNavController();

        // Добавяем пункты меню в тулбар
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_data,
                R.id.nav_webview,
                R.id.nav_task)
                .setOpenableLayout(drawer)
                .build();

        // Связываем тулбар
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // Связываем меню
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Находим контроллер по ID контейнера
        NavController navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_view)).getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}