package sg.edu.np.financetracker2;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class recyclerGoalHolder extends RecyclerView.ViewHolder {
    TextView txt;
    ImageButton button;

    public recyclerGoalHolder(View itemView){
        super(itemView);
        txt = itemView.findViewById(R.id.goalText);
        button = itemView.findViewById(R.id.deleteGoal);
    }
}
