package com.example.davidkladd.id3libtest;
// uses Media.Store to get audio details, not sure when all the scanning is done perhapse android service
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.media.MediaScannerConnection;

public class MainActivity extends AppCompatActivity {
    Button mybutt;
    TextView myTextView;
    EditText editTextSearchString;
    public Handler mainHandler = new Handler();
    final public String TAG = "Dave";

    int titleColumnIndex;
    String title;

    int artistColumnIndex;
    String artist;

    int dataColumnIndex;
    String data;

    int albumColumnIndex;
    String album;

    int yearColumnIndex;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check whether this app has write external storage permission or not.
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
// If do not grant write external storage permission.
        if(readExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
        }

        myTextView = findViewById(R.id.myTextView);
        editTextSearchString = findViewById(R.id.editTextSearchString);

        mybutt = findViewById(R.id.button);
        mybutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myTextView.setText("");
                Log.d(TAG, "starting runnable");

                try{
                    myTextView.setText("");
                    RunnableSearchDb runable = new RunnableSearchDb(editTextSearchString.getText().toString());
                    new Thread(runable).start();
                }
                catch (Exception eee){
                    Log.d(TAG, "error ");
                }

            }
        });


    }

    class RunnableSearchDb implements Runnable {
        String[] columns = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DATA
        };
        String searchString;

        public RunnableSearchDb(String searchString) {
            this.searchString = searchString;
        }

        @Override
        public void run() {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            int imageCounter = 0;
            Context myAppContext = getApplicationContext();
            Log.d(TAG, "run: " + uri.getPath());
            String search = MediaStore.Audio.Media.TITLE + " LIKE ? ";
            //String[] searchy = {"%w%"};
            String[] searchy = {"%" + searchString + "%"};
            try {
                Cursor cursor = myAppContext.getContentResolver().query(uri, columns, MediaStore.Audio.Media.TITLE + " LIKE ? ", searchy, null);
                assert cursor != null;
                int mCount = cursor.getCount();
                final String numres = searchString + " gives " + mCount + " results \n";

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        myTextView.append(numres);

                    }
                });

                Log.d(TAG, "number of results=" + mCount);
                for (int i = mCount - 1; i >= 0; i--) {
                    if (imageCounter <= 3) {
                        cursor.moveToPosition(i);

                        titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                        title = cursor.getString(titleColumnIndex);
                        artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                        artist = cursor.getString(artistColumnIndex);

                        albumColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                        album = cursor.getString(albumColumnIndex);
                        dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                        data = cursor.getString(dataColumnIndex);
                        yearColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.YEAR);
                        year = cursor.getString(yearColumnIndex);

                        imageCounter++;
                        final String outputDetail = title + " " + artist + " " + album + " " + year + "\n" + data + "\n";
                        Log.d(TAG, "runin: " + imageCounter);
                        Log.d(TAG, "found " + outputDetail );

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                myTextView.append(outputDetail);

                            }
                        });

                    } else {
                        cursor.close();
                        break;
                    }
                }
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
