package com.example.dijkstra_demo.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by FJS0420 on 2016/2/1.
 */
public class DatabaseHelper_road extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper_road.class.getName();
    private static final String DB_NAME = "road.db";
    private final static int VERSION = 1;
    private Context context;
    private String sql_road = "create table if not exists road(id int primary key,lon real,lat real,name text)";
    public DatabaseHelper_road(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_road);
        Log.e(TAG, "数据库创建");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS road");
        onCreate(db);
        Log.e(TAG,"数据库更新");
    }
}
