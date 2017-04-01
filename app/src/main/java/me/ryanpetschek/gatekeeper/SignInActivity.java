package me.ryanpetschek.gatekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    protected KeyPair generateKeys() {
        ECParameterSpec specs = ECNamedCurveTable.getParameterSpec("secp256k1");
        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
            g.initialize(specs, new SecureRandom());
            KeyPair pair = g.generateKeyPair();
            return pair;
        }
        catch (java.security.NoSuchAlgorithmException err) {

        }
        catch (java.security.NoSuchProviderException err) {

        }
        catch (java.security.InvalidAlgorithmParameterException err) {

        }
        finally {
            return null;
        }
    }
}
