package userpage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import login.MainActivity;

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
        socket = MainActivity.getSocket();
        bReader = MainActivity.getBufferedReader();
        bWriter = MainActivity.getBufferedWriter();
        Log.i(TAG, "before enter back");
        //openThread();


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


        ShowPost();


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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.drawer_layout, new ChatFragment()).commit();
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
                    Log.i(TAG,"----------waiting");
                    bWriter.write("test from client\n");
                    bWriter.flush();
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


    public void ShowPost() {
        AsyncTask<String, String, Void> read = new AsyncTask<String, String, Void>() {
            @Override
            protected Void doInBackground(String... params) {

                return null;
            }

            protected void onProgressUpdate(String... values) {
                    try {
                        String line;
                        Log.i(TAG, "----------waiting");
                        bWriter.write("test from client\n");
                        bWriter.flush();
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

        };
        read.execute();
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, User.getUserId() + "\tonDestroy");
//        try {
//            if (socket != null)
//                socket.close();
//            if (bReader != null)
//                bReader.close();
//            if (bWriter != null)
//                bWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
