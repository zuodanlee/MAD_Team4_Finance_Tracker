package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    double balanceAmount;
    final String TAG = "FinanceTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonSpend = (Button) findViewById(R.id.buttonSpend);
        final Button buttonReceive = (Button) findViewById(R.id.buttonReceive);

        buttonSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent spendPage = new Intent(MainActivity.this, SpendActivity.class);
                spendPage.putExtra("balanceAmount", balanceAmount);
                Log.v(TAG,"Sending data : " + balanceAmount);
                startActivity(spendPage);
                finish();
            }
        });

        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivePage = new Intent(MainActivity.this, ReceiveActivity.class);
                receivePage.putExtra("balanceAmount", balanceAmount);
                Log.v(TAG,"Sending data : " + balanceAmount);
                startActivity(receivePage);
                finish();
            }
        });
    }

    protected void onStart(){
        super.onStart();
        final TextView balance = (TextView) findViewById(R.id.balanceAmount);
        Intent getBal = getIntent();
        balanceAmount = getBal.getDoubleExtra("balanceAmount", 0);
        Log.v(TAG, "Received data : " + balanceAmount);
        Log.v(TAG, "Displaying balance...");
        balance.setText("$" + balanceAmount);
    }
}
