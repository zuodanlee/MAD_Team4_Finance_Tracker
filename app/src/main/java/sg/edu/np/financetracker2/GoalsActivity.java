package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class GoalsActivity extends AppCompatActivity {
    sharedPref sharedPref;
    ArrayList<Goal> goals = new ArrayList<>();
    private Button addGoal;
    private EditText newGoal;

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
        setContentView(R.layout.activity_goals);

        final RecyclerView recyclerGoal = findViewById(R.id.recyclerGoal);
        final recyclerGoalAdapter gAdapter = new recyclerGoalAdapter(goals);
        LinearLayoutManager gLayoutManager = new LinearLayoutManager(this);
        recyclerGoal.setLayoutManager(gLayoutManager);
        recyclerGoal.setItemAnimator(new DefaultItemAnimator());
        recyclerGoal.setAdapter(gAdapter);

        addGoal = (Button) findViewById(R.id.addGoal);
        newGoal = (EditText) findViewById(R.id.goalText);
        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = newGoal.getText().toString();
                Goal goal = new Goal();
                goal.setGoal(input);
                goals.add(goal);
                gAdapter.notifyDataSetChanged();
                showNewEntry(recyclerGoal,goals);
                newGoal.setText("");
            }
        });
        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.settings:
                        Intent intent = new Intent(GoalsActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.report:
                        Intent intent2 = new Intent(GoalsActivity.this, ReportActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.history:
                        Intent intent3 = new Intent(GoalsActivity.this, TransactionHistoryActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.home:
                        Intent intent4 = new Intent(GoalsActivity.this, MainActivity.class);
                        startActivity(intent4);
                        break;
                }

                return false;
            }
        });
    }
    private void showNewEntry(RecyclerView rv, ArrayList data){
        rv.scrollToPosition(data.size() - 1);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rv.getWindowToken(), 0);
    }
}
