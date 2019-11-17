package comp5216.sydney.edu.au.runningdiary.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import comp5216.sydney.edu.au.runningdiary.Support.Music;
import comp5216.sydney.edu.au.runningdiary.R;

public class MusicAdapter extends ArrayAdapter<Music> {

    Context context;
    int res;

    public MusicAdapter(Context context, int resource, List<Music> objects) {
        super(context, resource, objects);
        this.context = context;
        this.res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Music item = getItem(position);
        ViewHold viewHold;
        View view;
        if(convertView == null){
            view= LayoutInflater.from(context).inflate(res,parent,false);
            viewHold = new ViewHold();
            viewHold.name = (TextView) view.findViewById(R.id.musicTitleText);
            viewHold.artist = (TextView)view.findViewById(R.id.artistText);
            view.setTag(viewHold);
        }else {
            view = convertView;
            viewHold = (ViewHold)view.getTag();
        }

        viewHold.name.setText(item.getTitle());
        viewHold.artist.setText(item.getArtist());

        return view;

    }

    class  ViewHold{
        TextView name ;
        TextView artist;


    }
}