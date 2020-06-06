package sg.edu.np.financetracker2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycleViewAdaptorCategory extends RecyclerView.Adapter<recycleViewHolderCategory> {
    ArrayList<String> data;
    final String TAG = "Adaptor";
    private recycleViewHolderCategory.OnCategoryListener mOnCategoryListener;

    public recycleViewAdaptorCategory(ArrayList<String> input, recycleViewHolderCategory.OnCategoryListener onCategoryListener) {
        data = input;
        this.mOnCategoryListener = onCategoryListener;
    }

    @NonNull
    @Override
    public recycleViewHolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_category, parent, false);
            return new recycleViewHolderCategory(item, mOnCategoryListener);
        }

    @Override
    public void onBindViewHolder(@NonNull recycleViewHolderCategory holder, final int position) {
            final String s = data.get(position);
            holder.catButton.setText(s);
        }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
