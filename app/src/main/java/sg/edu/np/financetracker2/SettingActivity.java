package sg.edu.np.financetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ShareActionProvider;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;


public class SettingActivity extends AppCompatActivity {
    private Switch mySwitch;
    sharedPref sharedPref;
    final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NightMode
        sharedPref = new sharedPref(this);
        if(sharedPref.loadNightMode()==true){
            setTheme(R.style.darkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mySwitch=(Switch)findViewById(R.id.mySwitch);
        if(sharedPref.loadNightMode()==true){
            mySwitch.setChecked(true);
        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                    restartApp();
                }
                else{
                    sharedPref.setNightModeState(false);
                    restartApp();
                }
            }
        });

        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.home:
                        Intent intent2 = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.report:
                        Intent intent3 = new Intent(SettingActivity.this, ReportActivity.class);
                        startActivity(intent3);
                        break;
                }

                return false;
            }
        });


        //Clear Data(Not completed)
        Button clearData = (Button)findViewById(R.id.clearButton);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Data Cleared.");
            }
        });






    }
    public void restartApp(){
        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        startActivity(intent);
        finish();
    }

}
