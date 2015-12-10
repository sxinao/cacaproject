package login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.caca.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import entities.User;
import userpage.User_Main;

public class RegisterActivity extends AppCompatActivity {
    private TextView tvUserId;
    private TextView tvPwd;
    private EditText etUserId;
    private EditText etPwd;
    private EditText etUserName;
    private EditText etImage;
    private EditText etIsMale;
    private EditText etWhatsUp;
    private final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etUserId = (EditText) findViewById(R.id.etSignUserId);
        etPwd = (EditText) findViewById(R.id.etSignPwd);
        etUserName=(EditText) findViewById(R.id.etSignUserName);
        etImage = (EditText) findViewById(R.id.etSignImage);
        etWhatsUp = (EditText) findViewById(R.id.etSignWhatsUp);
        findViewById(R.id.btnSignSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = etUserId.getText().toString();
                String pwd = etPwd.getText().toString();
                String userName=etUserName.getText().toString();
                String image=etImage.getText().toString();
                String isMale=etIsMale.getText().toString();
                String whatsUp=etWhatsUp.getText().toString();
                JSONObject jQuery = new JSONObject();
                try {
                    // form: String
                    jQuery.put("method", 5);
                    jQuery.put("userId", userId);
                    jQuery.put("pwd", pwd);
                    jQuery.put("userName",userName);
                    jQuery.put("image", image);
                    jQuery.put("isMale",isMale);
                    jQuery.put("whatsUp",whatsUp);
                    jQuery.put("distance", "1000.0");  // should be from gps --String
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                 // write to db
                try {
                    MainActivity.getBufferedWriter().write(jQuery+"\n");
                    MainActivity.getBufferedWriter().flush();
                    Log.i(TAG,"getbwriter="+MainActivity.getBufferedWriter());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //  create User instance
                User user=new User(userName,Integer.parseInt(userId), Integer.valueOf(image.substring(2),16).intValue(), Boolean.parseBoolean(isMale),whatsUp,pwd);
                Log.i(TAG, user.toString());
                // jump to post
                Intent i= new Intent(RegisterActivity.this, User_Main.class);
                startActivity(i);

            }
        });
    }
}
