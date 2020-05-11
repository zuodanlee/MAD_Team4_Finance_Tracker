package sg.edu.np.financetracker2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSpend = (Button) findViewById(R.id.buttonSpend);
        Button buttonReceive = (Button) findViewById(R.id.buttonReceive);
        TextView balance = (TextView) findViewById(R.id.balanceAmount);

        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent spendPage = new Intent(MainActivity.this, ReceiveActivity.class);
                startActivity(spendPage);
            }
        });
    }
}
