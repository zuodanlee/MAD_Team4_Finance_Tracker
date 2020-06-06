package sg.edu.np.financetracker2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class recyclerGoalAdapter extends RecyclerView.Adapter<recyclerGoalHolder> {
    SharedPreferences sharedPreferences;
    ArrayList<Goal> goals;
    private Context context;
    public recyclerGoalAdapter(ArrayList<Goal> input, Context context){
        this.goals = input;
        this.context = context;
    }
    public recyclerGoalHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_goals,parent,false);
        return new recyclerGoalHolder(item);
    }
    public void onBindViewHolder(final recyclerGoalHolder holder, final int position){
        Goal goal = goals.get(position);
        holder.txt.setText((position+1) + ". " + goal.getGoal());
        //Delete goal
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alert for confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete Goal");
                builder.setMessage("Do you want to delete \"" + holder.txt.getText().toString() + "\"?");
                builder.setCancelable(false);
                //If yes
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goals.remove(position);
                        saveGoals(); //Save list of goals to shared prefs
                        notifyDataSetChanged(); //Update activity
                    }
                });
                //If no
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    public int getItemCount(){
        return goals.size();
    }
    //Save list of goals to shared prefs
    private void saveGoals(){
        sharedPreferences = this.context.getSharedPreferences("shared preferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(goals);
        editor.putString("Goal List", json);
        editor.apply();
    }
}
