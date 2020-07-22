package sg.edu.np.financetracker2;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.spi.FileTypeDetector;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingFragment extends Fragment {
    private Switch mySwitch;
    private Switch notificationSwitch;
    sharedPref sharedPref;
    Dialog myDialog;
    final String TAG = "SettingActivity";
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();

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

        notificationSwitch = (Switch)settingView.findViewById(R.id.notificationSwitch);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    registerAlarm();
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
                        double goalTotal = getGoalTotal();
                        Log.v(TAG, "Balance: " + goalTotal);
                        goalTotal = 0;
                        updateGoalTotal(goalTotal);
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

        //Export data as csv
        //pop up screen will appear
        Button exportData = (Button)settingView.findViewById(R.id.exportButton);
        exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Spinner spinner;
                myDialog = new Dialog(getActivity());
                myDialog.setContentView(R.layout.custompopup_export_setting);
                spinner = myDialog.findViewById(R.id.spinner);

                List<String> fileType = new ArrayList<>();
                fileType.add(0,"CSV");
                fileType.add(1,"Excel");

                //style and populate the spinner
                ArrayAdapter<String> dataAdapter;
                dataAdapter = new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,fileType);
                //dropdown layout style
                dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                //attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);

                Button cancelButton = myDialog.findViewById(R.id.cancelButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });

                Button okButton = myDialog.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get updated history list
                        loadData();
                        String fileType =  (String) spinner.getSelectedItem();
                        if (fileType.equals("CSV")){
                            //create and export csv
                            createExportCsv();
                        }
                        else{
                            //create and export excel sheet
                            createExportExcel();
                        }
                    }
                });
                myDialog.show();
            }
        });

        return settingView;
    }

    private void registerAlarm() {
        AlarmManager manager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),0,intent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY,8);
        calendar.set(Calendar.MINUTE,0);

        manager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    private void createExportCsv(){

        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Date,Income/Expenses,Category,Note,Price");

        for (int i = 0; i<historyList.size(); i++){
            transactionHistoryItem obj = historyList.get(i);
            String incomeExpense = null;
            if(obj.getmPrice().contains("+")) {
                incomeExpense = "Income";
            }
            else{
                incomeExpense = "Expenses";
            }
            data.append("\n"+obj.getmDate()+","+incomeExpense+","+obj.getmLine1()+","+obj.getmLine2()+","+obj.getmPrice());
        }
        try {
            //saving the file into device
            FileOutputStream out =  getActivity().openFileOutput("MoneySmartData.csv",Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //exporting
            Context context = getActivity().getApplicationContext();
            File fileLocation = new File(getActivity().getFilesDir(),"MoneySmartData.csv");
            Uri path = FileProvider.getUriForFile(context,"sg.edu.np.financetracker2",fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT,"Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM,path);
            startActivity(Intent.createChooser(fileIntent,"Exporting Data"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void createExportExcel(){
        //creating and exporting excel sheet
        Workbook wb=new HSSFWorkbook();
        Cell cell=null;

        //Now we are creating sheet
        Sheet sheet=null;
        sheet = wb.createSheet("MoneySmart");
        //Now column and row

        //first row
        Row row =sheet.createRow(0);

        cell=row.createCell(0);
        cell.setCellValue("Date");
        cell=row.createCell(1);
        cell.setCellValue("Income/Expenses");
        cell=row.createCell(2);
        cell.setCellValue("Category");
        cell=row.createCell(3);
        cell.setCellValue("Note");
        cell=row.createCell(4);
        cell.setCellValue("Price");

        for (int i = 0; i<historyList.size(); i++) {
            transactionHistoryItem obj = historyList.get(i);
            String incomeExpense = null;
            if (obj.getmPrice().contains("+")) {
                incomeExpense = "Income";
            } else {
                incomeExpense = "Expenses";
            }
            //creating row
            row =sheet.createRow(i+1);

            cell=row.createCell(0);
            cell.setCellValue(obj.getmDate());
            cell=row.createCell(1);
            cell.setCellValue(incomeExpense);
            cell=row.createCell(2);
            cell.setCellValue(obj.getmLine1());
            cell=row.createCell(3);
            cell.setCellValue(obj.getmLine2());
            cell=row.createCell(4);
            cell.setCellValue(obj.getmPrice());
        }


        try {
            //saving the file into device
            FileOutputStream out = getActivity().openFileOutput("MoneySmartData.xls", Context.MODE_PRIVATE);
            wb.write(out);
            out.close();

            //exporting
            Context context = getActivity().getApplicationContext();
            File fileLocation = new File(getActivity().getFilesDir(), "MoneySmartData.xls");
            Uri path = FileProvider.getUriForFile(context, "sg.edu.np.financetracker2", fileLocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/xls");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Exporting Data"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    private Double getGoalTotal(){
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = getActivity().openFileInput("goalTotal.txt");
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

    private void updateGoalTotal(Double newBal){
        String newData = newBal.toString();
        writeToGoalFile(newData, getActivity());
    }

    private void writeToGoalFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("goalTotal.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
        }
    }
}
