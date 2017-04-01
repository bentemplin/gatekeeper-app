package me.ryanpetschek.gatekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.URLUtil;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign up");

        final Button button = (Button) findViewById(R.id.signup);
        final EditText nameField = (EditText) findViewById(R.id.name);
        final EditText pictureUrlField = (EditText) findViewById(R.id.photoUrl);
        final AlertDialog alertDialog  = new AlertDialog.Builder(this).create();
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

                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    protected KeyPair generateKeys() {
        ECParameterSpec specs = ECNamedCurveTable.getParameterSpec("secp256k1");
        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("SHA256withECDSA", "BC");
            g.initialize(specs, new SecureRandom());
            KeyPair pair = g.generateKeyPair();
            return pair;
        }
        catch (java.security.NoSuchAlgorithmException | java.security.NoSuchProviderException | java.security.InvalidAlgorithmParameterException err) {
            Log.e("KeyGen", err.getMessage());
        }
        finally {
            return null;
        }
    }
}
