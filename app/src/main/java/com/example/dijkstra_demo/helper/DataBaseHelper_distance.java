package com.example.dijkstra_demo.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by FJS0420 on 2016/2/3.
 */
public class DataBaseHelper_distance extends SQLiteOpenHelper {
    private static final String TAG = DataBaseHelper_distance.class.getName();
    private static final String DB_NAME = "distance.db";
    private final static int VERSION = 1;
    private String sql_distance = "create table if not exists distance(s_id int primary key,e_id int,dis real)";
    public DataBaseHelper_distance(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_distance);

        Log.e(TAG, "数据库创建");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS distance");
        onCreate(db);
        Log.e(TAG,"数据库更新");
    }
}
