package sg.edu.np.financetracker2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
    final String TAG = "Report";
    ArrayList<Double> incomeList = new ArrayList<>();
    ArrayList<Double> expensesList = new ArrayList<>();
    String[] categoryList = {"Food","Uncategorized","Clothing","Utilities","Transport","Entertainment"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View reportView = inflater.inflate(R.layout.fragment_report, container, false);

        TabHost tabHost = (TabHost) reportView.findViewById(R.id.tabHost);
        tabHost.setup();

        //tab1
        TabHost.TabSpec spec = tabHost.newTabSpec("Income");
        spec.setContent(R.id.income);
        spec.setIndicator("Income");
        tabHost.addTab(spec);

        //tab2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("Expenses");
        spec2.setContent(R.id.expenses);
        spec2.setIndicator("Expenses");
        tabHost.addTab(spec2);

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
        incomeList.add(utilitiesIPrice);
        incomeList.add(transportIPrice);
        incomeList.add(entertainmentIPrice);
        expensesList.add(foodEPrice);
        expensesList.add(uncategorizedEPrice);
        expensesList.add(clothingEPrice);
        expensesList.add(utilitiesEPrice);
        expensesList.add(transportEPrice);
        expensesList.add(entertainmentEPrice);

        //setting pie charts
        setUpPieChart(reportView);

        return reportView;
    }

    public void setUpPieChart(View reportView){
        //set up income chart
        AnyChartView anyChartViewI = reportView.findViewById(R.id.any_chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartViewI);

        Pie pie = AnyChart.pie();

        List<DataEntry> dataEntries = new ArrayList<>();

        for(int i = 0; i<categoryList.length; i++){
            Log.v(TAG,String.valueOf("Income list: " + incomeList.get(i)));
            dataEntries.add(new ValueDataEntry(categoryList[i],incomeList.get(i)));
        }

        pie.data(dataEntries);
        anyChartViewI.setChart(pie);
        Log.v(TAG, "Set Income Chart!");

        //set up expenses chart
        AnyChartView anyChartViewE = reportView.findViewById(R.id.any_chart_view_2);
        APIlib.getInstance().setActiveAnyChartView(anyChartViewE);

        Pie pie1 = AnyChart.pie();

        List<DataEntry> dataEntries2 = new ArrayList<>();

        for(int i = 0; i<categoryList.length; i++){
            Log.v(TAG,String.valueOf("Income list: " + incomeList.get(i)));
            dataEntries2.add(new ValueDataEntry(categoryList[i],expensesList.get(i)));
        }

        pie1.data(dataEntries2);
        anyChartViewE.setChart(pie1);
        Log.v(TAG, "Set Expense Chart!");
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
}
