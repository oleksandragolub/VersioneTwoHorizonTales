package com.example.versionetwohorizontales.ui.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.versionetwohorizontales.R;
import com.example.versionetwohorizontales.data.repository.user.UserRepository;
import com.example.versionetwohorizontales.data.source.user.UserRemoteDataSource;
import com.example.versionetwohorizontales.ui.main.viewmodel.MainViewModel;
import com.example.versionetwohorizontales.ui.main.viewmodelfactory.MainViewModelFactory;
import com.example.versionetwohorizontales.ui.welcome.fragment.WelcomeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private BottomNavigationView bottomNavigationView;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Crea un'istanza di UserRemoteDataSource e passala a UserRepository
        UserRemoteDataSource userRemoteDataSource = new UserRemoteDataSource(this); // Passa il contesto
        UserRepository userRepository = new UserRepository(userRemoteDataSource);

        MainViewModelFactory factory = new MainViewModelFactory(userRepository);
        mainViewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        setupUI();
        observeViewModel();


        // Listener per gestire i click sul menu laterale
        navView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Log.d("MenuItem", "Clicked item ID: " + id);

            if (id == R.id.navigation_logout) {
                mainViewModel.logout();  // ViewModel handles the logout logic
                navigateToWelcomeActivity();
                return true;
            }

            NavigationUI.onNavDestinationSelected(menuItem, navController);
            drawerLayout.closeDrawer(GravityCompat.END);  // Close the drawer
            return true;
        });


    }

    private void setupUI() {
        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);

        // Setup BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Setup AppBarConfiguration for DrawerLayout and BottomNavigationView
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_category_user, R.id.navigation_profile)
                .setOpenableLayout(drawerLayout)
                .build();

        // Link Toolbar and NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Link BottomNavigationView and NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Setup side menu icon click
        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);  // Open the drawer on the right
            }
        });
    }

    private void observeViewModel() {
        mainViewModel.getUserLiveData().observe(this, user -> {
            if (user == null) {
                navigateToWelcomeActivity();  // Redirect to login if user is null (logged out)
            }
        });
    }

    private void navigateToWelcomeActivity() {
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}