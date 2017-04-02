package me.ryanpetschek.gatekeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link idFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link idFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class idFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences settings;
    private View v;
    private Bitmap defaultMap;

    private OnFragmentInteractionListener mListener;

    public idFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment idFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static idFragment newInstance(String param1, String param2) {
        idFragment fragment = new idFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v = inflater.inflate(R.layout.fragment_id, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.userImage);
        TextView nameText = (TextView) v.findViewById(R.id.txtName);
        TextView dobText = (TextView) v.findViewById(R.id.txtDateOfBirth);
        TextView occupationText = (TextView) v.findViewById(R.id.txtOccupation);
        TextView addressText = (TextView) v.findViewById(R.id.txtAddress);
        TextView phoneText = (TextView) v.findViewById(R.id.txtPhone);

        String name = settings.getString("name", "not set");
        String imgRef = settings.getString("imageUrl", "not set");
        String dob = settings.getString("DOB", "not set");
        String occ = settings.getString("Occupation", "not set");
        String add = settings.getString("Address", "not set");
        String pnum = settings.getString("PhoneNumber", "not set");

        nameText.setText(name);
        if (defaultMap != null) {
            imageView.setImageBitmap(defaultMap);
        } else {
            Log.d("Hello", "Uh uh");
        }
        new DownloadImagesTask(imageView).execute(imgRef);
        dobText.setText(dob);
        occupationText.setText(occ);
        addressText.setText(add);
        phoneText.setText(pnum);
        return v;
    }

    private class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;

        public DownloadImagesTask(ImageView view) {
            this.imageView = view;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            return download_Image(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            defaultMap = result;
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

    public void setSettings(SharedPreferences settings) {
        this.settings = settings;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
