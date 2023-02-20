package com.onkar.musicplayer;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private List<Integer> songQueue = new ArrayList<>();
    private int currentIndex = -1;
    private int songIndex;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        Button playButton = findViewById(R.id.play_button);
        Button pauseButton = findViewById(R.id.pause_button);
        Button stopButton = findViewById(R.id.stop_button);
        Button nextButton = findViewById(R.id.next_button);
        Button prevButton = findViewById(R.id.prev_button);

        songQueue.add(R.raw.faded);
        songQueue.add(R.raw.on_my_way);
        songQueue.add(R.raw.over_the_horizon);

        ListView songQueueListView = findViewById(R.id.song_queue_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getSongNames());
        songQueueListView.setAdapter(adapter);

        nextSong();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(MainActivity.this, getCurrentSongId());
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextSong();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevSong();
            }
        });
    }

    private void nextSong() {
        if (songIndex >= songQueue.size()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            songIndex = 0;
            return;
        }

        int songId = songQueue.get(songIndex);

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + songId));
            mediaPlayer.prepare();
            mediaPlayer.start();

            TextView songTitleTextView = findViewById(R.id.song_title);
            String songName = getResources().getResourceEntryName(songId).toUpperCase();
            songTitleTextView.setText("Now Playing: " + songName);

            int duration = mediaPlayer.getDuration();
            seekBar.setMax(duration);
            songIndex++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prevSong() {
        if (currentIndex > 0) {
            currentIndex--;
            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(MainActivity.this, getCurrentSongId());
            mediaPlayer.start();
        }
    }

    private List<String> getSongNames() {
        List<String> songNames = new ArrayList<>();
        for (int songId : songQueue) {
            String songName = getResources().getResourceEntryName(songId);
            songNames.add(songName);
        }
        return songNames;
    }

    private int getCurrentSongId() {
        return songQueue.get(currentIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


