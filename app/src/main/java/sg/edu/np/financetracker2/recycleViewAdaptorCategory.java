package sg.edu.np.financetracker2;

import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
            /*
            //Set onCLickListener here
            holder.catButton.setOnClickListener(new View.OnClickListener() {
                //Not complete
                @Override
                public void onClick(View v) {
                    //start new intent
                    Log.v(TAG,s);
                    Intent i = new Intent(v.getContext(), ReceiveActivity.class);
                    i.putExtra("Category",s);
                    v.getContext().startActivity(i); //starting new activity
                }
            });

             */

        }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
