package login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import configuration.MyConf;
import exception.CustomException;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import userpage.User_Main;

public class MainActivity extends AppCompatActivity {
    private EditText etId;
    private EditText etPwd;
    private String strId = "";
    private String pwd = "";
    private boolean isValid;
    private boolean isGotten;
    private boolean isRunning;

    private static Socket socket = null;
    private static BufferedReader bReader = null;
    private static BufferedWriter bWriter = null;
    private final String TAG = "MainActivity";

    public static Socket getSocket() {
        return socket;
    }

    public static BufferedReader getBufferedReader() {
        return bReader;
    }

    public static BufferedWriter getBufferedWriter() {
        return bWriter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        isGotten = false;
        isValid = false;
        isRunning = true;
        connect();
        etId = (EditText) findViewById(R.id.userID);
        etPwd = (EditText) findViewById(R.id.password);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get id
                strId = etId.getText().toString().trim();
                //Get password
                pwd = etPwd.getText().toString().trim();

                //-----------------exception handling------------------
                // UserID cannot be null
                if (strId.matches("")) {
                    try {
                        throw new CustomException(1);
                    } catch (CustomException e) {
                        System.out.println(e.printError(1));
                        etId.setError("Id cannot be empty.");
                        return;
                    }
                }
                //Password cannot be null
                if (pwd.matches("")) {
                    try {
                        throw new CustomException(2);
                    } catch (CustomException e) {
                        System.out.println(e.printError(2));
                        etPwd.setError("Password cannot be empty.");
                        return;
                    }
                }


                //-----------------validate------------------
                while ((!isGotten) && isRunning) {
                    try {
                        Thread.sleep(5000);
                        Log.i(TAG, "Waiting for feedback from server...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isRunning) {
                    if (isValid) { //jump
                        Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(MainActivity.this, User_Main.class);
                        startActivity(i);
                    } else
                        Toast.makeText(MainActivity.this, "No such user.Please register.", Toast.LENGTH_LONG).show();

                    isGotten = false;
                }
            }
        });

        findViewById(R.id.btnNewUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public void connect() {
        AsyncTask<String, String, Void> read = new AsyncTask<String, String, Void>() {
            @Override
            protected Void doInBackground(String... params) {

                try {
                    socket = new Socket(MyConf.HOST, MyConf.PORT);
                    bReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    Log.i(TAG, "bwriter=" + bWriter);
                    if (socket != null)
                        publishProgress("@success");
                    else
                        publishProgress("@failure");
                    // query database -- via server
                    while ((strId.equals("") || pwd.equals("")) && isRunning) {
                        try {
                            Thread.sleep(5000);
                            Log.i(TAG, "Waiting for input...");
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (isRunning) {
                        JSONObject jQuery = new JSONObject();
                        try {
                            jQuery.put("method", 3);
                            jQuery.put("userId", strId);
                            jQuery.put("pwd", pwd);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        bWriter.write(jQuery + "\n");
                        bWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // read feedback from database -- via server
                String line;
                try {
                    while ((line = bReader.readLine()) != null) {
                        Log.i(TAG, line);
                        JSONObject jLine = null;
                        try {
                            jLine = new JSONObject(line);
                            isValid = jLine.getBoolean("isValid");
                            isGotten = true;
                            Log.i(TAG, isGotten + "----------------isGotten");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onProgressUpdate(String... values) {

                if (values[0].equals("@success"))
                    Toast.makeText(MainActivity.this, "connection succeeded", Toast.LENGTH_SHORT).show();
                else if (values[0].equals("@failure"))
                    Toast.makeText(MainActivity.this, "connection failed", Toast.LENGTH_SHORT).show();
            }
        };
        read.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
