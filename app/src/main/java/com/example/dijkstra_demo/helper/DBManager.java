package com.example.dijkstra_demo.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.dijkstra_demo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by FJS0420 on 2016/2/1.
 */
public class DBManager {

    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME1 = "road";
    public static final String DB_NAME2 = "distance";
    public static final String PACKAGE_NAME = "com.example.dijkstra_demo";
    public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
                                        + "/" + PACKAGE_NAME + "/databases"; //手机存放数据库的位置
    public SQLiteDatabase database_road;
    public  SQLiteDatabase database_distance;
    private Context context;

    public DBManager(Context context){
        this.context = context;
    }

    public void openDatabase(){
        this.database_road = this.openroad(DB_PATH + "/" + DB_NAME1);
        this.database_distance= this.opendistance(DB_PATH + "/" + DB_NAME2);
    }

    private SQLiteDatabase openroad(String dbfile) {
        SQLiteDatabase db = null;
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(
                        R.raw.road); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);
            return db;
        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return db;
    }
    private SQLiteDatabase opendistance(String dbfile) {
        SQLiteDatabase db = null;
        try {
            if (!(new File(dbfile).exists())) {//判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                InputStream is = this.context.getResources().openRawResource(
                        R.raw.distance); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            db = SQLiteDatabase.openOrCreateDatabase(dbfile,
                    null);

        } catch (FileNotFoundException e) {
            Log.e("Database", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Database", "IO exception");
            e.printStackTrace();
        }
        return db;
    }



    public void closeDatabase() {
        this.database_distance.close();
        this.database_road.close();

    }


}
