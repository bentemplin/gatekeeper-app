package me.ryanpetschek.gatekeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link accountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link accountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class accountFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View v;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences settings;
    private EditText dobb;
    private EditText occb;
    private EditText addb;
    private EditText pnumb;

    private OnFragmentInteractionListener mListener;

    public accountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment accountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static accountFragment newInstance(String param1, String param2) {
        accountFragment fragment = new accountFragment();
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

    public void settSettings(SharedPreferences settings) {
        this.settings = settings;
    }

    public void updateUI() {
        String dob = settings.getString("DOB", "");
        String occ = settings.getString("Occupation", "");
        String add = settings.getString("Address", "");
        String pnum = settings.getString("PhoneNumber", "");

        dobb = (EditText) v.findViewById(R.id.set_DOB);
        occb = (EditText) v.findViewById(R.id.set_Occ);
        addb = (EditText) v.findViewById(R.id.set_Add);
        pnumb = (EditText) v.findViewById(R.id.set_Phone);

        dobb.setText(dob);
        occb.setText(occ);
        addb.setText(add);
        pnumb.setText(pnum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        v = inflater.inflate(R.layout.fragment_account, container, false);

        Button cb = (Button) v.findViewById(R.id.button_commit);
        Button ub = (Button) v.findViewById(R.id.undo_Changes);
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("DOB", dobb.getText().toString());
                editor.putString("Occupation", occb.getText().toString());
                editor.putString("Address", addb.getText().toString());
                editor.putString("PhoneNumber", pnumb.getText().toString());
                editor.commit();
            }
        });

        ub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Hi", "Clicked----------------");
                updateUI();
            }
        });

        updateUI();

        return v;
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
