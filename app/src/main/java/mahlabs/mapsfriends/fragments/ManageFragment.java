package mahlabs.mapsfriends.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import mahlabs.mapsfriends.Controller;
import mahlabs.mapsfriends.MainActivity;
import mahlabs.mapsfriends.R;
import mahlabs.mapsfriends.connection.JsonHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFragment extends Fragment{

    public static final String TAG="manageFragment";
    private Controller controller;

    private Button btnCreateGrp, btnDeregister;
    private TextView tvCurrentGrp, tvCurrentGrpAmount;
    private EditText etNewGroup;
    private ListView lvGroups;
    private ArrayAdapter<String> adapter;
    private String[] listContent = new String[0];

    private UpdateFragment responseListener;

    public ManageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).hideKeyboard();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controller=((MainActivity)getActivity()).getController();
        controller.setManageCallback(new UpdateManage());
        try {
            controller.sendToServer(JsonHandler.currentGroups());
        } catch (IOException e) {
            e.printStackTrace();
        }


        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        initComponents(rootView);
        populateListView();
        addListeners();
        updateUI();

        // Inflate the layout for this fragment
        return rootView;
    }

    private void populateListView() {
        ArrayList<String> grps = controller.getCurrentGroups();
        if(grps!=null){
            listContent = new String[grps.size()];
            for(int i = 0; i<grps.size();i++){
                listContent[i]=grps.get(i);
            }
        }

        if(isAdded()){
            adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, listContent);

        }
        lvGroups.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        lvGroups.invalidateViews();
    }

    private void addListeners() {
        btnDeregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    controller.sendToServer(JsonHandler.deregistration(controller.getThisUser().getId()));
                    btnDeregister.setEnabled(false);
                } catch (IOException e) {
                    //e.printStackTrace();
                }

            }
        });
        btnCreateGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newGroup = etNewGroup.getText().toString();
                String userName = getActivity().getSharedPreferences(MainActivity.SP_TAG, Activity.MODE_PRIVATE).getString("user_name",null);
                ((MainActivity)getActivity()).hideKeyboard();
                etNewGroup.setText("");
                etNewGroup.invalidate();
                if(newGroup.length()>0 && userName!=null){
                    try {
                        controller.sendToServer(JsonHandler.registration(newGroup,userName));
                    } catch (IOException e) {
                      //  e.printStackTrace();
                    }
                }

            }
        });
    }

    private void initComponents(View rootView) {
        btnCreateGrp = rootView.findViewById(R.id.btnCreateGrp);
        btnDeregister = rootView.findViewById(R.id.btnDeregister);
        tvCurrentGrp = rootView.findViewById(R.id.tvCurrentGrpName);
        tvCurrentGrpAmount = rootView.findViewById(R.id.tvGroupAmountRegistered);
        etNewGroup = rootView.findViewById(R.id.etCreateGrp);
        lvGroups = rootView.findViewById(R.id.lvList);
        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, listContent);
        lvGroups.setAdapter(adapter);
        lvGroups.setOnItemClickListener(new ListListener());

        String groupName = controller.getCurrentGroup();
        if(groupName==null){
            tvCurrentGrp.setText("None");
            btnDeregister.setEnabled(false);
        }else{
            tvCurrentGrp.setText(groupName);
            //TODO get amount of members
        }
    }


    public void updateUI() {
        String currentGrp =controller.getCurrentGroup();
        if(currentGrp==null){
            return;
        }
        Log.d(TAG, "update() - " +currentGrp );

        tvCurrentGrp.setText(currentGrp);
        if(currentGrp.equals("None")){
            btnDeregister.setEnabled(false);
            btnCreateGrp.setEnabled(true);
            etNewGroup.setEnabled(true);
        }else{
            btnDeregister.setEnabled(true);
            btnCreateGrp.setEnabled(false);
            etNewGroup.setEnabled(false);

        }

        populateListView();
        tvCurrentGrp.invalidate();
        btnDeregister.invalidate();
        btnCreateGrp.invalidate();


    }

    private class ListListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView tv = (TextView) view;

            try {
                controller.sendToServer(JsonHandler.deregistration(controller.getThisUser().getId()));
                controller.sendToServer(JsonHandler.registration(tv.getText().toString(),controller.getThisUser().getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdateManage implements UpdateFragment{

        @Override
        public void update() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
        }
    }
}
