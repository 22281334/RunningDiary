package comp5216.sydney.edu.au.runningdiary.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import comp5216.sydney.edu.au.runningdiary.R;

public class FragmentCalu extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calu_index, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button caluBtn = getActivity().findViewById(R.id.caluPaceBtn);
        final TextView displayText = getActivity().findViewById(R.id.displayTextView);
        final EditText timeHourText = getActivity().findViewById(R.id.timeHourText);
        final EditText timeMinutesText = getActivity().findViewById(R.id.timeMinText);
        final EditText timeSecondText = getActivity().findViewById(R.id.timeSecondText);
        final EditText distanceKmText = getActivity().findViewById(R.id.distanceKmText);
        final EditText distanceMeterText = getActivity().findViewById(R.id.distenceMeterText);
        Button clearBtn = getActivity().findViewById(R.id.clearBtn);

        // calculate button listener
        caluBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String timeHour = timeHourText.getText().toString();
                String timeMin = timeMinutesText.getText().toString();
                String timeSec = timeSecondText.getText().toString();
                String distanceKm = distanceKmText.getText().toString();
                String distanceM = distanceMeterText.getText().toString();

                // set default value
                if (timeHour.equals("")) {
                    timeHour = "0";
                }
                if (timeMin.equals("")) {
                    timeMin = "0";
                }
                if (timeSec.equals("")) {
                    timeSec = "0";
                }
                if (distanceKm.equals("")) {
                    distanceKm = "0";
                }
                if (distanceM.equals("")) {
                    distanceM = "0";
                }
                // calculate
                float timeTotal = Float.parseFloat(timeHour) * 60 +
                        Float.parseFloat(timeMin) +
                        Float.parseFloat(timeSec) / 60;
                float distanceTotal = Float.parseFloat(distanceKm) * 1000 +
                        Float.parseFloat(distanceM);
                displayText.setText("Pace: " + timeTotal / distanceTotal + " Min/Meter" + "\n" +
                        timeTotal * 60 / distanceTotal + " Second/Meter\n" +
                        (timeTotal / 60) / (distanceTotal / 1000) + " Hour/Kilometer");
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayText.setText("Pace: - Min/Meter");
                timeHourText.setText("");
                timeMinutesText.setText("");
                timeSecondText.setText("");
                distanceKmText.setText("");
                distanceMeterText.setText("");
            }
        });

    }
}
