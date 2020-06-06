package sg.edu.np.financetracker2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    sharedPref sharedPref;
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
    final String TAG = "Report";
    ArrayList<Double> incomeList = new ArrayList<>();
    ArrayList<Double> expensesList = new ArrayList<>();
    AnyChartView anyChartViewI;
    AnyChartView anyChartViewE;
    String[] categoryList = {"Food","Uncategorized","Clothing","Utilities","Transport","Entertainment"};
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
        setContentView(R.layout.activity_report);

        anyChartViewI =findViewById(R.id.any_chart_view);
        anyChartViewE = findViewById(R.id.any_chart_view_2);

        //income
        double foodIPrice =0.00;
        double uncategorizedIPrice =0.00;
        double clothingIPrice =0.00;
        double utilitiesIPrice =0.00;
        double transportIPrice =0.00;
        double entertainmentIPrice =0.00;

        //expenses
        double foodEPrice =0.00;
        double uncategorizedEPrice =0.00;
        double clothingEPrice =0.00;
        double utilitiesEPrice =0.00;
        double transportEPrice =0.00;
        double entertainmentEPrice =0.00;

        loadData();
        Log.v(TAG,String.valueOf(historyList.size()));
        for (int i = 0; i<(historyList.size());i++){
            transactionHistoryItem pieObj = historyList.get(i);
            String mPrice= pieObj.getmPrice().replaceAll("SGD","");
            String category = pieObj.getmLine1();
            //Income
            if(mPrice.contains("+")){
                double incomePrice = Math.abs(Double.valueOf(mPrice));
                if(category.matches("Food")){
                    foodIPrice+=incomePrice;
                }
                else if(category.matches("Uncategorized")){
                    uncategorizedIPrice+=incomePrice;
                }
                else if(category.matches("Clothing")){
                    clothingIPrice+=incomePrice;
                }
                else if(category.matches("Utilities")){
                    utilitiesIPrice+=incomePrice;
                }
                else if(category.matches("Transport")){
                    transportIPrice+=incomePrice;
                }
                else{
                    entertainmentIPrice+=incomePrice;
                }
            }
            //Expenses
            else{
                double expensesPrice = Math.abs(Double.valueOf(mPrice));
                if(category.matches("Food")){
                    foodEPrice+=expensesPrice;
                }
                else if(category.matches("Uncategorized")){
                    uncategorizedEPrice+=expensesPrice;
                }
                else if(category.matches("Clothing")){
                    clothingEPrice+=expensesPrice;
                }
                else if(category.matches("Utilities")){
                    utilitiesEPrice+=expensesPrice;
                }
                else if(category.matches("Transport")){
                    transportEPrice+=expensesPrice;
                }
                else{
                    entertainmentEPrice+=expensesPrice;
                }
            }
        }
        incomeList.add(foodIPrice);
        incomeList.add(uncategorizedIPrice);
        incomeList.add(clothingIPrice);
        incomeList.add(utilitiesEPrice);
        incomeList.add(transportIPrice);
        incomeList.add(entertainmentIPrice);
        expensesList.add(foodEPrice);
        expensesList.add(uncategorizedEPrice);
        expensesList.add(clothingEPrice);
        expensesList.add(utilitiesEPrice);
        expensesList.add(transportEPrice);
        expensesList.add(entertainmentEPrice);
        //Log.v(TAG,"FDFDFDSFDFDS: " + incomeList.get(0));

        //settting pie chart
        setUpPieChart();


        //Bottom Navigation View
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.home:
                        Intent intent2 = new Intent(ReportActivity.this, MainActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.settings:
                        Intent intent3 = new Intent(ReportActivity.this, SettingActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.history:
                        Intent intent4 = new Intent(ReportActivity.this, TransactionHistoryActivity.class);
                        startActivity(intent4);
                        break;
                    case R.id.goals:
                        Intent intent5 = new Intent(ReportActivity.this, GoalsActivity.class);
                        startActivity(intent5);
                        break;
                }

                return false;
            }
        });
    }
    public void setUpPieChart(){
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        for(int i = 0; i<categoryList.length; i++){
            dataEntries.add(new ValueDataEntry(categoryList[i],incomeList.get(i)));
        }

        pie.data(dataEntries);
        anyChartViewI.setChart(pie);
    }


    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list",null);
        Type type = new TypeToken<ArrayList<transactionHistoryItem>>(){}.getType();
        historyList =  gson.fromJson(json,type);

        if(historyList == null){
            historyList = new ArrayList<>();
        }
    }
}



