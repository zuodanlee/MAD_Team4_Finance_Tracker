package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

public class TransactionDetailActivity extends AppCompatActivity {
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NightMode
        sharedPref = new sharedPref(this);
        if (sharedPref.loadNightMode() == true) {
            setTheme(R.style.darkTheme);
        } else {
            setTheme(R.style.AppTheme);
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        //retrieve transaction history object
        transactionHistoryItem obj =  (transactionHistoryItem) getIntent().getSerializableExtra("historyObj");

        ImageView imageView = findViewById(R.id.imageView3);
        TextView categoryDetails = findViewById(R.id.categoryDetails);
        TextView incomeExpenseCategory = findViewById(R.id.textView7);
        TextView money = findViewById(R.id.textView10);
        TextView date = findViewById(R.id.textView8);
        TextView note = findViewById(R.id.textView9);
        FloatingActionButton editDetails = findViewById(R.id.editDetails);

        //set text and image
        String price = obj.getmPrice().replace("SGD","");
        String incomeExpense = null;
        if(obj.getmPrice().contains("+")) {
            incomeExpense = "Income";
            price = price.replace("+","");
        }
        else{
            incomeExpense = "Expenses";
            price = price.replace("-","");
        }
        incomeExpenseCategory.setText(incomeExpense);
        money.setText(price);
        imageView.setImageResource(obj.getmImageResource());
        categoryDetails.setText(obj.getmLine1());
        date.setText(obj.getmDate());
        note.setText(obj.getmLine2());

        //when edit button is clicked
        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
