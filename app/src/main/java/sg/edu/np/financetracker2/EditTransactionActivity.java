package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EditTransactionActivity extends AppCompatActivity implements recycleViewHolderCategory.OnCategoryListener{
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    int image;
    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
    private String notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NightMode
        sharedPref = new sharedPref(this);
        if(sharedPref.loadNightMode()==true){
            setTheme(R.style.darkTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        //Set actionbar back button
        getSupportActionBar().setTitle("Edit Transaction");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button confirmButton = findViewById(R.id.editSave);
        final EditText etEditAmt = findViewById(R.id.editBalanceAmt);
        final TextView categoryTextView = findViewById(R.id.editTvCategory);
        final EditText noteEditText = findViewById(R.id.editEtNotes);

        loadData();
        //editpage
        //get position from home fragment/transaction history fragment
        final int position = getIntent().getIntExtra("position",-1);
        //retrieve transaction history object from history list
        final transactionHistoryItem obj = historyList.get(position);


        //formatting price for display in activity page
        String price = obj.getmPrice().replace("SGD","");
        if(obj.getmPrice().contains("+")) {
            price = price.replace("+","");
        }
        else{
            price = price.replace("-","");
        }
        //Display price, notes and category in activity page
        etEditAmt.setText(price);
        noteEditText.setText(obj.getmLine2());
        categoryTextView.setText(obj.getmLine1());

        //onclick update transaction details
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double balanceAmount;
                Double editAmt;
                Double diffAmt;
                try {
                    //retrieve total balance
                    balanceAmount = getBalance();
                    //retrieve price user entered
                    editAmt = Double.parseDouble(etEditAmt.getText().toString());

                    //Validation
                    //if categoryTextView is empty it will be uncategorized
                    if (categoryTextView.length() == 0) {
                        //Notification to
                        Toast.makeText(getApplicationContext(), "Please choose a category", Toast.LENGTH_SHORT).show();
                    } else if (editAmt == 0 | editAmt < 0) {
                        //Notification to enter a price
                        Toast.makeText(getApplicationContext(), "Please enter price", Toast.LENGTH_SHORT).show();
                    } else {
                        //RecordPrice
                        //format price
                        String price1;
                        if (obj.getmPrice().contains("+")){
                            price1 = "+" + editAmt + " SGD";
                        }
                        else{
                            price1 = "-" + editAmt + " SGD";
                        }

                        //get old price from old transaction object that is selected
                        String oldPrice = obj.getmPrice().replace("SGD","");
                        if(obj.getmPrice().contains("+")) {
                            oldPrice = oldPrice.replace("+","");
                            //find diff in amount between old price and new price
                            diffAmt = Math.abs(Double.parseDouble(oldPrice)-editAmt);
                            if(Double.parseDouble(oldPrice)>editAmt){
                                balanceAmount -= diffAmt;
                            }
                            else{
                                balanceAmount += diffAmt;
                            }
                        }
                        else{
                            oldPrice = oldPrice.replace("-","");
                            //find diff in amount
                            diffAmt = Math.abs(Double.parseDouble(oldPrice)-editAmt);
                            if(Double.parseDouble(oldPrice)>editAmt){
                                balanceAmount += diffAmt;
                            }
                            else{
                                balanceAmount -= diffAmt;
                            }
                        }

                        //UpdateBalance to Transaction Detail page
                        updateBalance(balanceAmount);

                        //retrieve category that user input
                        String category = categoryTextView.getText().toString();
                        //set image category
                        InitImage(category);
                        //retrieve not that user input
                        notes = noteEditText.getText().toString();
                        //if note is empty, note is default to show category
                        if(notes.length() == 0){
                            notes = category;
                        }
                        //create new transaction history object
                        //replace old object to new object in history list
                        //list updated
                        transactionHistoryItem hObject = new transactionHistoryItem(image, category, notes, obj.getmDate(), price1);
                        historyList.set(position,hObject);
                        saveData();

                        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("allowRefresh", true);
                        editor.apply();

                        Intent editBal = new Intent(EditTransactionActivity.this, TransactionDetailActivity.class);
                        editBal.putExtra("position", position);
                        startActivity(editBal);
                        finish();
                    }
                } catch (Exception e) {
                    //Notification to enter a price
                    Toast.makeText(getApplicationContext(), "Invalid Price, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //Back to TransactionDetail Activity button
        ImageButton backButton = (ImageButton) findViewById(R.id.editBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Back to TransactionDetails Activity");
                Intent backToMain = new Intent(EditTransactionActivity.this, TransactionDetailActivity.class);
                backToMain.putExtra("position",position);
                startActivity(backToMain);
                finish();
            }
        });

        //RecyclerViewCategory
        final RecyclerView recyclerViewCustom = findViewById(R.id.editRvCategory);
        final recycleViewAdaptorCategory mAdaptor = new recycleViewAdaptorCategory(categoryList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());
        InitData();
    }

    private void saveData(){
            SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(historyList);
            editor.putString("task list", json);
            editor.apply();
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
    public void InitData() {
        String uncategorized = "Uncategorized";
        String food = "Food";
        String clothing = "Clothing";
        String utilities = "Utilities";
        String transport = "Transport";
        String entertainment = "Entertainment";
        categoryList.add(uncategorized);
        categoryList.add(food);
        categoryList.add(clothing);
        categoryList.add(utilities);
        categoryList.add(transport);
        categoryList.add(entertainment);
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

    @Override
    public void onCategoryClick(int position) {
        String category = categoryList.get(position);
        TextView tvCategory = findViewById(R.id.editTvCategory);
        tvCategory.setText(category);
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
