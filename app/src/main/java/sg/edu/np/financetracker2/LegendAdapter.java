package sg.edu.np.financetracker2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.List;

public class LegendAdapter extends ArrayAdapter<LegendItem> {

    Context context;
    int resource;
    List<LegendItem> legendItemList;

    public LegendAdapter(Context context, int resource, List<LegendItem> legendItemList) {
        super(context, resource, legendItemList);
        this.context = context;
        this.resource = resource;
        this.legendItemList = legendItemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(resource, null);

        TextView tvCategoryHeader = view.findViewById(R.id.tvCategoryHeader);
        TextView tvName = view.findViewById(R.id.tvItemName);
        TextView tvValue = view.findViewById(R.id.tvItemValue);
        TextView tvPercent = view.findViewById(R.id.tvItemPercent);

        LegendItem legendItem = legendItemList.get(position);
        DecimalFormat dfValue = new DecimalFormat("0.00");

        if (position == 0){
            tvName.setText("");
            tvValue.setText("Amount (SGD)");
            tvPercent.setText("%");
        }
        else if (position == legendItemList.size() - 1){
            tvCategoryHeader.setVisibility(View.GONE);

            tvName.setText("Total");
            tvValue.setText("$" + dfValue.format(legendItem.getValue()));
            tvPercent.setText("100%");
        }
        else{
            tvCategoryHeader.setVisibility(View.GONE);

            tvName.setText(legendItem.getName());
            tvValue.setText("$" + dfValue.format(legendItem.getValue()));
            tvPercent.setText(Math.round(legendItem.getPercent()) + "%");

            Bitmap bg = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bg);
            canvas.drawCircle(50, 50, 50, legendItem.colour);

            // set background of relative layout to drawn graphs above
            LinearLayout llItemColour = (LinearLayout) view.findViewById(R.id.llItemColour);
            llItemColour.setBackground(new BitmapDrawable(bg));
        }

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}