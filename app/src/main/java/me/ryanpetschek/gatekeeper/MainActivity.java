package me.ryanpetschek.gatekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        idFragment.OnFragmentInteractionListener, accountFragment.OnFragmentInteractionListener,
        nearbyFragment.OnFragmentInteractionListener, networkFragment.OnFragmentInteractionListener,
        permGivenFragment.OnFragmentInteractionListener,
        permReceivedFragment.OnFragmentInteractionListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadBusinesses();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        idFragment fragment = new idFragment();
        SharedPreferences settings = getSharedPreferences("GK_settings", 0);
        fragment.setSettings(settings);
        ft.replace(R.id.flContent, fragment);
        ft.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.menu_Name)).setText(settings.getString("name", "George P. Burdell"));
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.menu_PublicKey)).setText(settings.getString("publicKey", ""));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setTitle(navigationView.getMenu().getItem(0).getTitle());
        navigationView.getMenu().getItem(0).setChecked(true);

//        TextView nameView = (TextView) findViewById(R.id.menu_Name);
//        nameView.setText(settings.getString("name", "name not found"));
//
//        TextView keyView = (TextView) findViewById(R.id.menu_PublicKey);
//        keyView.setText("Public Key");
    }

    private void loadBusinesses() {
        RequestParams params = new RequestParams();

        // Upload this user to the server
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://gatekeeper.ryanpetschek.me/buildings";
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override

            public void onSuccess(int l, Header[] headers, JSONArray json) {
                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject building = json.getJSONObject(i);
                        String name = building.getString("name");
                        String slug = building.getString("nameSlug");
                        String description = building.getString("description");
                        String imageUrl = building.getString("pictureURL");
                        JSONObject loc = building.getJSONObject("location");
                        String address = loc.getString("address");
                        String latitude = loc.getString("latitude");
                        String longitude = loc.getString("longitude");
                        new Building(name, slug, description, imageUrl, address, latitude, longitude);
                    }
                    int ii = 0;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                alertDialog.setTitle("Error");
                try {
                    alertDialog.setMessage(response.getString("error"));
                }
                catch (JSONException err) {
                    alertDialog.setMessage(err.getMessage());
                }
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_delete:
                Log.d("Hello", "Hi");
                getSharedPreferences("GK_settings", 0).edit().clear().commit();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass;

        if (id == R.id.nav_id) {
            fragmentClass = idFragment.class;
        } else if (id == R.id.nav_nearby) {
            fragmentClass = nearbyFragment.class;
        } /*selse if (id == R.id.nav_network) {
            fragmentClass = networkFragment.class;
        } */else if (id == R.id.nav_account) {
            fragmentClass = accountFragment.class;
        } else if (id == R.id.nav_perm_given) {
            fragmentClass = permGivenFragment.class;
        } else if (id == R.id.nav_perm_received) {
            fragmentClass = permReceivedFragment.class;
        } else {
            fragmentClass = idFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            if (fragment instanceof accountFragment) {
                SharedPreferences settings = getSharedPreferences("GK_settings", 0);
                ((accountFragment) fragment).setSettings(settings);
            } else if (fragment instanceof  idFragment) {
                SharedPreferences settings = getSharedPreferences("GK_settings", 0);
                ((idFragment) fragment).setSettings(settings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Replace any existing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        //set values for NavigationView
        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //empty b/c not implemented
    }
}
