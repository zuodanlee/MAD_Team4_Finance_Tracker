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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SpendActivity extends AppCompatActivity implements recycleViewHolderCategory.OnCategoryListener{
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    int image;
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
        setContentView(R.layout.activity_spend);

        Button confirmButton = findViewById(R.id.spendBSave);
        final EditText etSpendAmt = findViewById(R.id.deductBalanceAmt);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent getBal = getIntent();
                //double balanceAmount = getBal.getDoubleExtra("balanceAmount", 0);
                //Log.v(TAG, "Received data : " + balanceAmount);
                double balanceAmount = getBalance();
                Double spentAmt = Double.parseDouble(etSpendAmt.getText().toString());

                Log.v(TAG, "Balance: " + balanceAmount);
                balanceAmount -= spentAmt;
                updateBalance(balanceAmount);
                Intent deductBal = new Intent(SpendActivity.this, MainActivity.class);
                //deductBal.putExtra("balanceAmount", balanceAmount);
                //Log.v(TAG, "Sending data : " + balanceAmount);
                startActivity(deductBal);
                finish();
            }
        });

        //RecycerViewCategory
        final RecyclerView recyclerViewCustom = findViewById(R.id.spendRvCategory);
        final recycleViewAdaptorCategory mAdaptor = new recycleViewAdaptorCategory(categoryList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());
        InitData();
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
        TextView tvCategory = findViewById(R.id.spendTvCategory);
        tvCategory.setText(category);
    }
}
