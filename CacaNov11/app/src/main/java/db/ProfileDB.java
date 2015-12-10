package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProfileDB extends SQLiteOpenHelper{

    public ProfileDB(Context context) {
        super(context, "db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE grades(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(255)," +
                "hometown VARCHAR(255)," +
                "introdution TEXT DEFAULT \"\"," +
                "like INTEGER DEFAULT \"\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
