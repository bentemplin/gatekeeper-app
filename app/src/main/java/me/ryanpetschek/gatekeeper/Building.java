package me.ryanpetschek.gatekeeper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by crsch on 4/2/2017.
 */

public class Building {

    private String name, slug, description, imageUrl, address, latitude, longitude;
    private Bitmap image;

    public static ArrayList<Building> buildings = new ArrayList<>();

    public Building(String name, String slug, String description, String imageUrl, String address, String latitude, String longitude) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        new DownloadImagesTask(this).execute(imageUrl);
        buildings.add(this);
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Bitmap getImage() {
        return image;
    }

    private static class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        private Building target;

        public DownloadImagesTask(Building target) {
            this.target = target;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            target.image = result;
        }


        private Bitmap download_Image(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }


    }
}
