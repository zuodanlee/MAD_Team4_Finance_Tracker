package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SpendActivity extends AppCompatActivity {

    final String TAG = "FinanceTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spend);

        Button confirmButton = findViewById(R.id.confirmSpend);
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
