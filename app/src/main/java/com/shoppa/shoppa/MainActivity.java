package com.shoppa.shoppa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shoppa.shoppa.NaviFragment.HomeFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentTransaction mFragmentTransaction;
    private Fragment mFragment;

    private final int LOG_IN_REQUEST_CODE = 1;
    private final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 2;

    private String currentFragment = "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkLogged();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState(); //Show animation of hamburger

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fabShop = (FloatingActionButton) findViewById(R.id.fab_shop);
        fabShop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        MainActivity.this.checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_ACCESS_CAMERA);
                } else {
                    startScan();
                }
            }
        });

        // Adding home fragment into frame as default
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragment = new HomeFragment();
        mFragmentTransaction = fragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.content_frame, mFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOG_IN_REQUEST_CODE) {

        } else {

            IntentResult scanningResult =
                    IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (scanningResult != null) {
                if (scanningResult.getContents() != null) {
                    String shopId = scanningResult.getContents();
                    Intent intent = new Intent(this, ShopActivity.class);
                    intent.putExtra("SHOP_ID", shopId);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "No scan data received!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                Toast.makeText(this, "Shoppa need permission to scan QR code", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startScan() {

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this)
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .setPrompt("Scan shopper QR code")
                .setCaptureActivity(ScannerActivity.class)
                .setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void checkLogged() {

        if (1 == 1) {

            Intent i = new Intent(MainActivity.this, ShopActivity.class);
            startActivity(i);

        } else {
            Intent intent = new Intent(this, LoginRegisterActivity.class);
            startActivityForResult(intent, LOG_IN_REQUEST_CODE);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home && !currentFragment.equals("HOME")) {

        } else if (id == R.id.nav_card && !currentFragment.equals("CARD")) {

        } else if (id == R.id.nav_history && !currentFragment.equals("HISTORY")) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
