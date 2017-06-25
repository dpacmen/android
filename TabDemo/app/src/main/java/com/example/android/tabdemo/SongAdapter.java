package com.example.android.tabdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by deepak on 21/6/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    private ArrayList<SongInfo> songs = new ArrayList<>();
    Context context;

    OnitemClickListener onitemClickListener;

    public SongAdapter(Context context, ArrayList<SongInfo> songs) {
        this.songs = songs;
        this.context = context;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View myView = LayoutInflater.from(context).inflate(R.layout.row_song, parent, false);
        return new SongHolder(myView);
    }

    @Override
    public void onBindViewHolder(final SongHolder holder, final int position) {
        final SongInfo c = songs.get(position);
        holder.songName.setText(c.songName);
        holder.artistName.setText(c.artistName);
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onitemClickListener != null)
                    onitemClickListener.onItemClick(holder.playButton, v, c, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TextView songName, artistName;
        Button playButton;

        public SongHolder(View itemView) {
            super(itemView);
            songName = (TextView) itemView.findViewById(R.id.song_name);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);
            playButton = (Button) itemView.findViewById(R.id.play_button);
        }
    }

    public interface OnitemClickListener {
        void onItemClick(Button b, View v, SongInfo obj, int position);
    }

    public void setOnitemClickListener(OnitemClickListener onitemClickListener) {
        this.onitemClickListener = onitemClickListener;
    }
}
