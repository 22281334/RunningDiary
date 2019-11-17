package comp5216.sydney.edu.au.runningdiary.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import comp5216.sydney.edu.au.runningdiary.Adapter.LogListAdapter;
import comp5216.sydney.edu.au.runningdiary.R;
import comp5216.sydney.edu.au.runningdiary.Support.RunningLogList;
import comp5216.sydney.edu.au.runningdiary.Support.SDUtils;

public class FragmentLog extends Fragment {
    final String TOTAL_DISTANCE = "Total Distance: ";
    final String TOTAL_TIME = "Total Time: ";
    final String AVERAGE_SPEED = "Average Speed: ";
    final String AVERAGE_PACE = "Average Pace: ";
    final String FILE_PATH = "/storage/emulated/0/Android/data/comp5216.sydney.edu.au.runningdiary/cache/runningData.txt";
    float totalDistance = 0;
    float averageSpeed = 0;
    float averagePace = 0;
    float totalTime = 0;

    private LogListAdapter myAdapter = null;
    List<RunningLogList> myListData = new LinkedList<RunningLogList>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_index, null);
        ListView logListView = view.findViewById(R.id.logListView);
        TextView weeklyAverageView = view.findViewById(R.id.weeklyAverageView);

        // read data from local storage
        loadData();
        weeklyAverageView.setText(TOTAL_DISTANCE + totalDistance + " m" + "\n" +
                TOTAL_TIME + totalTime + " s" + "\n" +
                AVERAGE_SPEED + averageSpeed + " m/s" + "\n" +
                AVERAGE_PACE + averagePace + "s/m");

        myAdapter = new LogListAdapter((LinkedList<RunningLogList>) myListData, getContext());
        logListView.setAdapter(myAdapter);
        return view;
    }


    /**
     * load data from local storage
     *
     * @return String of the file content
     */
    private String loadData() {
        byte[] bytes = SDUtils.loadDataFromSDCard(FILE_PATH);
        String data = new String(bytes);
        for (String str : data.split("%")) {
            RunningLogList runningLogList = null;
            List list = new ArrayList();
            for (String str1 : str.split(",")) {
                list.add(str1);
            }
            String time = list.get(0).toString();
            String distance = list.get(1).toString();
            String costTime = list.get(2).toString();
            String speed = list.get(3).toString();
            String pace = list.get(4).toString();
            totalDistance += Float.parseFloat(distance);
            averageSpeed += Float.parseFloat(speed) / 7;
            averagePace += Float.parseFloat(pace) / 7;
            totalTime += Float.parseFloat(costTime);

            runningLogList = new RunningLogList("Time: " + time,
                    "Distance: " + distance + " m",
                    "Cost Time: " + costTime + " s",
                    "Speed: " + speed + "m/s",
                    "Pace: " + pace + "s/m");
            myListData.add(runningLogList);
        }
        return data;
    }
}
