package com.example.davidkladd.id3libtest;
// uses Media.Store to get audio details, not sure when all the scanning is done perhapse android service
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaScannerConnection;

public class MainActivity extends AppCompatActivity {
    Button mybutt;
    TextView myTextView;
    final public String TAG = "Dave";

    int titleColumnIndex;
    String title;

    int artistColumnIndex;
    String artist;

    int imageCounter = 0;
    Context mycontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mycontext = getApplicationContext();
        myTextView = findViewById(R.id.myTestView);

        mybutt = findViewById(R.id.button);
        mybutt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTextView.setText("yay");
                Log.d(TAG, "onClick: yay ");

                try{
                    myTextView.setText("ooowweee");
                    RunnableSearchDb runable = new RunnableSearchDb();
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
                MediaStore.Audio.Media.ALBUM
        };

        @Override
        public void run() {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            Log.d(TAG, "run: " + uri.getPath());
            String search = MediaStore.Audio.Media.TITLE + " LIKE ? ";
            String[] searchy = {"%w%"};
            try {
                Cursor cursor = mycontext.getContentResolver().query(uri, columns, MediaStore.Audio.Media.TITLE + " LIKE ? ", searchy, null);
                assert cursor != null;
                int mCount = cursor.getCount();
                Log.d(TAG, "number of results=" + mCount);
                for (int i = mCount - 1; i >= 0; i--) {
                    if (imageCounter <= 370) {
                        cursor.moveToPosition(i);

                        titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                        title = cursor.getString(titleColumnIndex);
                        artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                        artist = cursor.getString(artistColumnIndex);

                        imageCounter++;
                        Log.d(TAG, "runin: " + imageCounter);
                        Log.d(TAG, "scaned: " + artist + " - " + title );

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
