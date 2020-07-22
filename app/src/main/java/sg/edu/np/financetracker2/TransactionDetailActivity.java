package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

        //Used to set back button actionbar
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData();
        //get item position in list from home fragment/transaction history fragment
        final int position =  getIntent().getIntExtra("position",-1);
        //retrieve transaction history object from history list
        final transactionHistoryItem obj = historyList.get(position);

        final ImageView imageView = findViewById(R.id.imageView3);
        TextView categoryDetails = findViewById(R.id.categoryDetails);
        TextView incomeExpenseCategory = findViewById(R.id.textView7);
        TextView money = findViewById(R.id.textView10);
        TextView date = findViewById(R.id.textView8);
        TextView note = findViewById(R.id.textView9);
        FloatingActionButton editDetails = findViewById(R.id.editDetails);

        //Formatting price for displaying in activity page
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
        //Display text and images in activity page
        incomeExpenseCategory.setText(incomeExpense);
        money.setText(price);
        imageView.setImageResource(obj.getmImageResource());
        categoryDetails.setText(obj.getmLine1());
        date.setText(obj.getmDate());
        note.setText(obj.getmLine2());


        //when edit button is clicked
        //item position is sent to edit transaction activity to get obj from history list
        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionDetailActivity.this,EditTransactionActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
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
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString("task list", json);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //set the delete action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);

        MenuItem deleteItem = menu.findItem(R.id.deleteItem);
        deleteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //onclick alert dialog appear prompting for deletion of item
                AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetailActivity.this);
                builder.setTitle("Delete Item");
                builder.setMessage("Are you sure you want to Delete this item?");
                //Clear Data
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        loadData();
                        //get obj position from history list
                        final int position =  getIntent().getIntExtra("position",-1);
                        //retrieve balance
                        Double balanceAmount = getBalance();
                        transactionHistoryItem obj = historyList.get(position);
                        String price = obj.getmPrice().replace("SGD","");
                        if (obj.getmPrice().contains("+")){
                            price = price.replace("+","");
                            //removing item price from balance
                            balanceAmount = (balanceAmount-Double.parseDouble(price));
                        }
                        else{
                            price = price.replace("-","");
                            //removing item price from balance
                            balanceAmount = (balanceAmount+Double.parseDouble(price));
                        }
                        //update balance
                        updateBalance(balanceAmount);

                        //delete item from list
                        historyList.remove(position);
                        //update list
                        saveData();

                        Intent deleteItem = new Intent(TransactionDetailActivity.this, MainActivity.class);
                        startActivity(deleteItem);
                        finish();
                        //Notification item has been deleted
                        Toast.makeText(TransactionDetailActivity.this, "Item has been deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                //Cancel back to MainActivity page
                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });


        return true;

    }
    // read balance.txt file and get current balance
    private Double getBalance(){
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = getApplicationContext().openFileInput("balance.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while((data = reader.readLine()) != null){
                stringBuffer.append(data);
            }
            inputStream.close();
        }catch(Exception e){
            e.printStackTrace();
            Log.v(TAG,"File not found");

        }
        return Double.parseDouble(stringBuffer.toString());
    }
    private void updateBalance(Double newBal){
        String newData = newBal.toString();
        writeToFile(newData, getApplicationContext());
    }
    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("balance.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.v(TAG, "Updated Balance!");
        }
        catch (IOException e) {
            Log.e(TAG, "Exception! File write failed: " + e.toString());
        }
    }

    protected void onStop(){
        super.onStop();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
