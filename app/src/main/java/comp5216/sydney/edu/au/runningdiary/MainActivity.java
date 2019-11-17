package comp5216.sydney.edu.au.runningdiary;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import comp5216.sydney.edu.au.runningdiary.Fragment.FragmentCalu;
import comp5216.sydney.edu.au.runningdiary.Fragment.FragmentHome;
import comp5216.sydney.edu.au.runningdiary.Fragment.FragmentLog;
import comp5216.sydney.edu.au.runningdiary.Fragment.FragmentMusic;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {


    private FragmentManager fragmentManager;

    private RadioButton mainHome;
    private RadioGroup group;

    Fragment homeFragment=new FragmentHome();
    Fragment musicFragment=new FragmentMusic();
    Fragment caluFragment=new FragmentCalu();
    Fragment fragment=new FragmentHome();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mainHome = findViewById(R.id.homeBtn);
        group = findViewById(R.id.radioGroup);
        fragmentManager = getSupportFragmentManager();
        mainHome.setChecked(true);
        group.setOnCheckedChangeListener(this);
        changeFragment(homeFragment, false);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.homeBtn:
                changeFragment(homeFragment, true);
                break;
            case R.id.musicBtn:
                changeFragment(musicFragment, true);
                break;
            case R.id.logBtn:
                changeFragment(new FragmentLog(), true);
                break;
            case R.id.caluBtn:
                changeFragment(caluFragment, true);
                break;
            default:
                break;
        }
    }

    public void changeFragment(Fragment fragM, boolean isInit) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!fragM.isAdded()) {
            transaction.hide(fragment).add(R.id.mainContent, fragM).commit();
        } else {
            transaction.hide(fragment).show(fragM).commit();
        }
        fragment = fragM;
        if (!isInit) {
            transaction.addToBackStack(null);
        }
    }
}
