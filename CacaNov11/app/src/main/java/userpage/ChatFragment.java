package userpage;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import configuration.MyConf;


public class ChatFragment extends android.support.v4.app.Fragment implements MyConf {
    private EditText ip;
    private EditText input;
    private TextView chatShow;
    // temp
    EditText etuserId2;
    final String TAG = "ChatFragment";
    private int userId2;
    private String time;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_chat, null);

        input = (EditText) view.findViewById(R.id.etChatInput);
        chatShow = (TextView) view.findViewById(R.id.tvChatChatShow);
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("HH:mm");
        time = format.format(date);

        view.findViewById(R.id.btnChatSend).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String msg = input.getText().toString();
                sendMsg(msg);
            }
        });
        return view;
    }

// ------------------------------------------------------------

    Socket socket = null;
    BufferedReader bReader = null;
    BufferedWriter bWriter = null;

    public void connect(final String ipAddress) {
        AsyncTask<String, String, Void> read = new AsyncTask<String, String, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    socket = new Socket(ipAddress, 80);
                    bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                    publishProgress("@success");
                    //-----------init---------
                    JSONObject jInit = new JSONObject();
                    try {
                        jInit.put("method", 0);
                        jInit.put("userId", MyConf.USERID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bWriter.write(jInit + "\n");
                    bWriter.flush();
                    //-----------init end---------
                    String line;
                    while ((line = bReader.readLine()) != null) {
                        publishProgress(line);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if (values[0].equals("@success"))
                    Toast.makeText(getActivity().getApplicationContext(), "connection succeeded", Toast.LENGTH_SHORT).show();
                else {
                    // parse json msg
                    String msg="";
                    try {
                        JSONObject jMsg = new JSONObject(values[0]);
                        msg = jMsg.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    chatShow.append(msg + "\n");
                }
            }
        };
        read.execute();
    }

    public void sendMsg(String msg) {
        try {
            chatShow.append("I: " + msg + "\n");
            JSONObject jMsg = new JSONObject();
            jMsg.put("method", 2);
            jMsg.put("userId", MyConf.USERID);
            jMsg.put("userId2", userId2);
            jMsg.put("msg", msg);
            jMsg.put("time", time);
            bWriter.write(jMsg + "\n");
            bWriter.flush();
            input.setText("");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // ---------------------------------------------------------

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Chat onDestroyView");
        try {
            if (socket != null)
                socket.close();
            if (bReader != null)
                bReader.close();
            if (bWriter != null)
                bWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}