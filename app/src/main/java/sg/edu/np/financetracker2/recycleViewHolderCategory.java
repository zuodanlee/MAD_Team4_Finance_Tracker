package sg.edu.np.financetracker2;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

public class recycleViewHolderCategory extends RecyclerView.ViewHolder implements View.OnClickListener {
    Button catButton;
    OnCategoryListener onCategoryListener;

    public recycleViewHolderCategory(View itemView, OnCategoryListener onCategoryListener){
        super(itemView);
        catButton = itemView.findViewById(R.id.categoryButton);
        this.onCategoryListener = onCategoryListener;

        catButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onCategoryListener.onCategoryClick(getAdapterPosition());
    }

    public interface OnCategoryListener {
        void onCategoryClick(int position);
    }
}
