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

public class ReceiveActivity extends AppCompatActivity implements recycleViewHolderCategory.OnCategoryListener {
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    int image;
    private String notes;
    ArrayList<String> categoryList = new ArrayList<>();
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
        setContentView(R.layout.activity_receive);


        Button addBal = (Button) findViewById(R.id.saveButton);
        final EditText etAddAmt = findViewById(R.id.addBalanceAmt);
        final TextView categoryTextView = findViewById(R.id.categoryTextView);
        final EditText noteEditText = findViewById(R.id.notesEditText);


        //getdate
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        final String[] newDate = currentDate.split(",");

        addBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double balanceAmount;
                Double spentAmt;
                try {
                    balanceAmount = getBalance();
                    spentAmt = Double.parseDouble(etAddAmt.getText().toString());

                    //Validation
                    //if categoryTextView is empty it will be uncategorized
                    if (categoryTextView.length() == 0) {
                        //Notification to
                        Toast.makeText(getApplicationContext(), "Please choose a category", Toast.LENGTH_SHORT).show();
                    } else if (spentAmt == 0 | spentAmt < 0) {
                        //Notification to enter a price
                        Toast.makeText(getApplicationContext(), "Please enter price", Toast.LENGTH_SHORT).show();
                    } else {
                        //RecordPrice
                        String price = "+" + spentAmt + " SGD";
                        Log.v(TAG, price);

                        //UpdateBalance to main page
                        Log.v(TAG, "Balance: " + balanceAmount);
                        balanceAmount += spentAmt;
                        updateBalance(balanceAmount);
                        Intent addBal = new Intent(ReceiveActivity.this, MainActivity.class);

                        //getcategory
                        String category = categoryTextView.getText().toString();
                        InitImage(category);
                        //getnote
                        notes = noteEditText.getText().toString();
                        if(notes.length() == 0){
                            notes = category;
                        }
                        //create transactionhistoryobject
                        transactionHistoryItem hObject = new transactionHistoryItem(image, category, notes, newDate[0], price);
                        //adding object into history list
                        loadData();
                        historyList.add(0,hObject);
                        saveData();

                        startActivity(addBal);
                        finish();
                    }
                } catch (Exception e) {
                    //Notification to enter a price
                    Toast.makeText(getApplicationContext(), "Invalid Price, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Back to Main Activity button
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Back to Main Activity");
                Intent backToMain = new Intent(ReceiveActivity.this, MainActivity.class);
                startActivity(backToMain);;
                finish();
            }
        });

        //RecycerViewCategory
        final RecyclerView recyclerViewCustom = findViewById(R.id.catRecycleviewButton);
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
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list",null);
        Type type = new TypeToken<ArrayList<transactionHistoryItem>>(){}.getType();
        historyList =  gson.fromJson(json,type);

        if(historyList == null){
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
    private Double getBalance() {
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try {
            InputStream inputStream = getApplicationContext().openFileInput("balance.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((data = reader.readLine()) != null) {
                stringBuffer.append(data);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "File not found");

        }
        return Double.parseDouble(stringBuffer.toString());
    }

    private void updateBalance(Double newBal) {
        String newData = newBal.toString();
        writeToFile(newData, getApplicationContext());
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("balance.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.v(TAG, "Updated Balance!");
        } catch (IOException e) {
            Log.e(TAG, "Exception! File write failed: " + e.toString());
        }
    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryList.get(position);
        TextView tvCategory = findViewById(R.id.categoryTextView);
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
