package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReceiveActivity extends AppCompatActivity {
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";

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
        /*
        Button testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getBal = getIntent();
                double balanceAmount = getBal.getDoubleExtra("balanceAmount", 0);
                Log.v(TAG, "Received data : " + balanceAmount);
                balanceAmount += 20;
                Intent addBal = new Intent(ReceiveActivity.this, MainActivity.class);
                addBal.putExtra("balanceAmount", balanceAmount);
                Log.v(TAG, "Sending data : " + balanceAmount);
                startActivity(addBal);
                finish();
            }
        });

         */
        TextView balAmt = (TextView)findViewById(R.id.addBalanceAmt);
        balAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Calculator opening");
            }
        });
    }
}
