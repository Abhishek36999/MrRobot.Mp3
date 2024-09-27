package com.example.mrrobotmp3;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    int currentSongIndex = 0;
    private static final int STORAGE_PERMISSION_CODE = 1;
    RecyclerView songRecyclerView;
    List<String> songList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        songRecyclerView = findViewById(R.id.songList);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(songRecyclerView.getContext(), layoutManager.getOrientation());
        songRecyclerView.addItemDecoration(dividerItemDecoration);
        Button playButton = findViewById(R.id.playButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button prevButton = findViewById(R.id.prevButton);

        // Request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            loadSongs();
        }

        // Basic button functionality (to be expanded later)
        playButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playButton.setText("Play");
            } else {
                mediaPlayer.start();
                playButton.setText("Pause");
            }
        });

        nextButton.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex + 1) % songList.size();
            playSong(songList.get(currentSongIndex));
        });

        prevButton.setOnClickListener(v -> {
            currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
            playSong(songList.get(currentSongIndex));
        });
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadSongs() {
        songList = getAudioFiles();
        SongAdapter songAdapter = new SongAdapter(songList, this::playSong);
        songRecyclerView.setAdapter(songAdapter);
    }

    private List<String> getAudioFiles() {
        List<String> songList = new ArrayList<>();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = getContentResolver().query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentPath = songCursor.getString(songPath);
                songList.add(currentPath);
            } while (songCursor.moveToNext());
            songCursor.close();
        }
        return songList;
    }

    private void playSong(String songPath) {
        // To be implemented
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(this, Uri.parse(songPath));
        mediaPlayer.start();
        Toast.makeText(this, "Playing: " + songPath, Toast.LENGTH_SHORT).show();
    }
}