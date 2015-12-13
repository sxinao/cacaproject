package fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.DialogInterface.OnClickListener;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Adapter.PostAdapter;
import db.PostDB;
import configuration.MyConf;
import entities.ContactsInfo;
import entities.Post;
import entities.User;
import activity.MainActivity;
import service.ChatService;
import service.SocketService;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostFragment extends Fragment implements MyConf {
    private static PostDB postDB;
    private ArrayList<Post> posts;
    private Post post;
    private String postText;
    double location;
    String timestamp;
    static private int userId2;

    private EditText etPost;
    private ListView lvPosts;
    final String TAG = "PostFragment";

    private BufferedReader bReader = null;
    private BufferedWriter bWriter = null;


    public PostFragment() {
    }

    static public int getUserId2() {
        return userId2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, null);

        posts = new ArrayList<Post>();
        location = 1000.0;
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm");
        timestamp = format.format(date);

        etPost = (EditText) view.findViewById(R.id.etPostpost);
        lvPosts = (ListView) view.findViewById(R.id.lvPostposts);
        postDB = new PostDB(getActivity().getApplicationContext());

        while(bWriter == null||bReader ==null) {
            bReader = ChatService.br;
            bWriter = ChatService.bw;
        }
        openThread();

        JSONObject jQuery=new JSONObject();
        try {
            jQuery.put("method",6);
            jQuery.put("requestUserId",User.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            bWriter.write(jQuery + "\n");
            bWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        populateListView();
        registerClickCallback();

        view.findViewById(R.id.btnPostSend).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get postText -> post
                postText = etPost.getText().toString();
                post = new Post(timestamp, User.getUserId(), postText, location, User.getImage());
                // add to posts
                posts.add(post);
                // send to server -> json
                JSONObject jPost = new JSONObject();
                try {
                    jPost.put("method", 1);
                    jPost.put("timestamp", post.getTimestamp());
                    jPost.put("userId", post.getUserId());
                    jPost.put("postText", post.getPostText());
                    jPost.put("location", post.getLocation());
                    jPost.put("image", post.getImage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    bWriter.write(jPost + "\n");
                    bWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // put into local database
                SQLiteDatabase dbWrite = postDB.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("userId", post.getUserId()); // default values
                cv.put("timestamp", post.getTimestamp());
                cv.put("distance", post.getLocation());
                cv.put("post", post.getPostText());
                dbWrite.insert("posts", null, cv);
                // shouwei
                etPost.setText("");
                dbWrite.close();
            }
        });

        return view;
    }

    //-------------------------------------------------------------------------------
    private void populateListView() {
        PostAdapter adapter = new PostAdapter(getActivity().getApplicationContext(), posts);
        lvPosts.setAdapter(adapter);
    }

    private void registerClickCallback() {
        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Post target_post = (Post) lvPosts.getAdapter().getItem(position);
                final int userid_target = (int) target_post.getUserId();

                Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                        android.R.drawable.btn_star).setTitle("choices").setMessage(
                        "").setPositiveButton("add friend",
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                add_friend(User.getUserId(), userid_target);

                                Toast.makeText(getContext(), "add friend successfully!", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("chat", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "chat", Toast.LENGTH_LONG)
                                .show();
                        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_layout, new ChatFragment()).commit();

                    }
                }).setNeutralButton("cancel", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "cancel successfully", Toast.LENGTH_LONG)
                                .show();
                    }
                }).create();

                dialog.show();


            }
        });
    }

    public void add_friend(final int userid_ask, final int userid_target){

                JSONObject jFriend = new JSONObject();
                try {
                    jFriend.put("method", 7);
                    jFriend.put("user_ask", userid_ask);
                    jFriend.put("userid_target", userid_target);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    bWriter.write(jFriend + "\n");
                    bWriter.flush();

                    String line;
                    while ((line = bReader.readLine()) != null) {

                        Log.i(TAG, line);
                        JSONObject jLine = null;
                        try {
                            jLine = new JSONObject(line);

                            if (jLine.getInt("method") == 7) {
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


    public void openThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String line;
                    Log.e(TAG, "----------waiting");
                    while ((line = bReader.readLine()) != null) {

                        Log.i(TAG, line);
                        JSONObject jLine = null;
                        try {
                            jLine = new JSONObject(line);

                                post = new Post(jLine.getString("timestamp"), jLine.getInt("userId"), jLine.getString("postText"), jLine.getDouble("location"), jLine.getInt("image"));
                                posts.add(post);

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






/*package fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.DialogInterface.OnClickListener;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Adapter.PostAdapter;
import db.PostDB;
import configuration.MyConf;
import entities.Post;
import entities.User;
import activity.MainActivity;
import service.ChatService;
import service.SocketService;


public class PostFragment extends Fragment implements MyConf {
    private static PostDB postDB;
    private ArrayList<Post> posts;
    private Post post;
    private String postText;
    double location;
    String timestamp;

    private EditText etPost;
    private ListView lvPosts;
    final String TAG = "PostFragment";

    private Socket socket = null;
    private BufferedReader bReader = null;
    private BufferedWriter bWriter = null;


    public PostFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, null);

        posts = new ArrayList<Post>();
        location = 1000.0;
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm");
        timestamp = format.format(date);

        etPost = (EditText) view.findViewById(R.id.etPostpost);
        lvPosts = (ListView) view.findViewById(R.id.lvPostposts);
        postDB = new PostDB(getActivity().getApplicationContext());


        bReader = ChatService.br;
        bWriter = ChatService.bw;


//        socket = MainActivity.getSocket();
//        bReader = MainActivity.getBufferedReader();
//        bWriter = MainActivity.getBufferedWriter();
        Log.e(TAG, "before enter back");
        openThread();


        SQLiteDatabase dbRead = postDB.getReadableDatabase();
        Cursor cursor = dbRead.query("posts", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            postText = cursor.getString(cursor.getColumnIndex("post"));
            post = new Post(timestamp, User.getUserId(), postText, location, User.getImage());
            posts.add(post);
        }
        dbRead.close();

        populateListView();
        registerClickCallback();

       // ShowPost();

        view.findViewById(R.id.btnPostSend).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get postText -> post
                postText = etPost.getText().toString();
                post = new Post(timestamp, User.getUserId(), postText, location, User.getImage());
                // add to posts
                posts.add(post);
                // send to server -> json
                JSONObject jPost = new JSONObject();
                try {
                    jPost.put("method", 1);
                    jPost.put("timestamp", post.getTimestamp());
                    jPost.put("userId", post.getUserId());
                    jPost.put("postText", post.getPostText());
                    jPost.put("location", post.getLocation());
                    jPost.put("image", post.getImage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    bWriter.write(jPost + "\n");
                    bWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // put into local database
                SQLiteDatabase dbWrite = postDB.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("userId", post.getUserId()); // default values
                cv.put("timestamp", post.getTimestamp());
                cv.put("distance", post.getLocation());
                cv.put("post", post.getPostText());
                dbWrite.insert("posts", null, cv);
                // shouwei
                etPost.setText("");
                dbWrite.close();
            }
        });

        return view;
    }

    //-------------------------------------------------------------------------------
    private void populateListView() {
        PostAdapter adapter = new PostAdapter(getActivity().getApplicationContext(), posts);
        lvPosts.setAdapter(adapter);
    }

    private void registerClickCallback() {
        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Post target_post = (Post) lvPosts.getAdapter().getItem(position);
                final int userid_target = (int) target_post.getUserId();

                Dialog dialog = new AlertDialog.Builder(getActivity()).setIcon(
                        android.R.drawable.btn_star).setTitle("choices").setMessage(
                        "test").setPositiveButton("add friend",
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                add_friend(User.getUserId(), userid_target);

                                Toast.makeText(getContext(), "add friend successfully!", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("chat", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "chat", Toast.LENGTH_LONG)
                                .show();
                        getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_layout, new ChatFragment()).commit();

                    }
                }).setNeutralButton("cancel", new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getContext(), "cancel successfully", Toast.LENGTH_LONG)
                                .show();
                    }
                }).create();

                dialog.show();


            }
        });
    }

    public void openThread() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String line;
                    Log.e(TAG, "----------waiting");
                    //bWriter.write("test from client\n");
                    //bWriter.flush();
                    while ((line = bReader.readLine()) != null) {
                        Log.i(TAG, line);
                        JSONObject jLine = null;
                        try {
                            jLine = new JSONObject(line);
                            post = new Post(jLine.getString("timestamp"), jLine.getInt("userId"), jLine.getString("postText"), jLine.getDouble("location"), jLine.getInt("image"));
                            posts.add(post);
                            Log.i(TAG, post.getPostText());
                            Log.i(TAG, "added");

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


    public void updateFriendList() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String line;
                    Log.e(TAG, "----------waiting friendList");
                    //bWriter.write("test from client\n");
                    //bWriter.flush();
                    while ((line = bReader.readLine()) != null) {
                        Log.i(TAG, line);
                        JSONObject userlist = null;
                        User user = null;
                        try {
                            //userlist = new JSONObject("userlist");
                            userlist= new JSONObject(line);

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




    public void ShowPost() {
        AsyncTask<String, String, Void> read = new AsyncTask<String, String, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    Log.e(TAG, "I am here3");
                    bReader = new BufferedReader(new InputStreamReader(MainActivity.getSocket().getInputStream(), "UTF-8"));
                    bWriter = new BufferedWriter(new OutputStreamWriter(MainActivity.getSocket().getOutputStream(), "UTF-8"));
                    Log.e(TAG, "I am here4");
                    publishProgress("@success");
                    String line;
                    while ((line = bReader.readLine()) != null) {
                        Log.e(TAG,line);
                        publishProgress(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onProgressUpdate(String... values) {
                    Log.e(TAG, "----------waiting");
                    Log.e(TAG, values[0]);
                    JSONObject jLine = null;
                    try {
                        jLine = new JSONObject(values[0]);
                        post = new Post(jLine.getString("timestamp"), jLine.getInt("userId"), jLine.getString("postText"), jLine.getDouble("location"), jLine.getInt("image"));
                        posts.add(post);
                        Log.i(TAG, post.getPostText());
                        Log.i(TAG, "added");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

        };
        read.execute();
    }



    //add friend
    public void add_friend(int userid_ask, int userid_target){
        //先把自己和对方的id发到服务器
        //服务器检查好友表中是否已经存在

        //如果没有存在
        //user1是小的，user2是id号大的
        //加入数据库

        //如果已存在，返回已是你的好友！
        JSONObject jFriend = new JSONObject();
        try {
            jFriend.put("method", 7);
            jFriend.put("user_ask", userid_ask);
            jFriend.put("userid_target", userid_target);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            bWriter.write(jFriend + "\n");
            bWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, User.getUserId() + "\tonDestroy");

    }


}
*/