package me.ryanpetschek.gatekeeper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

imp

public class BusinessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Building source = Nearby.currentBuilding;
        Bitmap image = source.getImage();
        String name = source.getName();
        String address = source.getAddress();
        String desc = source.getDescription();

        v = inflater.inflate(R.layout.fragment_id, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.buildingImage);
        TextView nameText = (TextView) v.findViewById(R.id.txtNameLabel);
        TextView addrText = (TextView) v.findViewById(R.id.txtAddressLabel);
        TextView descText = (TextView) v.findViewById(R.id.txtDescLabel);

        imageView.setImageBitmap(image);
        nameText.setText(name);
        addrText.setText(address);
        descText.setText(desc);


        setContentView(R.layout.activity_business);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Building");
    }

}
