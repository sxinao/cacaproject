package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/11/24.
 */
public class ChatDB extends SQLiteOpenHelper {
    public ChatDB(Context context) {
        super(context, "LocalDB", null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE chats(" + "userIdFrom INT DEFAULT 0," + "userIdTo INT DEFAULT 0," + "timestamp TEXT DEFAULT \"\","
                + "text TEXT DEFAULT \"\")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}
