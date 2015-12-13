package fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import Adapter.ContactsAdapter;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import entities.Post;
import service.ChatService;
import ws.local.connects.GetContacts;
import entities.ContactsInfo;
import exception.CustomException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private ListView lv;
    private ContactsAdapter contactsAdapter;

    private BufferedReader bReader = null;
    private BufferedWriter bWriter = null;

    public FriendsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        lv = (ListView) view.findViewById(R.id.friendsListView);

        bReader = ChatService.br;
        bWriter = ChatService.bw;

        openThread();

        //GetlistContact();
        lv.setAdapter(new ContactsAdapter(ChatService.contactlist, getActivity()));

        //Codes for get info from connects
        //GetContacts.getContactsInfo(getActivity());

        //Exception Handle and self-healing
//        if (GetContacts.lists.size() == 0) {
//            lv.setAdapter(new ContactsAdapter(GetlistContact(), getActivity()));
//            try {
//                throw new CustomException(5);
//            } catch (CustomException e) {
//                System.out.println(e.printError(5));
//            }
//        } else {
//            lv.setAdapter(new ContactsAdapter(GetContacts.lists, getActivity()));
//        }





        view.findViewById(R.id.btnFriendAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        return view;
    }


    //Test
    private ArrayList<ContactsInfo> GetlistContact() {
        //ChatService.contactlist = new ArrayList<ContactsInfo>();

        ContactsInfo contact = new ContactsInfo("Xinao", "123");
        ChatService.contactlist.add(contact);

        contact = new ContactsInfo("Xun", "124");
        ChatService.contactlist.add(contact);

        contact = new ContactsInfo("Luoshu", "125");
        ChatService.contactlist.add(contact);

        return ChatService.contactlist;
    }

    public void openThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String line;
                    Log.e("tag", "----------waiting");
                    while ((line = bReader.readLine()) != null) {
                        Log.e("tag", line);
                        JSONObject jLine = null;
                        try {
                            jLine = new JSONObject(line);
                            if(jLine.getInt("method")==7) {
                                ContactsInfo contacts = new ContactsInfo(jLine.getString("userName"), jLine.getString("userId"));
                                ChatService.contactlist.add(contacts);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}








