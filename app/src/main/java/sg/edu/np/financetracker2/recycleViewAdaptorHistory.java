package sg.edu.np.financetracker2;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycleViewAdaptorHistory extends RecyclerView.Adapter<recycleViewHolderHistory> {
    ArrayList<transactionHistoryItem> data;
    final String TAG = "Adaptor";
    public ImageView mImageView;
    public TextView mLine1;
    public TextView mLine2;
    public TextView mDate;
    public TextView mPrice;


    public recycleViewAdaptorHistory(ArrayList<transactionHistoryItem> input) {
        data = input;
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
}

