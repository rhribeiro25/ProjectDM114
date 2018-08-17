package br.com.renan.projetodm114;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.renan.projetodm114.Fragments.GCMFragment;
import br.com.renan.projetodm114.Fragments.InterestProductsFragment;
import br.com.renan.projetodm114.Fragments.LoginFragment;
import br.com.renan.projetodm114.Fragments.OrdersFragment;
import br.com.renan.projetodm114.Fragments.ProductsFragment;
import br.com.renan.projetodm114.Fragments.SettingsFragment;
import br.com.renan.projetodm114.Fragments.HomeFragment;
import br.com.renan.projetodm114.models.ProductInfo;
import br.com.renan.projetodm114.tasks.TokenEvents;
import br.com.renan.projetodm114.tasks.TokenGCMEvents;
import br.com.renan.projetodm114.tasks.TokenGCMTask;
import br.com.renan.projetodm114.tasks.TokenTasks;
import br.com.renan.projetodm114.webservice.CheckNetworkConnection;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ProductInfo productInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            displayFragment(R.id.nav_login);
        }

        Intent intent = this.getIntent();
        if (intent.hasExtra("productInfo")) {
            productInfo = (ProductInfo) intent.getSerializableExtra("productInfo");
            if (productInfo != null) {
                displayFragment(R.id.nav_gcm);
            }
        } else if (savedInstanceState == null) {
            displayFragment(R.id.nav_login);
        }
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1",
                    "virtualStore", importance);
            channel.setDescription("Info");
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayFragment(item.getItemId());
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra("productInfo")) {
            productInfo = (ProductInfo) intent.getSerializableExtra("productInfo");
            if (productInfo != null) {
                displayFragment(R.id.nav_gcm);
            }
        }
        super.onNewIntent(intent);
    }

    private void displayFragment(int fragmentId) {
        Class fragmentClass;
        Fragment fragment = null;
        int backStackEntryCount;
        backStackEntryCount = getFragmentManager().getBackStackEntryCount();
        for (int j = 0; j < backStackEntryCount; j++) {
            getFragmentManager().popBackStack();
        }
        try {
            switch (fragmentId) {
                case R.id.nav_login:
                    fragmentClass = LoginFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.nav_home:
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.nav_gcm:
                    fragmentClass = GCMFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    if (productInfo != null) {
                        Bundle args = new Bundle();
                        args.putSerializable("productInfo", productInfo);
                        fragment.setArguments(args);
                        productInfo = null;
                    }
                    break;
                case R.id.nav_config:
                    fragmentClass = SettingsFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.nav_pedidos:
                    fragmentClass = OrdersFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.nav_produtos:
                    fragmentClass = ProductsFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.nav_interest_produtos:
                    fragmentClass = InterestProductsFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                default:
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container,
                    fragment).commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

}
