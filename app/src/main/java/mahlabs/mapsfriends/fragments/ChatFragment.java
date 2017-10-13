package mahlabs.mapsfriends.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mahlabs.mapsfriends.Controller;
import mahlabs.mapsfriends.MainActivity;
import mahlabs.mapsfriends.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment{

    public static final String TAG="chatFragment";
    private Controller controller;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controller=((MainActivity)getActivity()).getController();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


}
