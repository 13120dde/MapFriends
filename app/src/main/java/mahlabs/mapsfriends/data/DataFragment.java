package mahlabs.mapsfriends.data;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import mahlabs.mapsfriends.MainActivity;
import mahlabs.mapsfriends.fragments.MapFragment;
import mahlabs.mapsfriends.fragments.UpdateFragment;

/**
 * Created by 13120dde on 2017-10-10.
 */

public class DataFragment extends Fragment {

    public static final String TAG="dataFragment";
    

    private static String currentTag;
    private User thisUser;
    private ArrayList<String> currentGroups;
    private ArrayList<User> currentUsers;
    private String currentGroup;
    private UpdateFragment fragmentListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String name = getActivity().getSharedPreferences(MainActivity.SP_TAG, Activity.MODE_PRIVATE).getString("user_name","");
        thisUser = new User(name,0,0,"");
        currentGroups = new ArrayList<>();
        currentUsers = new ArrayList<>();
        
    }

    public static String getCurrentTag() {
        return currentTag;
    }

    public void setMapCallback(UpdateFragment listener){
        fragmentListener=listener;
    }

    public static void setCurrentTag(String currentTag) {
        DataFragment.currentTag = currentTag;
    }

    public User getThisUser() {
        return thisUser;
    }

    public synchronized void setCurrentGroups(ArrayList<String> groups) {
        currentGroups=groups;
    }
    
    public synchronized ArrayList<String> getCurrentGroups(){
        return currentGroups;
    }

    public synchronized void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

    public synchronized String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentUsers(ArrayList<User> users) {
        this.currentUsers=users;
    }

    public ArrayList<User> getCurrentUsers(){
        return currentUsers;
    }
}
