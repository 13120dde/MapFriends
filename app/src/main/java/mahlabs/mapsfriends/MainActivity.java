package mahlabs.mapsfriends;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import mahlabs.mapsfriends.connection.JsonHandler;
import mahlabs.mapsfriends.data.DataFragment;
import mahlabs.mapsfriends.fragments.ChatFragment;
import mahlabs.mapsfriends.fragments.ManageFragment;
import mahlabs.mapsfriends.fragments.MapFragment;

public class MainActivity extends AppCompatActivity {

    public static final String SP_TAG="spMApFriends";

    private Controller controller;
    private Button btnMap, btnChat,btnManage;
    private String userName;

    private ManageFragment manageFragment;
    private MapFragment mapFragment;
    private ChatFragment chatFragment;
    private String currentTag;
    private FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences(SP_TAG,Activity.MODE_PRIVATE);
        userName = sp.getString("user_name",null);
        if(userName==null){
            showInputDialog();
        }

        initComponents();
        registerListeners();
        controller = new Controller(this);
        if(savedInstanceState==null){
            manageFragment = new ManageFragment();
            mapFragment = new MapFragment();
            chatFragment = new ChatFragment();

            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_container,manageFragment,ManageFragment.TAG);
            ft.add(R.id.fragment_container,chatFragment,ChatFragment.TAG);
            ft.add(R.id.fragment_container,mapFragment,MapFragment.TAG);

            ft.hide(manageFragment);
          //  ft.hide(mapFragment);
            ft.hide(chatFragment);
            ft.commit();


        }else{
            fm = getSupportFragmentManager();
            manageFragment = (ManageFragment) fm.findFragmentByTag(ManageFragment.TAG);
            mapFragment= (MapFragment) fm.findFragmentByTag(MapFragment.TAG);
            chatFragment= (ChatFragment) fm.findFragmentByTag(ChatFragment.TAG);
        }

        showFragment(controller.getCurrentTag());

    }

    private void showFragment(String tag) {
        if(tag==null){
            tag=MapFragment.TAG;
        }
        Fragment newFragment = fm.findFragmentByTag(tag);
        Fragment currentFragment = fm.findFragmentByTag(controller.getCurrentTag());
        if(newFragment!=null){
            FragmentTransaction ft = fm.beginTransaction();
            if(currentFragment!=null){
                ft.hide(currentFragment);
            }
            ft.show(newFragment);
            ft.commit();
            controller.setCurrentTag(tag);
        }
    }

    private void initComponents() {
        btnMap = (Button)findViewById(R.id.btnMap);
        btnChat = (Button)findViewById(R.id.btnChat);
        btnManage= (Button)findViewById(R.id.btnManage);
        btnMap.setText(R.string.enBtnMap);
        btnChat.setText(R.string.enBtnChat);
        btnManage.setText(R.string.enBtnManage);
        fm = getSupportFragmentManager();

    }

    protected void showInputDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //resultText.setText("Hello, " + editText.getText());
                        SharedPreferences sp = getSharedPreferences(SP_TAG, Activity.MODE_PRIVATE);
                        sp.edit().putString("user_name",editText.getText().toString()).commit();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void registerListeners() {

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(MapFragment.TAG);
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(ChatFragment.TAG);
            }
        });

        btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    controller.sendToServer(JsonHandler.currentGroups());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                showFragment(ManageFragment.TAG);
            }
        });
    }

    public Controller getController() {
        return controller;
    }

    public void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        View v = getCurrentFocus();
        if(v==null){
            return;
        }
        inputManager.hideSoftInputFromWindow(v.getWindowToken(),0);

    }
}
