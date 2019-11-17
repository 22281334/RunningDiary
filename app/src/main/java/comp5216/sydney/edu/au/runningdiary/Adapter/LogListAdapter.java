package comp5216.sydney.edu.au.runningdiary.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import comp5216.sydney.edu.au.runningdiary.Support.RunningLogList;
import comp5216.sydney.edu.au.runningdiary.R;

public class LogListAdapter extends BaseAdapter {

    private LinkedList<RunningLogList> linkedListData;
    private Context myContext;

    public LogListAdapter(LinkedList<RunningLogList> linkedListData, Context myContext) {
        this.linkedListData = linkedListData;
        this.myContext = myContext;
    }

    @Override
    public int getCount() {
        return linkedListData.size();
    }

    @Override
    public Object getItem(int position) {
        return linkedListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        convertView = LayoutInflater.from(myContext).inflate(R.layout.log_list_adapter, parent, false);
        TextView runTime = (TextView) convertView.findViewById(R.id.timeView);
        TextView distance = (TextView) convertView.findViewById(R.id.distanceView);
        TextView costTime = (TextView) convertView.findViewById(R.id.costTimeView);
        TextView speed = (TextView) convertView.findViewById(R.id.speedView);
        TextView pace= (TextView) convertView.findViewById(R.id.paceView);

        runTime.setText(linkedListData.get(position).getRunTime());
        distance.setText(linkedListData.get(position).getDistance());
        costTime.setText(linkedListData.get(position).getCostTime());
        speed.setText(linkedListData.get(position).getSpeed());
        pace.setText(linkedListData.get(position).getPace());

        return convertView;
    }

}
