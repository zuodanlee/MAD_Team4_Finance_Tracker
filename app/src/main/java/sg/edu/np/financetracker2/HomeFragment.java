package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements recycleViewAdaptorHistory.onHistoryListener{
    double balanceAmount;
    final String TAG = "FinanceTracker";
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View homeView = inflater.inflate(R.layout.fragment_home, container, false);

        final Button buttonSpend = (Button) homeView.findViewById(R.id.buttonSpend);
        final Button buttonReceive = (Button) homeView.findViewById(R.id.buttonReceive);
        final Button seeAllTransactionButton = (Button) homeView.findViewById(R.id.seeAllTransactionButton);

        buttonSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent spendPage = new Intent(getActivity(), SpendActivity.class);
                startActivity(spendPage);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent receivePage = new Intent(getActivity(), ReceiveActivity.class);
                startActivity(receivePage);
                getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

        //seeAllTransactionButton
        seeAllTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottomNavigationView);
                bottomNavigationView.getMenu().findItem(R.id.history).setChecked(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TransactionHistoryFragment()).commit();
            }
        });
        Log.d(TAG, "onCreateView: "+historyList.size());
        //RecycleViewHistory
        final RecyclerView recyclerViewCustom = homeView.findViewById(R.id.rvHistory);
        recyclerViewCustom.setHasFixedSize(true);
        final recycleViewAdaptorHistory mAdaptor = new recycleViewAdaptorHistory(historyList,this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());

        return homeView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading history list data
        loadData();

        if (balanceExists() == false){
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().getApplicationContext().openFileOutput("balance.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write("0.00");
                outputStreamWriter.close();
                Log.v(TAG, "Initiated Balance!");
            }
            catch (IOException e) {
                Log.e(TAG, "Exception! File write failed: " + e.toString());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final TextView balance = (TextView) getActivity().findViewById(R.id.balanceAmount);
        //Intent getBal = getIntent();
        //balanceAmount = getBal.getDoubleExtra("balanceAmount", 0);
        balanceAmount = getBalance();

        Log.v(TAG, "Balance: " + balanceAmount);
        Log.v(TAG, "Displaying balance...");
        Double displayAmount = Math.abs(balanceAmount);
        String displayString;

        if (balanceAmount >= 0){
            DecimalFormat df = new DecimalFormat("0.00");
            displayString = "$" + df.format(displayAmount);
        }
        else{
            DecimalFormat df = new DecimalFormat("0.00");
            displayString = "-$" + df.format(displayAmount);
        }
        balance.setText(displayString);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", getActivity().MODE_PRIVATE);
        boolean allowRefresh = sharedPreferences.getBoolean("allowRefresh", false);

        if (allowRefresh)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("allowRefresh", false);
            editor.apply();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(historyList);
        editor.putString("task list", json);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list",null);
        Type type = new TypeToken<ArrayList<transactionHistoryItem>>(){}.getType();
        historyList =  gson.fromJson(json,type);

        if(historyList == null){
            historyList = new ArrayList<>();
        }
    }


    // read balance.txt file and get current balance
    private Double getBalance(){
        String data;
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
        }
        return Double.parseDouble(stringBuffer.toString());
    }

    public boolean balanceExists() {
        File file = getActivity().getFileStreamPath("balance.txt");
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    //on transaction item click in recyclerview
    @Override
    public void onHistoryClick(int position) {
        Intent intent = new Intent(getActivity(),TransactionDetailActivity.class);
        //sent item position in recyclerview to transaction detail activity to retrieve object from history list
        intent.putExtra("position",position);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }


}