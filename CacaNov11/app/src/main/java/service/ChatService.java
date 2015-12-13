package service;

/**
 * Created by lucile on 15/12/9.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import configuration.MyConf;
import entities.ContactsInfo;

public class ChatService extends Service {
    private final int SERVER_PORT = MyConf.PORT; // server port
    private final int TIMEOUT = 4000; // default timeout

    private final IBinder mBinder = new ChatClientBinder(); // service binder
    private String name; // client name
    private String serverHost; // server address
    private Socket socket; // socket for the connection
    //private DataOutputStream sockOut; // stream for sending data
    public static BufferedWriter bw;
    public static BufferedReader br;
    //private DataInputStream sockIn; // stream for receiving data
    //private serverListenerThread listenServerThread; // thread for listening to the server
    //private ChatActivity chat; // chat activity instance
    private String[] clients; // list of connected clients
    private List<String[]> messages; // list of messages (ID, client, text)
    private boolean connected = false;


    public static ArrayList<ContactsInfo> contactlist;

    public class ChatClientBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }


    @Override
    public void onCreate() {
        Log.e("DEBUG", "On create!");

        contactlist = new ArrayList<ContactsInfo>();
        ContactsInfo contact = new ContactsInfo("Xinao", "123");
        contactlist.add(contact);

        contact = new ContactsInfo("Xun", "124");
        contactlist.add(contact);

        contact = new ContactsInfo("Luoshu", "125");
        contactlist.add(contact);

        new Thread(new Runnable(){
            public void run() {
                 connectServer();

            }
        }).start();

    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Debug", "onStartCommand" + intent);
        return 1;
    }



    public String[] getClients(){
        return clients;
    }

    public List<String[]> getMessages(){
        return messages;
    }

    // stop listening and disconnect

    public void end(){
        connected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // connect to the server
    private boolean connectServer(){

        try {
            socket = new Socket(MyConf.HOST, MyConf.PORT);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            connected = true;
            Log.e("DEBUG","connectServer, connected successfully!!");

        }
        catch (Exception e) {
            return false;
        }

        return true;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    public boolean isConnected(){
        return connected;
    }

    // send a message to the server
    public static void sendMsg(JSONObject jLine){
        try {
            bw.write(jLine + "\n");
            bw.flush();
            System.out.println("has been out");

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}