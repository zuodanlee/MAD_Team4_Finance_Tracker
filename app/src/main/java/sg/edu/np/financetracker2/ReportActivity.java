package sg.edu.np.financetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class ReportActivity extends AppCompatActivity {
    sharedPref sharedPref;
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
        setContentView(R.layout.activity_report);

        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.home:
                        Intent intent2 = new Intent(ReportActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.settings:
                        Intent intent3 = new Intent(ReportActivity.this, SettingActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.history:
                        Intent intent4 = new Intent(ReportActivity.this, TransactionHistoryActivity.class);
                        startActivity(intent4);
                        break;

                }

                return false;
            }
        });
    }
}
