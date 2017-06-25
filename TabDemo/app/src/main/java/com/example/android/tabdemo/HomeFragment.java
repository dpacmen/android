package com.example.android.tabdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class HomeFragment extends Fragment {
    private ArrayList<SongInfo> songs = new ArrayList<>();
    RecyclerView recyclerView;
    SeekBar seekBar;
    SongAdapter songAdapter;
    MediaPlayer mediaPlayer;
    private android.os.Handler myHandler = new android.os.Handler();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        seekBar = (SeekBar) rootView.findViewById(R.id.seekbar);

//        SongInfo songInfo = new SongInfo("Cheap Thrills", "Sia", "http://fullgaana.com/siteuploads/files/sfd4/1522/Sia%20-%20Cheap%20Thrills%20(feat.%20Sean%20Paul)-(FullGaana.Com).mp3");
//        songs.add(songInfo);
        songAdapter = new SongAdapter(getActivity(), songs);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        DividerItemDecoration dm = new DividerItemDecoration(recyclerView.getContext(), lm.getOrientation());
        recyclerView.addItemDecoration(dm);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(songAdapter);

        songAdapter.setOnitemClickListener(new SongAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(final Button b, View v, final SongInfo obj, int position) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (b.getText().toString().equals("Stop")) {
                                b.setText("Play");
                                mediaPlayer.stop();
                                mediaPlayer.reset();
                                mediaPlayer.release();
                                mediaPlayer = null;
                            } else {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(obj.getUrl());
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaPlayer.start();
                                        b.setText("Stop");
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                myHandler.postDelayed(runnable, 100);
            }
        });
        checkUserPermission();

        Thread myThread = new runThread();
        myThread.start();

        return rootView;
    }

    public class runThread extends Thread {


        @Override
        public void run() {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("Runwa", "run: " + 1);
                if (mediaPlayer != null) {
                    seekBar.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    });

                    Log.d("Runwa", "run: " + mediaPlayer.getCurrentPosition());
                }
            }
        }

    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
                return;
            }
        }
        loadAudiofiles();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadAudiofiles();
        }
    }

    public void loadAudiofiles() {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, sortOrder);

        Log.d("MUSIC", String.valueOf(cursor.getCount()));

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                SongInfo s = new SongInfo(name, artist, url);
                songs.add(s);

            }
        }
        cursor.close();
        songAdapter = new SongAdapter(getActivity(), songs);
        songAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
