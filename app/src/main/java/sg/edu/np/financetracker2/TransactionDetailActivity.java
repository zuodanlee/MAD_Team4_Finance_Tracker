package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TransactionDetailActivity extends AppCompatActivity {
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
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

        loadData();
        //retrieve transaction history object from history list
        //get position from home fragment/transaction history fragment
        final int position =  getIntent().getIntExtra("position",-1);
        final transactionHistoryItem obj = historyList.get(position);

        final ImageView imageView = findViewById(R.id.imageView3);
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
                Intent intent = new Intent(TransactionDetailActivity.this,EditTransactionActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });


    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<transactionHistoryItem>>() {
        }.getType();
        historyList = gson.fromJson(json, type);

        if (historyList == null) {
            historyList = new ArrayList<>();
        }
    }
    protected void onStop(){
        super.onStop();
    }
}
