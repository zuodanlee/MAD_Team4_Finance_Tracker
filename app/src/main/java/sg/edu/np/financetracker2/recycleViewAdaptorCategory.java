package sg.edu.np.financetracker2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycleViewAdaptorCategory extends RecyclerView.Adapter<recycleViewHolderCategory> {
    ArrayList<String> data;
    int image;
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
            image = InitImage(s);
            holder.catButton.setImageResource(image);
        }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int InitImage(String category) {
        if (category.equals("Food")) {
            image = R.raw.food;
        } else if (category.equals("Uncategorized")) {
            image = R.raw.uncategorized;
        } else if (category.equals("Clothing")) {
            image = R.raw.clothing;
        } else if (category.equals("Utilities")) {
            image = R.raw.utilities;
        } else if (category.equals("Transport")) {
            image = R.raw.transport;
        } else {
            image = R.raw.entertainment;
        }
        return image;
    }
}
