package sg.edu.np.financetracker2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class recycleViewAdaptorHistory extends RecyclerView.Adapter<recycleViewHolderHistory> implements Filterable {
    ArrayList<transactionHistoryItem> data;
    ArrayList<transactionHistoryItem> dataListFull;
    final String TAG = "Adaptor";
    public ImageView mImageView;
    public TextView mLine1;
    public TextView mLine2;
    public TextView mDate;
    public TextView mPrice;



    public recycleViewAdaptorHistory(ArrayList<transactionHistoryItem> input) {
        data = input;
        dataListFull = new ArrayList<>(input);
    }


    @Override
    public recycleViewHolderHistory onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_history, parent, false);
        return new recycleViewHolderHistory(item);
    }

    @Override
    public void onBindViewHolder(recycleViewHolderHistory holder, final int position) {
        final transactionHistoryItem s = data.get(position);
        holder.mImageView.setImageResource(s.getmImageResource());
        holder.mLine1.setText(s.getmLine1());
        holder.mLine2.setText(s.getmLine2());
        holder.mDate.setText(s.getmDate());
        holder.mPrice.setText(s.getmPrice());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<transactionHistoryItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(dataListFull);
            }
            else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (transactionHistoryItem item : dataListFull){
                    if (item.getmLine1().toLowerCase().contains(filterPattern) || item.getmLine2().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data.clear();
            data.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}


