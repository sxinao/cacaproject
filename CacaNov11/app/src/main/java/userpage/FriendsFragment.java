package userpage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import Adapter.ContactsAdapter;

import com.caca.R;

import java.util.ArrayList;

import ws.local.connects.GetContacts;
import entities.ContactsInfo;
import exception.CustomException;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private ListView lv;
    private ContactsAdapter contactsAdapter;

    public FriendsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        lv = (ListView) view.findViewById(R.id.friendsListView);

        //Codes for get info from connects
        GetContacts.getContactsInfo(getActivity());

        //Exception Handle and self-healing
        if (GetContacts.lists.size() == 0) {
            lv.setAdapter(new ContactsAdapter(GetlistContact(), getActivity()));
            try {
                throw new CustomException(5);
            } catch (CustomException e) {
                System.out.println(e.printError(5));
            }
        } else {
            lv.setAdapter(new ContactsAdapter(GetContacts.lists, getActivity()));
        }

        view.findViewById(R.id.btnFriendAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        return view;
    }


    //Test
    private ArrayList<ContactsInfo> GetlistContact() {
        ArrayList<ContactsInfo> contactlist = new ArrayList<ContactsInfo>();

        ContactsInfo contact = new ContactsInfo("Xinao", "0123456789");
        contactlist.add(contact);

        contact = new ContactsInfo("Xun", "1234567890");
        contactlist.add(contact);

        contact = new ContactsInfo("Luoshu", "2345678901");
        contactlist.add(contact);

        return contactlist;
    }

}
