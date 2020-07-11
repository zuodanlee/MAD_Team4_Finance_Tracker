package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GoalsFragment extends Fragment {
    ArrayList<Goal> goals = new ArrayList<>();
    private Button addGoal;
    private EditText newGoal;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View goalsView = inflater.inflate(R.layout.fragment_goals, container, false);

        //Goal recyclerview adapter and layout manager
        final RecyclerView recyclerGoal = goalsView.findViewById(R.id.recyclerGoal);
        final recyclerGoalAdapter gAdapter = new recyclerGoalAdapter(goals,getActivity());
        LinearLayoutManager gLayoutManager = new LinearLayoutManager(getActivity());
        recyclerGoal.setLayoutManager(gLayoutManager);
        recyclerGoal.setItemAnimator(new DefaultItemAnimator());
        recyclerGoal.setAdapter(gAdapter);

        addGoal = (Button) goalsView.findViewById(R.id.addGoal);
        newGoal = (EditText) goalsView.findViewById(R.id.goalText);
        //Adding new goal
        addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = newGoal.getText().toString();
                //If no input
                if(input.length()==0){
                    Toast.makeText(getActivity(),"Please enter a goal",Toast.LENGTH_SHORT).show();
                    return;
                    //If input exceeds 60 characters
                }else if (input.length()>60){
                    Toast.makeText(getActivity(),"Goal cannot exceed 60 letters",Toast.LENGTH_SHORT).show();
                    return;
                }
                Goal goal = new Goal();
                goal.setGoal(input);
                goals.add(goal);
                saveGoals(); //Save list of goals to shared prefs
                //Update activity
                gAdapter.notifyDataSetChanged();
                showNewEntry(recyclerGoal,goals);
                newGoal.setText(""); //Reset input field
            }
        });

        return goalsView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadGoals(); //Load list of goals from shared prefs
    }

    private void showNewEntry(RecyclerView rv, ArrayList data){
        rv.scrollToPosition(data.size() - 1);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rv.getWindowToken(), 0);
    }
    //Save list of goals to shared prefs
    private void saveGoals(){
        sharedPreferences = getActivity().getSharedPreferences("shared preferences",getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(goals);
        editor.putString("Goal List", json);
        editor.apply();
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
}
