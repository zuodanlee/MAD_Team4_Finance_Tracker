package sg.edu.np.financetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class SettingActivity extends AppCompatActivity {
    private Switch mySwitch;
    sharedPref sharedPref;
    final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NightMode
        sharedPref = new sharedPref(this);
        if(sharedPref.loadNightMode()){
            setTheme(R.style.darkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mySwitch=(Switch)findViewById(R.id.mySwitch);
        if(sharedPref.loadNightMode()){
            mySwitch.setChecked(true);
        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPref.setNightModeState(true);
                    restartApp();
                }
                else{
                    sharedPref.setNightModeState(false);
                    restartApp();
                }
            }
        });

        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.home:
                        Intent intent2 = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.report:
                        Intent intent3 = new Intent(SettingActivity.this, ReportActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.history:
                        Intent intent4 = new Intent(SettingActivity.this, TransactionHistoryActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.goals:
                        Intent intent5 = new Intent(SettingActivity.this, GoalsActivity.class);
                        startActivity(intent5);
                        break;
                }

                return false;
            }
        });


        //Clear Data(Not completed)
        Button clearData = (Button)findViewById(R.id.clearButton);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Cleat Data");
                builder.setMessage("Are you sure you want to clear you data?");
                //Clear Data
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Log.v(TAG,"Data Cleared.");
                        //clear transaction item
                        clearTransactionHistoryItem();
                        ///updatebal
                        double balanceAmount = getBalance();
                        Log.v(TAG, "Balance: " + balanceAmount);
                        balanceAmount = 0;
                        updateBalance(balanceAmount);
                        Intent deductBal = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(deductBal);
                        finish();
                        //Notification Data has been cleared
                        Toast.makeText(getApplicationContext(), "Data has been cleared", Toast.LENGTH_SHORT).show();
                    }
                });
                //Cancel back to setting page
                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });






    }
    public void restartApp(){
        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
        startActivity(intent);
        finish();
    }
    private void clearTransactionHistoryItem(){
        SharedPreferences preferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        preferences.edit().clear().commit();
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
