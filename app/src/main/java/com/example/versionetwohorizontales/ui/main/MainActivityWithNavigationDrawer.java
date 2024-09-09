package com.example.versionetwohorizontales.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.versionetwohorizontales.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityWithNavigationDrawer extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_with_navigation_drawer);

        // Setup della toolbar
        MaterialToolbar toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        // Setup del DrawerLayout e NavigationView
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Setup NavHostFragment e NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Setup AppBarConfiguration per DrawerLayout e BottomNavigationView
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_profile)
                .setOpenableLayout(drawerLayout)  // Indica che drawerLayout è apribile
                .build();

        // Configura il collegamento tra Toolbar e NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Configura il collegamento tra NavigationView (menu laterale) e NavController
        NavigationUI.setupWithNavController(navigationView, navController);

        // For the NavigationView (NavigationDrawer)
        NavigationUI.setupWithNavController(navView, navController);

        // Aggiungi il listener per gestire la selezione del menu
        navView.setNavigationItemSelectedListener(item -> handleMenuSelection(item));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    private boolean handleMenuSelection(MenuItem item) {
        int id = item.getItemId();

        NavDestination navDestination = navController.getCurrentDestination();
        if (navDestination != null) {
            int currentDestination = navController.getCurrentDestination().getId();

           /* if (id == R.id.navigation_category_user) {
                if (currentDestination != R.id.fragment_category_user) {
                    navView.setCheckedItem(R.id.navigation_category_user);
                    openFragment(new CategoryUserFragment());
                }
            } else if (id == R.id.searchUserFragment) {
                if (currentDestination != R.id.fragment_search_user) {
                    navView.setCheckedItem(R.id.searchUserFragment);
                    openFragment(new SearchUserFragment());
                }
            } else if (id == R.id.chatsFragment) {
                if (currentDestination != R.id.fragment_chats) {
                    navView.setCheckedItem(R.id.chatsFragment);
                    openFragment(new ChatsFragment());
                }
            } else if (id == R.id.navigation_character_info) {
                if (currentDestination != R.id.fragment_character_info) {
                    navView.setCheckedItem(R.id.navigation_character_info);
                    openFragment(new CharacterInfoFragment());
                }
            } else if (id == R.id.nav_marvel_comics_detail) {
                if (currentDestination != R.id.fragment_comics_marvel_detail) {
                    navView.setCheckedItem(R.id.nav_marvel_comics_detail);
                    openFragment(new ComicsMarvelDetailFragment());
                }
            } else if (id == R.id.navigation_api_comics_user) {
                if (currentDestination != R.id.fragment_comics_api_user) {
                    navView.setCheckedItem(R.id.navigation_api_comics_user);
                    openFragment(new ComicsApiUserFragment());
                }
            } else if (id == R.id.navigation_comics_user) {
                if (currentDestination != R.id.fragment_comics_user) {
                    navView.setCheckedItem(R.id.navigation_comics_user);
                    openFragment(new ComicsUserFragment());
                }
            } else*/ if (id == R.id.navigation_logout) {
                // Effettua il logout da Firebase Auth
                FirebaseAuth.getInstance().signOut();
                // Effettua il logout da Google Sign-In
              //  signOutFromGoogle();
                // Passa alla LoginActivity
              /*  Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;*/
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
