package sg.edu.np.financetracker2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerGoalAdapter extends RecyclerView.Adapter<recyclerGoalHolder> {
    ArrayList<Goal> goals;
    public recyclerGoalAdapter(ArrayList<Goal> input){
        this.goals = input;
    }
    public recyclerGoalHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_goals,parent,false);
        return new recyclerGoalHolder(item);
    }
    public void onBindViewHolder(final recyclerGoalHolder holder, final int position){
        Goal goal = goals.get(position);
        holder.txt.setText((position+1) + ". " + goal.getGoal());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete Goal");
                builder.setMessage("Do you want to delete \"" + holder.txt.getText().toString() + "\"?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goals.remove(position);
                        notifyDataSetChanged();
                    }
                });
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
}
