package sg.edu.np.financetracker2;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class recyclerGoalHolder extends RecyclerView.ViewHolder {
    TextView txt;
    ImageButton button;
    TextView amt;
    ImageView cat;
    TextView deadline;

    public recyclerGoalHolder(View itemView){
        super(itemView);
        txt = itemView.findViewById(R.id.goalText);
        button = itemView.findViewById(R.id.deleteGoal);
        amt = itemView.findViewById(R.id.goalAmt);
        cat = itemView.findViewById(R.id.goalCat);
        deadline = itemView.findViewById(R.id.deadline);
    }
}
