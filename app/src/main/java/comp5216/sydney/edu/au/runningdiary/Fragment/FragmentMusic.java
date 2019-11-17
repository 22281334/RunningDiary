package comp5216.sydney.edu.au.runningdiary.Fragment;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.au.runningdiary.Adapter.MusicAdapter;
import comp5216.sydney.edu.au.runningdiary.R;
import comp5216.sydney.edu.au.runningdiary.Support.Music;

public class FragmentMusic extends Fragment {

    private ListView listView;
    private MediaPlayer player;
    private boolean isPause = false;// music pause state
    private File file;// music file
    private TextView hint;
    private int playingNumer = 0;

    private List<Music> musics;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_index, null);
        setupMusicCompleteListener();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getActivity().findViewById(R.id.lstView);
        musics = getMusic();
        MusicAdapter adapter = new MusicAdapter(getContext(), R.layout.music_list_adapter, musics);
        listView.setAdapter(adapter);
        setupMusicCompleteListener();
        setupListViewListener();

    }

    private void setupListViewListener() {
        final Button previousBtn = (Button) getActivity().findViewById(R.id.previousBtn);//play previous button
        final Button playBtn = (Button) getActivity().findViewById(R.id.playBtn);// play/pause button
        final Button nextBtn = (Button) getActivity().findViewById(R.id.nextBtn);//play next music button
        hint = (TextView) getActivity().findViewById(R.id.musicHint);
        //set up music complete listener
        setupMusicCompleteListener();
        //music click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Music", "Clicked item " + position + musics.get(1).getUrl());
                if (player != null) {
                    // stop current music play
                    onDestroy();
                }
                playingNumer = position;
                setupMusicCompleteListener();
                play(playingNumer);
                playBtn.setBackgroundResource(R.drawable.ic_action_pause);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player != null) {
                    if (player.isPlaying() && !isPause) {
                        //stop play music
                        player.pause();
                        //change state
                        isPause = true;
                        //change icon
                        playBtn.setBackgroundResource(R.drawable.ic_action_play);
                        hint.setText("Playing: " + musics.get(playingNumer).getTitle());
                    } else {
                        //continue play music
                        player.start();
                        hint.setText("Playing: " + musics.get(playingNumer).getTitle());
                        //change state
                        isPause = false;
                        //change icon
                        playBtn.setBackgroundResource(R.drawable.ic_action_pause);
                    }
                } else {
                    //play music
                    play(playingNumer);
                    //change icon
                    playBtn.setBackgroundResource(R.drawable.ic_action_pause);
                }
            }
        });

        // previous music play listener
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                if (playingNumer != 0) {
                    playingNumer -= 1;
                } else {
                    playingNumer = 0;
                }

                //play music
                play(playingNumer);
                hint.setText("Playing: " + musics.get(playingNumer).getTitle());
            }
        });

        //next play music listener
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                playingNumer += 1;
                //play music
                play(playingNumer);
                hint.setText("Playing: " + musics.get(playingNumer).getTitle());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            //stop music play
            player.stop();
            player.release();
        } catch (Exception e) {

        }
    }

    private void setupMusicCompleteListener() {
        if (player != null) {
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    onDestroy();
                    playingNumer += 1;
                    play(playingNumer);
                    hint.setText("Playing: " + musics.get(playingNumer).getTitle());
                }
            });
        }
    }

    private void play(int playForListNumber) {
        // get music path
        file = new File(musics.get(playForListNumber).getUrl());
        if (file.exists()) {
            player = MediaPlayer.create(getContext(), Uri.parse(file.getAbsolutePath()));
        } else {
            hint.setText("File doesn't exist ！");
            return;
        }
        try {
            player.reset();
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
            player.start();
            hint.setText("Playing: " + musics.get(playingNumer).getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get music details
     */
    public List<Music> getMusic() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Music> musicList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                Music m = new Music();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                int ismusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
                if (ismusic != 0 && duration / (500 * 60) >= 1) {
                    m.setId(id);
                    m.setTitle(title);
                    m.setArtist(artist);
                    m.setDuration(duration);
                    m.setSize(size);
                    m.setUrl(url);
                    m.setAlbum(album);
                    m.setAlbum_id(album_id);
                    musicList.add(m);
                }
                cursor.moveToNext();
            }
        }
        return musicList;
    }
}







