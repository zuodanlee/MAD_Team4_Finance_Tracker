package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ReportFragment extends Fragment {
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    int rx = 20;
    int ry = 20;
    Paint paint = new Paint();
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    String[] categoryList = {"Food","Uncategorized","Clothing","Utilities","Transport","Entertainment"};
    PieChart pieChart;
    int[] colourClassArray = new int[] {0xFFFABF96, 0xFFFF8A5B, 0xFFD97531, 0xFFEA526F, 0xFF25CED1, 0xFFFFC100};
    String reportTab;
    int reportMonth;

    // initialise total amounts for last 3 months
    double leftValueI = 0;
    double middleValueI = 0;
    double rightValueI = 0;
    double leftValueE = 0;
    double middleValueE = 0;
    double rightValueE = 0;

    // initialise income and expenses lists for the last 3 months
    private ArrayList<Double> leftIncomeList = new ArrayList<>();
    private ArrayList<Double> leftExpensesList = new ArrayList<>();
    private ArrayList<Double> middleIncomeList = new ArrayList<>();
    private ArrayList<Double> middleExpensesList = new ArrayList<>();
    private ArrayList<Double> rightIncomeList = new ArrayList<>();
    private ArrayList<Double> rightExpensesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View reportView = inflater.inflate(R.layout.fragment_report, container, false);

        sharedPref = new sharedPref(getActivity());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        reportTab = sharedPreferences.getString("reportTab", "Income");
        reportMonth = sharedPreferences.getInt("reportMonth", currentMonth);
        Button bIncome = (Button) reportView.findViewById(R.id.bIncome);
        Button bExpenses = (Button) reportView.findViewById(R.id.bExpenses);
        final TextView tvGraphHeader = (TextView) reportView.findViewById(R.id.tvGraphHeader);
        LinearLayout llOnGraphListenerContainer = (LinearLayout) reportView.findViewById(R.id.llOnGraphListenerContainer);
        View vLeftGraph = (View) reportView.findViewById(R.id.vLeftGraph);
        View vMiddleGraph = (View) reportView.findViewById(R.id.vMiddleGraph);
        View vRightGraph = (View) reportView.findViewById(R.id.vRightGraph);
        llOnGraphListenerContainer.bringToFront();
        LinearLayout llPieChartContainer = (LinearLayout) reportView.findViewById(R.id.llPieChartContainer);
        final View incomeTabIndicator = (View) reportView.findViewById(R.id.incomeTabIndicator);
        final View expensesTabIndicator = (View) reportView.findViewById(R.id.expensesTabIndicator);

        if(sharedPref.loadNightMode()){
            llPieChartContainer.setBackgroundColor(0xFF363636);
        }
        else{
            llPieChartContainer.setBackgroundColor(0xFFE8EBE9);
        }

        if (reportTab == "Expenses"){
            incomeTabIndicator.setVisibility(View.INVISIBLE);
        }
        else{
            expensesTabIndicator.setVisibility(View.INVISIBLE);
        }

        bIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("reportTab", "Income");
                reportTab = "Income";
                editor.apply();
                incomeTabIndicator.setVisibility(View.VISIBLE);
                expensesTabIndicator.setVisibility(View.INVISIBLE);
                drawGraph(reportView, leftValueI, middleValueI, rightValueI);
                tvGraphHeader.setText("Total Income Over The Last 3 Months");
                if (reportMonth == currentMonth-2){
                    setUpPieChart(reportView, leftIncomeList);
                }
                else if (reportMonth == currentMonth-1){
                    setUpPieChart(reportView, middleIncomeList);
                }
                else if (reportMonth == currentMonth){
                    setUpPieChart(reportView, rightIncomeList);
                }
                else{
                    editor.putInt("reportMonth", currentMonth);
                    setUpPieChart(reportView, leftIncomeList);
                }
            }
        });

        bExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("reportTab", "Expenses");
                reportTab = "Expenses";
                editor.apply();
                expensesTabIndicator.setVisibility(View.VISIBLE);
                incomeTabIndicator.setVisibility(View.INVISIBLE);
                drawGraph(reportView, leftValueE, middleValueE, rightValueE);
                tvGraphHeader.setText("Total Expenses Over The Last 3 Months");
                if (reportMonth == currentMonth-2){
                    setUpPieChart(reportView, leftExpensesList);
                }
                else if (reportMonth == currentMonth-1){
                    setUpPieChart(reportView, middleExpensesList);
                }
                else if (reportMonth == currentMonth){
                    setUpPieChart(reportView, rightExpensesList);
                }
                else{
                    editor.putInt("reportMonth", currentMonth);
                    setUpPieChart(reportView, leftExpensesList);
                }
            }
        });

        vLeftGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("reportMonth", currentMonth-2);
                editor.apply();
                reportMonth = currentMonth-2;
                if (reportTab.equals("Income")){
                    setUpPieChart(reportView, leftIncomeList);
                }
                else{
                    setUpPieChart(reportView, leftExpensesList);
                }
            }
        });

        vMiddleGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("reportMonth", currentMonth-1);
                editor.apply();
                reportMonth = currentMonth-1;
                if (reportTab.equals("Income")){
                    setUpPieChart(reportView, middleIncomeList);
                }
                else{
                    setUpPieChart(reportView, middleExpensesList);
                }
            }
        });

        vRightGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("reportMonth", currentMonth);
                editor.apply();
                reportMonth = currentMonth;
                if (reportTab.equals("Income")){
                    setUpPieChart(reportView, rightIncomeList);
                }
                else{
                    setUpPieChart(reportView, rightExpensesList);
                }
            }
        });


        loadData();


        // prepare values for bar chart
        for (transactionHistoryItem item : historyList){
            double amount = Double.parseDouble(item.getmPrice().substring(0, item.getmPrice().length() - 4));
            if (amount > 0){
                if (item.getMonth() == currentMonth-2){
                    leftValueI += amount;
                }
                else if (item.getMonth() == currentMonth-1){
                    middleValueI += amount;
                }
                else if (item.getMonth() == currentMonth){
                    rightValueI += amount;
                }
            }
            else{
                if (item.getMonth() == currentMonth-2){
                    leftValueE -= amount;
                }
                else if (item.getMonth() == currentMonth-1){
                    middleValueE -= amount;
                }
                else if (item.getMonth() == currentMonth){
                    rightValueE -= amount;
                }
            }
        }

        Log.v(TAG, "Drawing graphs...");

        TextView leftMonth = reportView.findViewById(R.id.leftGraph);
        TextView middleMonth = reportView.findViewById(R.id.middleGraph);
        TextView rightMonth = reportView.findViewById(R.id.rightGraph);

        leftMonth.setText(months[currentMonth-2]);
        middleMonth.setText(months[currentMonth-1]);
        rightMonth.setText(months[currentMonth]);

        // prepare values for pie chart
        loadPieChartValues();

        // set up bar and pie charts
        reportTab = sharedPreferences.getString("reportTab", "Income");
        reportMonth = sharedPreferences.getInt("reportMonth", currentMonth);
        if (reportTab.equals("Income")){
            drawGraph(reportView, leftValueI, middleValueI, rightValueI);
            tvGraphHeader.setText("Total Income Over The Last 3 Months");
            if (reportMonth == currentMonth-2){
                setUpPieChart(reportView, leftIncomeList);
            }
            else if (reportMonth == currentMonth-1){
                setUpPieChart(reportView, middleIncomeList);
            }
            else if (reportMonth == currentMonth){
                setUpPieChart(reportView, rightIncomeList);
            }
            else{
                editor.putInt("reportMonth", currentMonth);
                setUpPieChart(reportView, leftIncomeList);
            }
        }
        else if (reportTab.equals("Expenses")){
            drawGraph(reportView, leftValueE, middleValueE, rightValueE);
            tvGraphHeader.setText("Total Expenses Over The Last 3 Months");
            if (reportMonth == currentMonth-2){
                setUpPieChart(reportView, leftExpensesList);
            }
            else if (reportMonth == currentMonth-1){
                setUpPieChart(reportView, middleExpensesList);
            }
            else if (reportMonth == currentMonth){
                setUpPieChart(reportView, rightExpensesList);
            }
            else{
                editor.putInt("reportMonth", currentMonth);
                setUpPieChart(reportView, leftExpensesList);
            }
        }

        return reportView;
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

    private void drawGraph(View view, double leftValue, double middleValue, double rightValue) {
        Log.v(TAG, "LeftValue: " + leftValue + " | MiddleValue: " + middleValue + " | RightValue: " + rightValue);
        double[] values = {leftValue, middleValue, rightValue};
        Arrays.sort(values);
        double largestValue = values[2];
        Log.v(TAG, "Calculating coordinates...");
        int leftHeight = (int) ((1 - (leftValue / largestValue)) * 475);
        int middleHeight = (int) ((1 - (middleValue / largestValue)) * 475);
        int rightHeight = (int) ((1 - (rightValue / largestValue)) * 475);
        Log.v(TAG, "LeftCoord: " + leftHeight + " | MiddleCoord: " + middleHeight + " | RightCoord: " + rightHeight);

        int leftHeightSharp = leftHeight+20;
        if (leftHeightSharp > 475) {
            leftHeightSharp = 475;
        }
        int middleHeightSharp = middleHeight+20;
        if (middleHeightSharp > 475) {
            middleHeightSharp = 475;
        }
        int rightHeightSharp = rightHeight+20;
        if (rightHeightSharp > 475) {
            rightHeightSharp = 475;
        }

        paint.setColor(Color.parseColor("#d97531"));

        Bitmap bg = Bitmap.createBitmap(480, 480, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bg);
        RectF rectL = new RectF(60, leftHeightSharp, 140, 475);
        RectF rectLR = new RectF(60, leftHeight, 140, 475);

        RectF rectM = new RectF(200, middleHeightSharp, 280, 475);
        RectF rectMR = new RectF(200, middleHeight, 280, 475);

        RectF rectR = new RectF(340, rightHeightSharp, 420, 475);
        RectF rectRR = new RectF(340, rightHeight, 420, 475);

        canvas.drawRect(rectL, paint);
        canvas.drawRect(rectM, paint);
        canvas.drawRect(rectR, paint);
        canvas.drawRoundRect(rectLR, rx, ry, paint);
        canvas.drawRoundRect(rectMR, rx, ry, paint);
        canvas.drawRoundRect(rectRR, rx, ry, paint);

        // set background of relative layout to drawn graphs above
        RelativeLayout graphContainer = (RelativeLayout) view.findViewById(R.id.graphContainer);
        graphContainer.setBackground(new BitmapDrawable(bg));
    }

    public void loadPieChartValues(){
        // initialise total amounts for each category under income
        double foodIPrice = 0.00;
        double uncategorizedIPrice = 0.00;
        double clothingIPrice = 0.00;
        double utilitiesIPrice = 0.00;
        double transportIPrice = 0.00;
        double entertainmentIPrice = 0.00;

        // initialise total amounts for each category under income
        double foodIPrice2 = 0.00;
        double uncategorizedIPrice2 = 0.00;
        double clothingIPrice2 = 0.00;
        double utilitiesIPrice2 = 0.00;
        double transportIPrice2 = 0.00;
        double entertainmentIPrice2 = 0.00;

        // initialise total amounts for each category under income
        double foodIPrice3 = 0.00;
        double uncategorizedIPrice3 = 0.00;
        double clothingIPrice3 = 0.00;
        double utilitiesIPrice3 = 0.00;
        double transportIPrice3 = 0.00;
        double entertainmentIPrice3 = 0.00;

        // initialise total amounts for each category under expenses
        double foodEPrice = 0.00;
        double uncategorizedEPrice = 0.00;
        double clothingEPrice = 0.00;
        double utilitiesEPrice = 0.00;
        double transportEPrice = 0.00;
        double entertainmentEPrice = 0.00;

        // initialise total amounts for each category under expenses
        double foodEPrice2 = 0.00;
        double uncategorizedEPrice2 = 0.00;
        double clothingEPrice2 = 0.00;
        double utilitiesEPrice2 = 0.00;
        double transportEPrice2 = 0.00;
        double entertainmentEPrice2 = 0.00;

        // initialise total amounts for each category under expenses
        double foodEPrice3 = 0.00;
        double uncategorizedEPrice3 = 0.00;
        double clothingEPrice3 = 0.00;
        double utilitiesEPrice3 = 0.00;
        double transportEPrice3 = 0.00;
        double entertainmentEPrice3 = 0.00;

        for (int i = 0; i<(historyList.size());i++){
            transactionHistoryItem pieObj = historyList.get(i);
            String mPrice= pieObj.getmPrice().replaceAll("SGD","");
            String category = pieObj.getmLine1();
            if (pieObj.getMonth() == currentMonth-2){
                //Income
                if(mPrice.contains("+")){
                    double incomePrice = Math.abs(Double.valueOf(mPrice));
                    if(category.matches("Food")){
                        foodIPrice += incomePrice;
                    }
                    else if(category.matches("Uncategorized")){
                        uncategorizedIPrice += incomePrice;
                    }
                    else if(category.matches("Clothing")){
                        clothingIPrice += incomePrice;
                    }
                    else if(category.matches("Utilities")){
                        utilitiesIPrice += incomePrice;
                    }
                    else if(category.matches("Transport")){
                        transportIPrice += incomePrice;
                    }
                    else{
                        entertainmentIPrice += incomePrice;
                    }
                }
                //Expenses
                else{
                    double expensesPrice = Math.abs(Double.valueOf(mPrice));
                    if(category.matches("Food")){
                        foodEPrice += expensesPrice;
                    }
                    else if(category.matches("Uncategorized")){
                        uncategorizedEPrice += expensesPrice;
                    }
                    else if(category.matches("Clothing")){
                        clothingEPrice += expensesPrice;
                    }
                    else if(category.matches("Utilities")){
                        utilitiesEPrice += expensesPrice;
                    }
                    else if(category.matches("Transport")){
                        transportEPrice += expensesPrice;
                    }
                    else{
                        entertainmentEPrice += expensesPrice;
                    }
                }
            }
            else if (pieObj.getMonth() == currentMonth-1){
                //Income
                if(mPrice.contains("+")){
                    double incomePrice = Math.abs(Double.valueOf(mPrice));
                    if(category.matches("Food")){
                        foodIPrice2 += incomePrice;
                    }
                    else if(category.matches("Uncategorized")){
                        uncategorizedIPrice2 += incomePrice;
                    }
                    else if(category.matches("Clothing")){
                        clothingIPrice2 += incomePrice;
                    }
                    else if(category.matches("Utilities")){
                        utilitiesIPrice2 += incomePrice;
                    }
                    else if(category.matches("Transport")){
                        transportIPrice2 += incomePrice;
                    }
                    else{
                        entertainmentIPrice2 += incomePrice;
                    }
                }
                //Expenses
                else{
                    double expensesPrice = Math.abs(Double.valueOf(mPrice));
                    if(category.matches("Food")){
                        foodEPrice2 += expensesPrice;
                    }
                    else if(category.matches("Uncategorized")){
                        uncategorizedEPrice2 += expensesPrice;
                    }
                    else if(category.matches("Clothing")){
                        clothingEPrice2 += expensesPrice;
                    }
                    else if(category.matches("Utilities")){
                        utilitiesEPrice2 += expensesPrice;
                    }
                    else if(category.matches("Transport")){
                        transportEPrice2 += expensesPrice;
                    }
                    else{
                        entertainmentEPrice2 += expensesPrice;
                    }
                }
            }
            else if (pieObj.getMonth() == currentMonth){
                //Income
                if(mPrice.contains("+")){
                    double incomePrice = Math.abs(Double.valueOf(mPrice));
                    if(category.matches("Food")){
                        foodIPrice3 += incomePrice;
                    }
                    else if(category.matches("Uncategorized")){
                        uncategorizedIPrice3 += incomePrice;
                    }
                    else if(category.matches("Clothing")){
                        clothingIPrice3 += incomePrice;
                    }
                    else if(category.matches("Utilities")){
                        utilitiesIPrice3 += incomePrice;
                    }
                    else if(category.matches("Transport")){
                        transportIPrice3 += incomePrice;
                    }
                    else{
                        entertainmentIPrice3 += incomePrice;
                    }
                }
                //Expenses
                else{
                    double expensesPrice = Math.abs(Double.valueOf(mPrice));
                    if(category.matches("Food")){
                        foodEPrice3 += expensesPrice;
                    }
                    else if(category.matches("Uncategorized")){
                        uncategorizedEPrice3 += expensesPrice;
                    }
                    else if(category.matches("Clothing")){
                        clothingEPrice3 += expensesPrice;
                    }
                    else if(category.matches("Utilities")){
                        utilitiesEPrice3 += expensesPrice;
                    }
                    else if(category.matches("Transport")){
                        transportEPrice3 += expensesPrice;
                    }
                    else{
                        entertainmentEPrice3 += expensesPrice;
                    }
                }
            }
        }

        leftIncomeList.add(foodIPrice);
        leftIncomeList.add(uncategorizedIPrice);
        leftIncomeList.add(clothingIPrice);
        leftIncomeList.add(utilitiesIPrice);
        leftIncomeList.add(transportIPrice);
        leftIncomeList.add(entertainmentIPrice);
        leftExpensesList.add(foodEPrice);
        leftExpensesList.add(uncategorizedEPrice);
        leftExpensesList.add(clothingEPrice);
        leftExpensesList.add(utilitiesEPrice);
        leftExpensesList.add(transportEPrice);
        leftExpensesList.add(entertainmentEPrice);

        middleIncomeList.add(foodIPrice2);
        middleIncomeList.add(uncategorizedIPrice2);
        middleIncomeList.add(clothingIPrice2);
        middleIncomeList.add(utilitiesIPrice2);
        middleIncomeList.add(transportIPrice2);
        middleIncomeList.add(entertainmentIPrice2);
        middleExpensesList.add(foodEPrice2);
        middleExpensesList.add(uncategorizedEPrice2);
        middleExpensesList.add(clothingEPrice2);
        middleExpensesList.add(utilitiesEPrice2);
        middleExpensesList.add(transportEPrice2);
        middleExpensesList.add(entertainmentEPrice2);

        rightIncomeList.add(foodIPrice3);
        rightIncomeList.add(uncategorizedIPrice3);
        rightIncomeList.add(clothingIPrice3);
        rightIncomeList.add(utilitiesIPrice3);
        rightIncomeList.add(transportIPrice3);
        rightIncomeList.add(entertainmentIPrice3);
        rightExpensesList.add(foodEPrice3);
        rightExpensesList.add(uncategorizedEPrice3);
        rightExpensesList.add(clothingEPrice3);
        rightExpensesList.add(utilitiesEPrice3);
        rightExpensesList.add(transportEPrice3);
        rightExpensesList.add(entertainmentEPrice3);
    }

    public void setUpPieChart(View reportView, ArrayList<Double> itemList){
        pieChart = reportView.findViewById(R.id.pieChart);

        ArrayList<PieEntry> dataEntries = new ArrayList<>();

        for(int i = 0; i<categoryList.length; i++){
            if (itemList.get(i) > 0){
                dataEntries.add(new PieEntry(itemList.get(i).floatValue(), categoryList[i]));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(dataEntries, "");
        pieDataSet.setColors(colourClassArray);
        pieDataSet.setSliceSpace(3);
        int no = 1;
        for (int i : colourClassArray){
            Log.v(TAG, "Colour #" + no + ": " + i);
            no += 1;
        }

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(15);

        if (sharedPref.loadNightMode()){
            pieData.setValueTextColor(Color.WHITE);
            pieChart.setEntryLabelColor(Color.WHITE);
            pieChart.setHoleColor(0xFF363636);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.getLegend().setTextColor(Color.WHITE);
        }
        else{
            pieData.setValueTextColor(Color.BLACK);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setHoleColor(0xFFE8EBE9);
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.getLegend().setTextColor(Color.BLACK);
        }

        pieChart.setData(pieData);
        pieChart.setCenterText(reportTab + " In " + months[reportMonth] + " (SGD)");
        pieChart.setCenterTextSize(17);
        pieChart.setHoleRadius(70);
        pieChart.setTransparentCircleRadius(75);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();

        Log.v(TAG, "Set Pie Chart!");

        // set legend
        ListView lvPieChartLegend = (ListView) reportView.findViewById(R.id.lvPieChartLegend);

        double totalValue = 0;
        for(double value : itemList){
            totalValue += value;
        }

        List<LegendItem> legendItemList = new ArrayList<>();
        legendItemList.add(new LegendItem("Header", 0, 0, new Paint()));

        int i = 0;
        for (PieEntry pe : dataEntries){
            String itemName = pe.getLabel();
            double itemValue = pe.getValue();
            double itemPercent = Math.round((itemValue / totalValue) * 100);
            Paint itemColour = new Paint();
            itemColour.setColor(colourClassArray[i]);

            LegendItem newLegendItem = new LegendItem(itemName, itemValue, itemPercent, itemColour);

            legendItemList.add(newLegendItem);
            Log.v(TAG, "Added new legend item!");
            i++;
        }

        legendItemList.add(new LegendItem("Total", totalValue, 100, new Paint()));

        LegendAdapter legendAdapter = new LegendAdapter(getContext(), R.layout.legend_item, legendItemList);

        lvPieChartLegend.setAdapter(legendAdapter);
        setListViewHeightBasedOnChildren(lvPieChartLegend);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
