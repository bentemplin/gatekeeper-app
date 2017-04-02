package me.ryanpetschek.gatekeeper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Building");

        Building source = Nearby.currentBuilding;
        Bitmap image = source.getImage();
        String name = source.getName();
        String address = source.getAddress();
        String desc = source.getDescription();

        ImageView imageView = (ImageView) findViewById(R.id.buildingImage);
        TextView nameText = (TextView) findViewById(R.id.txtName);
        TextView addrText = (TextView) findViewById(R.id.txtAddress);
        TextView descText = (TextView) findViewById(R.id.txtDesc);

        imageView.setImageBitmap(image);
        nameText.setText(name);
        addrText.setText(address);
        descText.setText(desc);
    }

}
