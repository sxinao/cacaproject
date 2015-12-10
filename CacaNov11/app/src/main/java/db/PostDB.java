package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/11/17.
 */
public class PostDB extends SQLiteOpenHelper {

    public PostDB(Context context) {
        super(context, "LocalDB", null, 1);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE posts(" + "userId INT DEFAULT 0," + "timestamp TEXT DEFAULT \"\"," + "distance DOUBLE DEFAULT 0,"
                + "post TEXT DEFAULT \"\")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

}