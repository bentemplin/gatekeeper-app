package me.ryanpetschek.gatekeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.util.encoders.Hex;

import cz.msebera.android.httpclient.Header;

public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences("GK_settings", 0);

        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Building");

        Building source = Nearby.currentBuilding;
        Bitmap image = source.getImage();
        final String name = source.getName();
        String address = source.getAddress();
        String desc = source.getDescription();

        ImageView imageView = (ImageView) findViewById(R.id.buildingImage);
        TextView nameText = (TextView) findViewById(R.id.txtName);
        TextView addrText = (TextView) findViewById(R.id.txtAddress);
        TextView descText = (TextView) findViewById(R.id.txtDesc);

        final Button requestButton = (Button) findViewById(R.id.btnRequest);
        final Context outside = this;
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestButton.setEnabled(false);
                AsyncHttpClient client = new AsyncHttpClient();
                String url = "https://gatekeeper.ryanpetschek.me/" + name.replace(" ", "-").toLowerCase() + "/api/request";

                RequestParams params = new RequestParams();
                params.add("name", settings.getString("name", ""));
                params.add("imageUrl", settings.getString("pictureURL", ""));
                params.add("publicKey", settings.getString("publicKey", ""));

                client.post(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        AlertDialog alertDialog = new AlertDialog.Builder(outside).create();
                        if (statusCode != 200) {
                            alertDialog.setTitle("Error");
                            try {
                                alertDialog.setMessage(response.getString("error"));
                            }
                            catch (JSONException err) {
                                alertDialog.setMessage(err.getMessage());
                            }
                            alertDialog.show();
                            requestButton.setEnabled(true);
                            return;
                        }
                        alertDialog.setTitle("Success");
                        alertDialog.setMessage("Your request for access has been submitted");
                        requestButton.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        AlertDialog alertDialog = new AlertDialog.Builder(outside).create();
                        alertDialog.setTitle("Error");
                        try {
                            alertDialog.setMessage(response.getString("error"));
                        }
                        catch (JSONException err) {
                            alertDialog.setMessage(err.getMessage());
                        }
                        alertDialog.show();
                        requestButton.setEnabled(true);
                    }
                });
            }
        });

        imageView.setImageBitmap(image);
        nameText.setText(name);
        addrText.setText(address);
        descText.setText(desc);
    }

}
