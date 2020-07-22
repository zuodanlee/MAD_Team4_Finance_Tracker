package sg.edu.np.financetracker2;

import android.widget.Button;

public class Goal {
    private String goal;
    private int mImageResource;
    private String goalAmount;
    private String deadline;
    private String category;
    private transient Button deleteGoal;

    public Goal(int imageResource, String cat, String text, String date, String amount){
        mImageResource = imageResource;
        category = cat;
        goal = text;
        deadline = date;
        goalAmount = amount;
    }

    public int getmImageResource(){
        return mImageResource;
    }
    public String getCategory(){
        return category;
    }
    public String getGoal() {
        return goal;
    }
    public String getDeadline(){
        return deadline;
    }
    public String getGoalAmount(){
        return goalAmount;
    }
}
