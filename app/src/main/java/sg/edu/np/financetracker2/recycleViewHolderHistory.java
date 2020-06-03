package sg.edu.np.financetracker2;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class recycleViewHolderHistory extends RecyclerView.ViewHolder{
    ImageView mImageView;
    TextView mLine1;
    TextView mLine2;
    TextView mDate;
    TextView mPrice;
    public recycleViewHolderHistory(View itemView){
        super(itemView);
        mImageView = itemView.findViewById(R.id.ImageView);
        mLine1 = itemView.findViewById(R.id.line1);
        mLine2 = itemView.findViewById(R.id.line2);
        mDate = itemView.findViewById(R.id.date);
        mPrice = itemView.findViewById(R.id.price);
    }


}


