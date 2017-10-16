package mahlabs.mapsfriends;

import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import mahlabs.mapsfriends.connection.JsonHandler;
import mahlabs.mapsfriends.data.Locator;
import mahlabs.mapsfriends.data.User;
import mahlabs.mapsfriends.fragments.ManageFragment;
import mahlabs.mapsfriends.fragments.MapFragment;
import mahlabs.mapsfriends.connection.ConnectionFragment;
import mahlabs.mapsfriends.data.DataFragment;
import mahlabs.mapsfriends.fragments.UpdateFragment;

/**
 * Created by tsroax on 27/09/15.
 */
public class Controller {
    private static final String LOG="IN_Controller";

    private MainActivity activity;

    private DataFragment dataFragment;
    private ConnectionFragment connection;
    private Locator locator;
    private UpdateFragment fMapListener;
    private UpdateFragment fManageListener;


    public Controller(MainActivity activity) {
        this.activity = activity;
        FragmentManager fm = activity.getSupportFragmentManager();
        dataFragment = (DataFragment) fm.findFragmentByTag(DataFragment.TAG);
        connection = (ConnectionFragment) fm.findFragmentByTag(ConnectionFragment.TAG);

        if(dataFragment==null){
            dataFragment = new DataFragment();
            dataFragment.setCurrentTag(MapFragment.TAG);
            dataFragment.setCurrentGroup(activity.getResources().getString(R.string.enCurrentGrpNone));
            fm.beginTransaction().add(dataFragment,DataFragment.TAG).commit();
        }

        if(connection==null){
            connection = new ConnectionFragment();
            connection.setIP("195.178.227.53");
            connection.setPort(7117);
            connection.setCallbackListener(new ServerResponse());
            connection.connect();
            fm.beginTransaction().add(connection,ConnectionFragment.TAG).commit();
        }

    }


    public User getThisUser() {
        return dataFragment.getThisUser();
    }

    public String getCurrentGroup() {
       return dataFragment.getCurrentGroup();
    }

    public void sendToServer(String msg){
        connection.sendToServer(msg);
    }

    public void setCurrentGroup(String groupName){
        dataFragment.setCurrentGroup(groupName);
    }

    public ArrayList<String> getCurrentGroups() {
        return dataFragment.getCurrentGroups();
    }

    public ArrayList<User> getCurrentUsers() {
        return dataFragment.getCurrentUsers();
    }

    public String getCurrentTag() {
        return dataFragment.getCurrentTag();
    }

    public void setCurrentTag(String tag){
        dataFragment.setCurrentTag(tag);
    }

    public void setMapCallback(MapFragment.UpdateMap updateMap) {
        this.fMapListener =updateMap;
    }

    public void setManageCallback(ManageFragment.UpdateManage updateManage) {
        this.fManageListener=updateManage;
    }

    private class ServerResponse implements ResponseListener{

        @Override
        public void parseServerMsg(String msg) {
            JSONObject json = null;
            try {
                json = new JSONObject(msg);
                final String type = json.getString("type");
                Log.d(LOG,"parsing type: "+type);

                switch (type){
                    case "register":
                        String groupName = json.getString("group");
                        String id = json.getString("id");
                        sendToServer(JsonHandler.currentGroups());
                        handleRegister(groupName,id);
                        break;
                    case "unregister":
                        handleUnregister();
                        break;
                    case "members":

                        break;
                    case "groups":
                        handleGroups(json);
                        break;
                    case "location":

                        break;
                    case "locations":
                        handleLocations(json);
                        break;
                    case "exception":
                        break;
                    case "textchat":
                        break;
                    case "imagechat":
                        break;

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(LOG,e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void handleUnregister() {
            if(locator!=null){
                locator = null;
            }
            dataFragment.setCurrentGroup(activity.getResources().getString(R.string.enCurrentGrpNone));
            dataFragment.setCurrentUsers(new ArrayList<User>());
            fManageListener.update();
        }

        private void handleRegister(final String groupName, final String id) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(locator==null){
                        locator=new Locator(activity,Controller.this);
                    }
                    dataFragment.getThisUser().setId(id);
                    dataFragment.setCurrentGroup(groupName);
                    fManageListener.update();
                }
            });



        }
        private void handleLocations(JSONObject json) throws JSONException {
            JSONArray array = json.getJSONArray("location");
            JSONObject transaction;
            Log.d("IN_HLOC",json.toString());
            ArrayList<User> users = new ArrayList<>();
            for(int i =0; i<array.length();i++){
                transaction=array.getJSONObject(i);
                double lon = Double.parseDouble(transaction.getString("longitude"));
                double lat= Double.parseDouble(transaction.getString("latitude"));
                String name = transaction.getString("member");
                users.add(new User(name,lon,lat,""));
            }
            dataFragment.setCurrentUsers(users);
            fMapListener.update();

        }

        private void handleGroups(JSONObject json) throws JSONException {
            JSONArray array = json.getJSONArray("groups");
            JSONObject transaction;
            ArrayList<String> groups = new ArrayList<>();
            Log.d(LOG,array.length()+"");

            for(int i = 0; i<array.length();i++){
                transaction = array.getJSONObject(i);
                groups.add(transaction.getString("group"));
                Log.d(LOG, "group: "+groups.get(i));
            }
            dataFragment.setCurrentGroups(groups);
            fManageListener.update();

        }
    }


}
