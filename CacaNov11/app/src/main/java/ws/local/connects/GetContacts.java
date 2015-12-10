package ws.local.connects;

/*

Get Phone number and Name from local contacts

 */
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import entities.ContactsInfo;




public class GetContacts {

    public static List<ContactsInfo> lists = new ArrayList<ContactsInfo>();

    public static String getContactsInfo(Context context){

        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String phoneNumber;
        String phoneName;
        //Get all connects
        while(cursor.moveToNext()){
            phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            ContactsInfo contactsInfo= new ContactsInfo(phoneName, phoneNumber);
            lists.add(contactsInfo);
            System.out.println(phoneName + "," + phoneNumber);
        }
        return null;

    }

}
