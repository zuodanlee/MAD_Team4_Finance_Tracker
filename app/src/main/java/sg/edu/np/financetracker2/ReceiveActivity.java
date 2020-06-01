package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ReceiveActivity extends AppCompatActivity {
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    ArrayList<String> categoryList = new ArrayList<>();
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
        setContentView(R.layout.activity_receive);


        Button addBal = (Button)findViewById(R.id.saveButton);
        final EditText etAddAmt = findViewById(R.id.addBalanceAmt);
        final EditText categoryEditText = findViewById(R.id.categoryEditText);
        addBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Adding to balance");
                double balanceAmount;
                Double spentAmt;
                try {
                    balanceAmount = getBalance();
                    spentAmt = Double.parseDouble(etAddAmt.getText().toString());

                    //if categoryEditext is empty it will be uncategorized
                    if (categoryEditText.length() == 0 ){
                        categoryEditText.setText("Uncategorized");
                    }

                    //Validation
                    if (spentAmt== 0 | spentAmt<0){
                        //Notification to enter a price
                        Toast.makeText(getApplicationContext(), "Please enter price", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.v(TAG, "Balance: " + balanceAmount);
                        balanceAmount += spentAmt;
                        updateBalance(balanceAmount);
                        Intent addBal = new Intent(ReceiveActivity.this, MainActivity.class);
                        startActivity(addBal);
                        finish();
                    }
                }
                catch (Exception e)
                {
                    //Notification to enter a price
                    Toast.makeText(getApplicationContext(), "Invalid Price, Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Receive category from adapter
        Intent i = getIntent();
        String category = i.getStringExtra("Category");
        categoryEditText.setText(category);

        //Back to Main Activity button
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Back to Main Activity");
                Intent backToMain = new Intent(ReceiveActivity.this, MainActivity.class);
                startActivity(backToMain);
                finish();
            }
        });

        //RecycerViewCategory
        final RecyclerView recyclerViewCustom = findViewById(R.id.catRecycleviewButton);
        final recycleViewAdaptorCategory mAdaptor = new recycleViewAdaptorCategory(categoryList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());
        InitData();

    }
    public void InitData(){
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
}
