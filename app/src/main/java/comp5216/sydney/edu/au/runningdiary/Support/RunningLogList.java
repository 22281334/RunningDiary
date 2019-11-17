package comp5216.sydney.edu.au.runningdiary.Support;


import androidx.appcompat.app.AppCompatActivity;

public class RunningLogList extends AppCompatActivity {

    private String runTime;
    private String distance;
    private String costTime;
    private String speed;
    private String pace;

    public RunningLogList(String runTime, String distance, String costTime, String speed, String pace) {

        this.runTime = runTime;
        this.distance = distance;
        this.costTime = costTime;
        this.speed = speed;
        this.pace = pace;

    }

    /**
     * get run time
     *
     * @return
     */
    public String getRunTime() {
        return runTime;
    }

    /**
     *get distance
     *
     * @return
     */
    public String getDistance() {
        return distance;
    }

    /**
     *get cost time
     *
     * @return
     */
    public String getCostTime() {
        return costTime;
    }

    /**
     *get speed
     *
     * @return
     */
    public String getSpeed() {
        return speed;
    }

    /**
     *get pace
     *
     * @return
     */
    public String getPace() {
        return pace;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }
}
