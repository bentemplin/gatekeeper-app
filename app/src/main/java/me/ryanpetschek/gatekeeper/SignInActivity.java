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

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.util.encoders.Hex;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class SignInActivity extends AppCompatActivity {
    protected PublicKey pub;
    protected PrivateKey priv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences settings = getSharedPreferences("GK_settings", 0);
        if (settings.getBoolean("hasAccount", false) == true) {
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
                    // Validate inputs
                    final String name = nameField.getText().toString().trim();
                    final String pictureUrl = pictureUrlField.getText().toString().trim();
                    if (name.length() == 0 || pictureUrl.length() == 0) {
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Name or photo URL cannot be left blank");
                        alertDialog.show();
                        return;
                    }
                    if (!URLUtil.isValidUrl(pictureUrl)) {
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("Invalid photo URL");
                        alertDialog.show();
                        return;
                    }
                    button.setEnabled(false);

                    KeyPair keyPair = generateKeys();
                    if (keyPair == null) {
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("No Keys Generated!");
                        alertDialog.show();
                        button.setEnabled(true);
                        return;
                    }
                    priv = keyPair.getPrivate();
                    pub = keyPair.getPublic();
                    String pubKeyHex = Hex.toHexString(pub.getEncoded());

                    // Make the signature
                    String sigHex = "";
                    String signingPayload = name + pictureUrl + pubKeyHex;
                    byte[] digested;
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        md.update(signingPayload.getBytes()); // Change this to "UTF-16" if needed
                        digested = md.digest();
                        Signature signature = Signature.getInstance("ECDSA", "SC");
                        signature.initSign(priv);
                        signature.update(digested);
                        byte[] sigData = signature.sign();
                        sigHex = Hex.toHexString(sigData);
                    } catch (NoSuchAlgorithmException | java.security.SignatureException
                            | java.security.InvalidKeyException
                            | java.security.NoSuchProviderException e) {}

                    // Upload this user to the server
                    AsyncHttpClient client = new AsyncHttpClient();
                    //String url = "https://gatekeeper.ryanpetschek.me/upload";
                    String url = "http://192.168.43.233:3000/upload";

                    RequestParams params = new RequestParams();
                    params.put("name", name);
                    params.put("pictureURL", pictureUrl);
                    params.put("publicKey", pubKeyHex);
                    params.put("signature", sigHex);

                    client.post(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("hasAccount", true);
                            editor.putString("name", name);
                            editor.putString("imageUrl", pictureUrl);
                            editor.commit();

                            storeKeyPair(priv, pub);

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

    private KeyPair generateKeys() {
        //Generates an elliptic curve encryption key-pair with a secp256k1 curve
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 3);
        ECParameterSpec specs = ECNamedCurveTable.getParameterSpec("secp256k1");
        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "SC");
            g.initialize(specs, new SecureRandom());
            KeyPair pair = g.generateKeyPair();
            return pair;
        }
        catch (java.security.NoSuchAlgorithmException | java.security.NoSuchProviderException |
                java.security.InvalidAlgorithmParameterException err) {
            Log.e("KeyGen", err.getMessage());
            err.printStackTrace();
            return null;
        }
    }

    private void storeKeyPair(PrivateKey privKey, PublicKey pubKey) {
        String pubKeyHex = Hex.toHexString(pubKey.getEncoded());
        String privKeyHex = Hex.toHexString(privKey.getEncoded());
        SharedPreferences.Editor settings = getSharedPreferences("GK_settings", 0).edit();
        settings.putString("publicKey", pubKeyHex);
        settings.putString("privateKey", privKeyHex);
    }

    private String getPublicKey() {
        SharedPreferences settings = getSharedPreferences("GK_settings", 0);
        return settings.getString("publicKey", "keyNotFound");
    }
}
