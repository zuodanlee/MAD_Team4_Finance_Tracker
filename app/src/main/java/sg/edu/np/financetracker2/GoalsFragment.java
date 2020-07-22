package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class GoalsFragment extends Fragment {
    final String TAG = "FinanceTracker";
    ArrayList<Goal> goals = new ArrayList<>();
    private Button addGoal;
    private recyclerGoalAdapter gAdapter;
    private RecyclerView recyclerGoal;
    private View goalsView;
    double goalTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        goalsView = inflater.inflate(R.layout.fragment_goals, container, false);

        //Goal recyclerview adapter and layout manager
        recyclerGoal = goalsView.findViewById(R.id.recyclerGoal);
        gAdapter = new recyclerGoalAdapter(goals,getActivity());
        LinearLayoutManager gLayoutManager = new LinearLayoutManager(getActivity());
        recyclerGoal.setLayoutManager(gLayoutManager);
        recyclerGoal.setItemAnimator(new DefaultItemAnimator());
        recyclerGoal.setAdapter(gAdapter);

        addGoal = (Button) goalsView.findViewById(R.id.addGoal);
        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddGoalActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        return goalsView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadGoals(); //Load list of goals from shared prefs
        if (goalTotalExists() == false){
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().getApplicationContext().openFileOutput("goalTotal.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("0.00");
                outputStreamWriter.close();
                Log.v(TAG, "Created new goalTotal.txt");
            }
            catch (IOException e) {
                Log.e(TAG, "Exception! File write failed: " + e.toString());
            }
        }
    }

    //Display goal total
    @Override
    public void onStart() {
        super.onStart();
        final TextView totalAmt = (TextView) getActivity().findViewById(R.id.goalTotal);
        goalTotal = getGoalTotal();
        Log.v(TAG, "Goal total: " + goalTotal);
        Log.v(TAG, "Displaying goal total...");

        Double displayAmount = Math.abs(goalTotal);
        String displayString;
        DecimalFormat df = new DecimalFormat("0.00");
        displayString = "$" + df.format(displayAmount);
        totalAmt.setText(displayString);
    }

    //Update view after adding new goal
    @Override
    public void onResume(){
        super.onResume();
        loadGoals();
        recyclerGoal = goalsView.findViewById(R.id.recyclerGoal);
        gAdapter = new recyclerGoalAdapter(goals,getActivity());
        recyclerGoal.setAdapter(gAdapter);
    }

    //Load list of goals from shared prefs
    private void loadGoals(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Goal List",null);
        Type type = new TypeToken<ArrayList<Goal>>(){}.getType();
        goals =  gson.fromJson(json,type);

        if(goals == null){
            goals = new ArrayList<>();
        }
    }

    //Get goal total
    private Double getGoalTotal(){
        String data;
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = getActivity().openFileInput("goalTotal.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while((data = reader.readLine()) != null){
                stringBuffer.append(data);
            }
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return Double.parseDouble(stringBuffer.toString());
    }

    //Check if goalTotal.txt exists
    public boolean goalTotalExists() {
        File file = getActivity().getFileStreamPath("goalTotal.txt");
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}
