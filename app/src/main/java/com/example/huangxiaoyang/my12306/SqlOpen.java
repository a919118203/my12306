package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HuangXiaoyang on 2018/09/04.
 */

public class SqlOpen extends SQLiteOpenHelper {
    public SqlOpen(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table lscx (cnt integer primary key autoincrement,s char[20] , e char[20])";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
