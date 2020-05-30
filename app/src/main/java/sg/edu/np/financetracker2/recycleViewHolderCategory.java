package sg.edu.np.financetracker2;

import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class recycleViewHolderCategory extends RecyclerView.ViewHolder {
    Button catButton;
    public recycleViewHolderCategory(View itemView){
        super(itemView);
        catButton = itemView.findViewById(R.id.categoryButton);
    }
}
