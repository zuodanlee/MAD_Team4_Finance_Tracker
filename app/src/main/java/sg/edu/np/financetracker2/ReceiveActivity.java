package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ReceiveActivity extends AppCompatActivity {

    final String TAG = "FinanceTracker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

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
    }
}
