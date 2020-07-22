package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPref {
    SharedPreferences mySharedPref;
    public sharedPref(Context context){
        mySharedPref = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
    }
    //this method will save the nightMode  State: True or False
    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }
    //this method will load the Night Mode State
    public boolean loadNightMode(){
        Boolean state =mySharedPref.getBoolean("NightMode",false);
        return state;
    }
    public void setNotificationState(Boolean state){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("Notification",state);
        editor.commit();
    }
    public boolean loadNotification(){
        Boolean state = mySharedPref.getBoolean("Notification",false);
        return state;
    }
    public void setNotificationTime(int hour, int minute){
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putInt("Hour",hour);
        editor.putInt("Minute",minute);
        editor.commit();
    }
    public int[] loadNotificationTime(){
        int hour = mySharedPref.getInt("Hour",0);
        int minute = mySharedPref.getInt("Minute",0);
        int[] time = {hour,minute};
        return time;
    }
}
