package me.ryanpetschek.gatekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;

import javax.net.ssl.HttpsURLConnection;

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
                    String name = nameField.getText().toString().trim();
                    String pictureUrl = pictureUrlField.getText().toString().trim();
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

                    KeyPair keyPair = generateKeys();
                    if (keyPair == null) {
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("No Keys Generated!");
                        alertDialog.show();
                        return;
                    }
                    priv = keyPair.getPrivate();
                    pub = keyPair.getPublic();
                    String pubKeyHex = Hex.toHexString(pub.getEncoded());

                    //Make the signatures
                    String sigHex = "";
                    String signingPayload = new String(name + pictureUrl + pubKeyHex);
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

                    new PostCrypto().execute(name, pictureUrl, pubKeyHex, sigHex);

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("hasAccount", true);
                    editor.putString("name", name);
                    editor.putString("imageUrl", pictureUrl);
                    editor.commit();

                    storeKeyPair(priv, pub);

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
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

    private class PostCrypto extends AsyncTask<String, Void, String> {

        public PostCrypto() {}
        @Override
        public String doInBackground(String ... strings ) {
            //Post everything to the Server
            try {
                URL url = new URL("https://ga.tekeeper.com");
                HttpsURLConnection connect = (HttpsURLConnection) url.openConnection();
                connect.setRequestMethod("POST");
                connect.setRequestProperty("User-Agent", "Fu[sic]GA");
                connect.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                connect.setRequestProperty("Content-Type", "application/json");
                connect.setRequestProperty("Accept", "application/json");

                JSONObject payload = new JSONObject();

                try {
                    payload.put("name", strings[0]);
                    payload.put("pictureURL", strings[1]);
                    payload.put("publicKey", strings[2]);
                    payload.put("signature", strings[3]);
                } catch (org.json.JSONException err) {
                }

                //Initiate the post
                connect.setDoOutput(true);
                DataOutputStream write = new DataOutputStream(connect.getOutputStream());
                write.writeBytes(payload.toString());
                write.flush();
                write.close();

                //Get response data
                Integer respCode = connect.getResponseCode();
                Log.d("RESPONSE-CODE: ", respCode.toString());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connect.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String finalResp = response.toString();

                //Parse the JSON
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(finalResp);
                } catch (org.json.JSONException err) {

                }
                try {
                    jsonData.getBoolean("success");
                    return null;
                } catch (org.json.JSONException e) {
                    try {
                        return jsonData.getString("error");
                    } catch (org.json.JSONException err) {
                    }
                }

            } catch (ProtocolException err1) {
                Log.e("ERROR", err1.getMessage());
                err1.printStackTrace();
            } catch (IOException err2) {
                Log.e("ERROR", err2.getMessage());
                err2.printStackTrace();
            }
            return null;
        }
    }
}
