package com.slimsim.mumet_test_merge;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SlimSim on 28/12/15.
 */
public class DBHelper  extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.Song.CREATE_TABLE);
        db.execSQL(DBContract.SongList.CREATE_TABLE);
        db.execSQL(DBContract.SongList_Song.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.Song.DELETE_TABLE);
        db.execSQL(DBContract.SongList.DELETE_TABLE);
        db.execSQL(DBContract.SongList_Song.DELETE_TABLE);
        onCreate(db);
    }
}