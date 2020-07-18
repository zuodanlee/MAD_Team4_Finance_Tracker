package sg.edu.np.financetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    sharedPref sharedPref;
    final String TAG = "FinanceTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new sharedPref(this);
        if(sharedPref.loadNightMode()){
            setTheme(R.style.darkTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch(menuItem.getItemId()){
                    case R.id.home:
                        Log.v(TAG, "Going to Home...");
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.settings:
                        Log.v(TAG, "Going to Settings...");
                        selectedFragment = new SettingFragment();
                        break;
                    case R.id.report:
                        Log.v(TAG, "Going to Report...");
                        selectedFragment = new ReportFragment();
                        break;
                    case R.id.history:
                        Log.v(TAG, "Going to History...");
                        selectedFragment = new TransactionHistoryFragment();
                        break;
                    case R.id.goals:
                        Log.v(TAG, "Going to Goals...");
                        selectedFragment = new GoalsFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                return true;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String defaultFragment = sharedPreferences.getString("default page",null);
        if (savedInstanceState == null) {
            if (defaultFragment == "settings") {
                Log.v(TAG, "Loading Settings...");
                bottomNavigationView.getMenu().findItem(R.id.settings).setChecked(true);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
            }
            else {
                Log.v(TAG, "Loading Home...");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            }
            editor.putString("default page", "home");
            editor.apply();
        }
    }
}
