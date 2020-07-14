package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SettingFragment extends Fragment {
    private Switch mySwitch;
    sharedPref sharedPref;
    final String TAG = "SettingActivity";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View settingView = inflater.inflate(R.layout.fragment_setting, container, false);

        sharedPref = new sharedPref(getActivity());
        mySwitch=(Switch)settingView.findViewById(R.id.mySwitch);
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

        //Clear Data(Not completed)
        Button clearData = (Button)settingView.findViewById(R.id.clearButton);
        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Clear Data");
                builder.setMessage("Are you sure you want to clear your data?");
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
                        Intent deductBal = new Intent(getActivity(), MainActivity.class);
                        startActivity(deductBal);
                        getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                        getActivity().finish();
                        //Notification Data has been cleared
                        Toast.makeText(getActivity(), "Data has been cleared", Toast.LENGTH_SHORT).show();
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

        return settingView;
    }

    public void restartApp(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("default page", "settings");
        editor.apply();
        Intent intent = new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
    private void clearTransactionHistoryItem(){
        SharedPreferences preferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }


    // read balance.txt file and get current balance
    private Double getBalance(){
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = getActivity().openFileInput("balance.txt");
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
        writeToFile(newData, getActivity());
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
