package sg.edu.np.financetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.Math;

public class MainActivity extends AppCompatActivity {

    sharedPref sharedPref;
    double balanceAmount;
    final String TAG = "FinanceTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new sharedPref(this);
        if(sharedPref.loadNightMode()){
            setTheme(R.style.darkTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonSpend = (Button) findViewById(R.id.buttonSpend);
        final Button buttonReceive = (Button) findViewById(R.id.buttonReceive);

        buttonSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent spendPage = new Intent(MainActivity.this, SpendActivity.class);
                //spendPage.putExtra("balanceAmount", balanceAmount);
                //Log.v(TAG,"Sending data : " + balanceAmount);
                startActivity(spendPage);
                finish();
            }
        });

        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivePage = new Intent(MainActivity.this, ReceiveActivity.class);
                //receivePage.putExtra("balanceAmount", balanceAmount);
                //Log.v(TAG,"Sending data : " + balanceAmount);
                startActivity(receivePage);
                finish();
            }
        });
        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.settings:
                        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.report:
                        Intent intent2 = new Intent(MainActivity.this, ReportActivity.class);
                        startActivity(intent2);
                        break;
                }

                return false;
            }
        });

    }

    protected void onStart(){
        super.onStart();
        final TextView balance = (TextView) findViewById(R.id.balanceAmount);
        //Intent getBal = getIntent();
        //balanceAmount = getBal.getDoubleExtra("balanceAmount", 0);
        balanceAmount = getBalance();

        Log.v(TAG, "Balance: " + balanceAmount);
        Log.v(TAG, "Displaying balance...");
        Double displayAmount = Math.abs(balanceAmount);
        if (balanceAmount >= 0){
            balance.setText("$" + displayAmount);
        }
        else{
            balance.setText("-$" + displayAmount);
        }
    }

    // read balance.txt file and get current balance
    private Double getBalance(){
        String data;
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
        }
        return Double.parseDouble(stringBuffer.toString());
    }
}
