package fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import configuration.MyConf;
import entities.User;
import service.ChatService;


public class ChatFragment extends android.support.v4.app.Fragment implements MyConf {
    private EditText input;
    private TextView chatShow;
    // temp
    final String TAG = "ChatFragment";
    private int userId2;
    private String time;

    private BufferedReader bReader = null;
    private BufferedWriter bWriter = null;

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

        bReader = ChatService.br;
        bWriter = ChatService.bw;
        openThread();
        Log.i(TAG, "executed therad");
        userId2 = PostFragment.getUserId2();
        Log.i(TAG, "userId2=" + userId2);

        view.findViewById(R.id.btnChatSend).setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String msg = input.getText().toString();
                sendMsg(msg);
            }
        });
        return view;
    }

    // ------------------------------------------------------------
    public void openThread() {
        AsyncTask<String, String, Void> read = new AsyncTask<String, String, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    String line;
                    Log.i(TAG, "-----waitin for line ");
                    Log.i(TAG,"active count="+Thread.activeCount());
                    while ((line = bReader.readLine()) != null) {
                        Log.i(TAG, line);
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
                Log.i(TAG,"value="+values[0]);
                // parse json msg
                String msg = "";
                try {
                    JSONObject jMsg = new JSONObject(values[0]);
                    msg = jMsg.getString("msg");
                    chatShow.append(msg + "\n");
                    Log.i(TAG,"appended");
                } catch (JSONException e) {
                    e.printStackTrace();
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
            jMsg.put("userId", User.getUserId());
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
}