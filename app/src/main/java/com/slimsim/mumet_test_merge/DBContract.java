package com.slimsim.mumet_test_merge;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by SlimSim on 27/12/15.
 */
public class DBContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final  String DATABASE_NAME      = "mumet_database.db";

    private static final String TEXT_TYPE          = " TEXT";
    private static final String INT_TYPE           = " INTEGER";
    private static final String COMMA_SEP          = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Song implements BaseColumns {
        public static final String TABLE_NAME = "Song";
        public static final String COLUMN_NAME_SONG_NAME = "SONG_NAME";
        public static final String COLUMN_NAME_SONG_TEMPO = "TEMPO";
        public static final String COLUMN_NAME_SONG_TIME_SIGNATURE = "TIME_SIGNATURE";

//        DBHelper mDbHelper = new DBHelper(this);

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_SONG_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SONG_TEMPO + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SONG_TIME_SIGNATURE + INT_TYPE +
                " )";

        public static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;




    }
    /* Inner class that defines the table contents */
    public static abstract class SongList implements BaseColumns {
        public static final String TABLE_NAME = "SongList";
        public static final String COLUMN_NAME_SONGLIST_NAME = "SONGLIST_NAME";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_SONGLIST_NAME + TEXT_TYPE +
                        " )";

        public static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /* Inner class that defines the table contents */
    public static abstract class SongList_Song implements BaseColumns {
        public static final String TABLE_NAME = "SongList_Song";
        public static final String COLUMN_NAME_SONGLIST_ID = "_ID_SongList";
        public static final String COLUMN_NAME_SONG_ID = "_ID_Song";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_SONGLIST_ID + INT_TYPE +
                        COLUMN_NAME_SONG_ID + INT_TYPE +
                        " )";

        public static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}