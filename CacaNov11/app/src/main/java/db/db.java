package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lucile on 15/11/24.
 */
public class db extends SQLiteOpenHelper {

    public db(Context context) {
        super(context, "LocalDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE posts(" + "userId INT DEFAULT 0," + "timestamp TEXT DEFAULT \"\"," + "distance DOUBLE DEFAULT 0,"
                + "text TEXT DEFAULT \"\")");

        db.execSQL("CREATE TABLE userinfo(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(255)," +
                "hometown VARCHAR(255)," +
                "introdution TEXT DEFAULT \"\"," +
                "like INTEGER DEFAULT \"\")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }



}
