package sg.edu.np.financetracker2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
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
        final Goal goal = goals.get(position);
        holder.txt.setText(goal.getGoal());
        holder.amt.setText(goal.getGoalAmount() + " SGD");
        holder.cat.setImageResource(goal.getmImageResource());
        holder.deadline.setText("By: " + goal.getDeadline());
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
                        //update total amount
                        double goalTotal = getGoalTotal();
                        goalTotal -= Double.parseDouble(goal.getGoalAmount());
                        updateGoalTotal(goalTotal);
                        TextView totalAmt = (TextView) ((Activity)context).findViewById(R.id.goalTotal);
                        Double displayAmount = Math.abs(goalTotal);
                        String displayString;
                        DecimalFormat df = new DecimalFormat("0.00");
                        displayString = "$" + df.format(displayAmount);
                        totalAmt.setText(displayString);
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

    //Get goal total
    private Double getGoalTotal(){
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = context.getApplicationContext().openFileInput("goalTotal.txt");
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

    //Update goal total
    private void updateGoalTotal(Double newBal){
        String newData = newBal.toString();
        writeToFile(newData, context.getApplicationContext());
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("goalTotal.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
        }
    }
}
