package com.slimsim.mumet_test_merge;


        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.support.v4.view.ViewPager;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.RadioButton;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.Timer;
        import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    /*
    private MediaPlayer mpClick;
    private MediaPlayer mpClack;
    */

    Metronome metronome = new Metronome();


    private int m_iLight;
    private Timer m_tTimer;
    private int m_iTotalNrOfBars = -1;
    private int m_iTotalNrOfBeats = 0;
    private int m_iCurrentNrOfBeats = 0;


    ListView listView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("TAG", "onCreate ->");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_test);

        m_iLight = 0;

//        mpClick = MediaPlayer.create(MainActivity.this, R.raw.click);
//        mpClack = MediaPlayer.create(MainActivity.this, R.raw.clack);

        readFromDB();

    }

    public void stopTimer(View view) {
        stopTimer();
    }

    public void stopTimer() {
        ((TextView) findViewById(R.id.currentTempo)).setText("-");
        ((TextView) findViewById(R.id.currentSong)).setText("-");
        m_iLight = 0;
        m_iCurrentNrOfBeats = 0;
        if(m_tTimer != null){
            m_tTimer.cancel();
            metronome.stop();
        }

    }

    public void addNewSong(View view) {
        setNewSong();
    }


    /****************************************************************************
     *                                                                          *
     * The following methods are for every song-view / song-widget              *
     *                                                                          *
     ****************************************************************************/

    public void getTempo(View view) {
        stopTimer(view);

        View parent = (View) view.getParent();
        TextView tvTempo = (TextView) parent.findViewById(R.id.songTempo);
        TextView tvName = (TextView) parent.findViewById(R.id.songName);
        TextView tvTimesignature = (TextView) parent.findViewById(R.id.songTimesignature);

        String sTempo = "" + tvTempo.getText();
        String sTimesignature = "" + tvTimesignature.getText();

        int bpm, iTimesignature;
        try {
            bpm = Integer.parseInt(sTempo);
            iTimesignature = Integer.parseInt(sTimesignature);
        } catch(NumberFormatException nfe) {
            // alert not good....
            bpm = 6;
            iTimesignature = 4;
        }

        ((TextView) findViewById(R.id.currentTempo)).setText(tvTempo.getText());
        ((TextView) findViewById(R.id.currentSong)).setText(tvName.getText());

        m_iTotalNrOfBeats = m_iTotalNrOfBars * iTimesignature;
        m_tTimer = new Timer();
        final long tempo = (long) 60000 / bpm;
        final int ifinalTimesignature = iTimesignature;
        final int[] aLightId = {
                R.id.light0, R.id.light1, R.id.light2, R.id.light3,
                R.id.light4, R.id.light5, R.id.light6, R.id.light7
        };
        final int[] aSpaceId = {
                R.id.space0, R.id.space1, R.id.space2, R.id.space3,
                R.id.space4, R.id.space5, R.id.space6, R.id.space7
        };

        for(int i = 0; i < aLightId.length; i++) {
            if(i < ifinalTimesignature) {
                findViewById(aLightId[i]).setVisibility(View.VISIBLE);
                findViewById(aSpaceId[i]).setVisibility(View.VISIBLE);
            } else {
                findViewById(aLightId[i]).setVisibility(View.GONE);
                findViewById(aSpaceId[i]).setVisibility(View.GONE);
            }
        }

        metronome.play(bpm, iTimesignature);
        m_tTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (m_iTotalNrOfBars != -1) {
                            m_iCurrentNrOfBeats++;
                            if (m_iCurrentNrOfBeats > m_iTotalNrOfBeats) {
                                stopTimer();
                            }
                        }
                        int iId;
                        for (int i = 0; i < ifinalTimesignature; i++) {
                            if (i == m_iLight) {
                                iId = R.drawable.light_on;
                            } else {
                                iId = R.drawable.light_off;
                            }
                            findViewById(aLightId[i]).setBackgroundResource(iId);
                        }
                        m_iLight = (++m_iLight % ifinalTimesignature);
                    }
                });
            }
        }, 0, tempo);
    }


    public void edit(View view) {

        View vSong = (View) view.getParent();
        TextView tvTempo = (TextView) vSong.findViewById(R.id.songTempo);
        TextView tvName = (TextView) vSong.findViewById(R.id.songName);
        TextView tvSongId = (TextView) vSong.findViewById(R.id.songId);
        TextView tvSongTimeSignature = (TextView) vSong.findViewById(R.id.songTimesignature);

        setNewSong();

        findViewById(R.id.addSongDeleteButton).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.addSongTitle)).setText(R.string.editSong);

        ((TextView) findViewById(R.id.addSongName)).setText(tvName.getText());
        ((TextView) findViewById(R.id.addSongTempo)).setText(tvTempo.getText());
        ((TextView) findViewById(R.id.addSongId)).setText(tvSongId.getText());
        ((TextView) findViewById(R.id.addSongTimesignature)).setText(tvSongTimeSignature.getText());

        String sTimesignature = "" + tvSongTimeSignature.getText();
        setTimesignature(sTimesignature);
    }

    private void setNewSong(){
        showNewSong();

        EditText etSongName = (EditText) findViewById(R.id.addSongName);
        etSongName.setFocusableInTouchMode(true);
        etSongName.requestFocus();


        View view = this.getCurrentFocus();
        if (view != null) {
//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }

    private void setSongList(){
        ((EditText)findViewById(R.id.addSongName) ).setText("");
        ((EditText)findViewById(R.id.addSongTempo) ).setText("");
        ((TextView)findViewById(R.id.addSongTimesignature) ).setText("");
        ((TextView)findViewById(R.id.addSongId) ).setText("");
        setTimesignature("4");

        findViewById(R.id.addSongDeleteButton).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.addSongTitle)).setText(R.string.addSong);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        showSongList();
    }

    private void setAutoStop() {
        EditText etAutoStopValue = (EditText) findViewById(R.id.autoStopValue);
        etAutoStopValue.setFocusableInTouchMode(true);
        etAutoStopValue.requestFocus();

        showAutoStop();
    }

    private void showNewSong() {
        showNone();
        findViewById(R.id.songCreateNewSongParent).setVisibility(View.VISIBLE);
    }

    private void showSongList() {
        showNone();
        findViewById(R.id.songListParent).setVisibility(View.VISIBLE);
    }

    private void showAutoStop() {
        showNone();
        findViewById(R.id.autoStopParent).setVisibility(View.VISIBLE);
    }

    private void showNone() {
        findViewById(R.id.songListParent).setVisibility(View.GONE);
        findViewById(R.id.songCreateNewSongParent).setVisibility(View.GONE);
        findViewById(R.id.autoStopParent).setVisibility(View.GONE);

    }

    public void newSongCancel(View view) {
        setSongList();
    }

    public void setAutoStop(View view) {
        setAutoStop();
    }

    public void autoStopOnButton(View view) {
        String sAutoStop = "" + ((TextView) findViewById(R.id.autoStopValue)).getText();

        int iValue;
        try {
            iValue = Integer.parseInt(sAutoStop);
        } catch(NumberFormatException nfe) {
            // alert not good....
            autoStopOffButton(view);
            return;
        }

        m_iTotalNrOfBars = iValue;
        ((TextView) findViewById(R.id.autoStopDisplayValue)).setText(sAutoStop);
        setSongList();
    }

    public void autoStopOffButton(View view) {
        m_iTotalNrOfBars = -1;
        ((TextView) findViewById(R.id.autoStopDisplayValue)).setText(R.string.no);
        setSongList();
    }

    public void newSongDelete(View view) {
        String sSongId = "" + ((TextView) findViewById(R.id.addSongId)).getText();

        long lID;
        try {
            lID = Long.parseLong(sSongId);
        } catch(NumberFormatException nfe) {
            // alert not good....
            lID = -1;
        }
        if (lID > -1) {
            removeSongFromDB(lID);
            removeSongFromXML(lID);
        }
        setSongList();
    }

    public void newSongSave(View view) {
        String sSongName = "" + ((EditText) findViewById(R.id.addSongName)).getText();
        String sSongTempo = "" + ((TextView) findViewById(R.id.addSongTempo)).getText();
        String sId = "" + ((TextView) findViewById(R.id.addSongId)).getText();
        String sTimesignature = "" + ((TextView) findViewById(R.id.addSongTimesignature)).getText();



        int iBPM, iTimesignature;
        try {
            iBPM = Integer.parseInt(sSongTempo);
            iTimesignature = Integer.parseInt(sTimesignature);
        } catch(NumberFormatException nfe) {
            // alert not good....
            iBPM = 6;
            iTimesignature = 4;
        }

        if(sId.equals("")) {
            long newId = saveSongToDB(sSongName, iBPM, iTimesignature);
            addSongToXML(sSongName, sSongTempo, iTimesignature, newId);
        } else {
            long lId = Long.parseLong(sId);
            updateSongInDB(sSongName, sSongTempo, iTimesignature, lId);
            updateSongInXML(sSongName, sSongTempo, iTimesignature, lId);
        }
        setSongList();
    }

    public void addSongToXML(String sName, String sBPM, int iTimeSignature, long iId) {

        LinearLayout llSong = (LinearLayout)View.inflate(this, R.layout.song, null);
        TextView tvSongName = (TextView) llSong.findViewById(R.id.songName);
        tvSongName.setText(sName);

        TextView tvTempo = (TextView) llSong.findViewById(R.id.songTempo);
        tvTempo.setText(sBPM);

        TextView tvTimesignature = (TextView) llSong.findViewById(R.id.songTimesignature);
        String sTimeSignature = "" + iTimeSignature;
        tvTimesignature.setText(sTimeSignature);

        TextView tvID = (TextView) llSong.findViewById(R.id.songId);
        String sId = "" + iId;
        tvID.setText(sId);

        ((LinearLayout) findViewById(R.id.songParent)).addView(llSong);

        llSong.findViewById(R.id.songSelectButton)
                .setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        edit(v);
                        return true;
                    }
                });

    }

    public int removeSongFromDB(long lID) {
        // Create new helper
        DBHelper dbHelper = new DBHelper(this);
        // Get the database. If it does not exist, this is where it will
        // also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // Define 'where' part of query.
        String selection = DBContract.Song._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(lID) };
        // Issue SQL statement.
        return db.delete(DBContract.Song.TABLE_NAME, selection, selectionArgs);
    }

    public void updateSongInDB(String sSongName, String sSongTempo, int iTimesignature, long lId) {

        // Create new helper
        DBHelper dbHelper = new DBHelper(this);
        // Get the database. If it does not exist, this is where it will
        // also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();



        // Create insert entries
        ContentValues values = new ContentValues();
        values.put(DBContract.Song.COLUMN_NAME_SONG_NAME, sSongName);
        values.put(DBContract.Song.COLUMN_NAME_SONG_TEMPO, sSongTempo);
        values.put(DBContract.Song.COLUMN_NAME_SONG_TIME_SIGNATURE, iTimesignature);

        // Define 'where' part of query.
        String selection = DBContract.Song._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(lId) };
        // Issue SQL statement.

        db.update(DBContract.Song.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateSongInXML(String sSongName, String sSongTempo, int iTimesignature, long lId) {
        LinearLayout llSongParent = (LinearLayout) findViewById(R.id.songParent);
        int iSongParentLength = llSongParent.getChildCount();
        for (int i = 0; i < iSongParentLength; i++ ) {
            View song = llSongParent.getChildAt(i);
            String sCurrentID = "" + ((TextView) song.findViewById(R.id.songId)).getText();
            long lCurrentID = Long.parseLong(sCurrentID);
            if ( lId == lCurrentID ) {
                String sTimesignature = "" + iTimesignature;
                ((TextView) song.findViewById(R.id.songName)).setText(sSongName);
                ((TextView) song.findViewById(R.id.songTempo)).setText(sSongTempo);
                ((TextView) song.findViewById(R.id.songTimesignature)).setText(sTimesignature);
                break;
            }
        }
    }

    public void removeSongFromXML(long lID) {
        LinearLayout llSongParent = (LinearLayout) findViewById(R.id.songParent);
        int iSongParentLength = llSongParent.getChildCount();
        for (int i = 0; i < iSongParentLength; i++ ) {
            View song = llSongParent.getChildAt(i);
            String sCurrentID = "" + ((TextView) song.findViewById(R.id.songId)).getText();
            long lCurrentID = Long.parseLong(sCurrentID);
            if ( lID == lCurrentID ) {
                llSongParent.removeViewAt(i);
                break;
            }
        }
    }

    public long saveSongToDB(String sName, int iBPM, int iTimeSignature) {
        // Create new helper
        DBHelper dbHelper = new DBHelper(this);
        // Get the database. If it does not exist, this is where it will
        // also be created.
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // Create insert entries
        ContentValues values = new ContentValues();
        values.put(DBContract.Song.COLUMN_NAME_SONG_NAME, sName);
        values.put(DBContract.Song.COLUMN_NAME_SONG_TEMPO, iBPM);
        values.put(DBContract.Song.COLUMN_NAME_SONG_TIME_SIGNATURE, iTimeSignature);


        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(
                DBContract.Song.TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public void readFromDB() {
        // Create new helper
        DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();


// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                DBContract.Song._ID,
                DBContract.Song.COLUMN_NAME_SONG_NAME,
                DBContract.Song.COLUMN_NAME_SONG_TEMPO,
                DBContract.Song.COLUMN_NAME_SONG_TIME_SIGNATURE
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                DBContract.Song._ID + " ASC";

        Cursor cursor = db.query(
                DBContract.Song.TABLE_NAME,  // The table to query
                projection,                  // The columns to return
                null, /*selection,*/         // The columns for the WHERE clause
                null, /*selectionArgs,*/     // The values for the WHERE clause
                null,                        // don't group the rows
                null,                        // don't filter by row groups
                sortOrder                    // The sort order
        );
        long itemId;
        String songName;
        String songTempo;
        int songTimesignature;

        while(cursor.moveToNext()) {
            itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DBContract.Song._ID)
            );
            songName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Song.COLUMN_NAME_SONG_NAME)
            );
            songTempo = "" + cursor.getInt(
                    cursor.getColumnIndexOrThrow(DBContract.Song.COLUMN_NAME_SONG_TEMPO)
            );

            songTimesignature = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DBContract.Song.COLUMN_NAME_SONG_TIME_SIGNATURE)
            );


            addSongToXML(songName, songTempo, songTimesignature, itemId);
        }
    }


    public void addSongCheckTimesignatureNumber(View view) {
        String sTimesignature;
        try {
            sTimesignature = "" + ((RadioButton) view).getText();
        } catch (Exception e) {
            sTimesignature = "4";
        }

        setTimesignature(sTimesignature);

    }

    public void setTimesignature(String sTimesignature) {

        ((TextView) findViewById(R.id.addSongTimesignature)).setText(sTimesignature);

        LinearLayout llRow1 = ((LinearLayout) findViewById(R.id.addSongTimesignatureRow1));
        LinearLayout llRow2 = ((LinearLayout) findViewById(R.id.addSongTimesignatureRow2));
        for(int i=0; i< llRow1.getChildCount(); i++) {
            RadioButton rbButton1 = ((RadioButton) llRow1.getChildAt(i));
            RadioButton rbButton2 = ((RadioButton) llRow2.getChildAt(i));
            rbButton1.setChecked(sTimesignature.equals(rbButton1.getText()));
            rbButton2.setChecked(sTimesignature.equals(rbButton2.getText()));
        }
    }

    public void setCreateSongList(View view) {
        createSongList csl = new createSongList();
//        setContentView(R.layout.create_song_list);
//        csl.onCreate(view);
        setContentView(R.layout.create_song_list);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.listViewSongList);

        // Defined Array values to show in ListView
        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Log.v("TAG", "on click in list view, create songlist");

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });
    }
}