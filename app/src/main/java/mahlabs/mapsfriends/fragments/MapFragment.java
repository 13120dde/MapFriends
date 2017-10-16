package mahlabs.mapsfriends.fragments;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import mahlabs.mapsfriends.Controller;
import mahlabs.mapsfriends.MainActivity;
import mahlabs.mapsfriends.R;
import mahlabs.mapsfriends.data.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment{

    public static final String TAG="mapFragment";

    private SupportMapFragment mSupportMapFragment;
    private GoogleMap map;
    private Controller controller;
    private boolean first= true;
    private MainActivity activity;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("IN_ONATTACH","MAP");
        activity=(MainActivity)context;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        controller = ((MainActivity)getActivity()).getController();
        controller.setMapCallback(new UpdateMap());
        initMapFragment();
        addMarker();

        return rootView;
    }

    private void initMapFragment() {
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapView, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {

                        map=googleMap;
                        map.getUiSettings().setAllGesturesEnabled(true);

                        LatLng currentPosition = new LatLng(55.599317,13.001262);

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentPosition).zoom(13.0f).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        map.moveCamera(cameraUpdate);

                    }

                }
            });
        }
    }

    public void addMarker() {
/*
        if(getActivity()==null){
            return;
        }*/
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(map!=null){
                    map.clear();
                    LatLng myPos = null;
                    ArrayList<User> users = controller.getCurrentUsers();
                    for(User u : users){
                        if(u.getName().equals(controller.getThisUser().getName())){
                            myPos = new LatLng(u.getLatitude(),u.getLongitude());
                        }
                        map.addMarker(new MarkerOptions().position(new LatLng(u.getLatitude(),u.getLongitude())).title(u.getName()));

                    }
                    if(myPos!=null && first){
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos,13.0f));
                        first=false;
                    }
                }
            }
        });
    }

    public class UpdateMap implements UpdateFragment{

        @Override
        public void update() {
            addMarker();
        }
    }


}
