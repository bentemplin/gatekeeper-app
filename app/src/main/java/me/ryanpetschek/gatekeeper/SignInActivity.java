package me.ryanpetschek.gatekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import org.bitcoin.NativeSecp256k1;
import org.bitcoin.NativeSecp256k1Util;
import org.bitcoinj.core.Sha256Hash;

import org.spongycastle.util.encoders.Hex;
import java.security.SecureRandom;

import org.json.JSONException;
import org.json.JSONObject;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

public class SignInActivity extends AppCompatActivity {

    public static String privateKey, publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences("GK_settings", 0);
        if (settings.getBoolean("hasAccount", false) == true) {
            publicKey = settings.getString("publicKey", "default");
            privateKey = settings.getString("privateKey", "default");
            Log.d("Hello", publicKey);
            Log.d("Hello", privateKey);

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_sign_in);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            setTitle("GateKeeper");

            final Button button = (Button) findViewById(R.id.signup);
            final EditText nameField = (EditText) findViewById(R.id.name);
            final EditText pictureUrlField = (EditText) findViewById(R.id.photoUrl);
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    button.setEnabled(false);
                    // Validate inputs
                    final String name = nameField.getText().toString().trim();
                    final String pictureUrl = pictureUrlField.getText().toString().trim();
                    if (name.length() == 0 || pictureUrl.length() == 0) {
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Name or photo URL cannot be left blank");
                        alertDialog.show();
                        button.setEnabled(true);
                        return;
                    }
                    if (!URLUtil.isValidUrl(pictureUrl)) {
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Invalid photo URL");
                        alertDialog.show();
                        button.setEnabled(true);
                        return;
                    }

                    final ECKey key = new ECKey();
                    byte[] payload = (name + pictureUrl + Hex.toHexString(key.getPubKey())).getBytes();
                    byte[] hashedPayload = Sha256Hash.hash(payload);
                    byte[] signature = key.sign(hashedPayload);

                    // Upload this user to the server
                    AsyncHttpClient client = new AsyncHttpClient();
                    String url = "https://gatekeeper.ryanpetschek.me/upload";

                    RequestParams params = new RequestParams();
                    params.put("name", name);
                    params.put("pictureURL", pictureUrl);
                    params.put("publicKey", Hex.toHexString(key.getPubKey()));
                    params.put("signature", Hex.toHexString(signature));

                    client.post(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("hasAccount", true);
                            editor.putString("name", name);
                            editor.putString("imageUrl", pictureUrl);
                            editor.putString("privateKey", Hex.toHexString(key.getPubKey()));
                            editor.putString("publicKey", key.getPrivKey().toString());
                            editor.commit();

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
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
                            button.setEnabled(true);
                        }
                    });
                }
            });
        }
    }
}
