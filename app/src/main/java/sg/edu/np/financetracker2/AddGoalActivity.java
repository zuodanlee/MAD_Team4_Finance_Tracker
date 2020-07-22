package sg.edu.np.financetracker2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class AddGoalActivity extends AppCompatActivity implements recycleViewHolderCategory.OnCategoryListener{
    sharedPref sharedPref;
    final String TAG = "FinanceTracker";
    int image;
    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<Goal> goalList = new ArrayList<>();
    private String goalText;
    private String deadline;
    DatePickerDialog picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Night mode
        sharedPref = new sharedPref(this);
        if(sharedPref.loadNightMode()==true){
            setTheme(R.style.darkTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        Button confirmButton = findViewById(R.id.addButton);
        final EditText etGoalAmt = findViewById(R.id.goalAmt);
        final TextView categoryTextView = findViewById(R.id.categoryText);
        final EditText goalEditText = findViewById(R.id.goalText);
        final EditText goalDeadline = findViewById(R.id.goalDeadline);
        goalDeadline.setInputType(InputType.TYPE_NULL);

        //Select deadline for goal
        goalDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                //Date picker dialog
                picker = new DatePickerDialog(AddGoalActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                goalDeadline.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        //Add new goal
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double goalTotal;
                Double goalAmt;
                try {
                    goalTotal = getGoalTotal();
                    goalAmt = Double.parseDouble(etGoalAmt.getText().toString());
                    goalText = goalEditText.getText().toString();
                    deadline = goalDeadline.getText().toString();
                    Log.v(TAG, goalAmt.toString());
                    Log.v(TAG, goalText);
                    Log.v(TAG, deadline);
                    //Validation
                    if (goalText.length() == 0) {
                        //Notification to enter a goal
                        Toast.makeText(getApplicationContext(), "Please enter goal", Toast.LENGTH_SHORT).show();
                    } else if(categoryTextView.length() == 0) {
                        //Notification to choose a category
                        Toast.makeText(getApplicationContext(), "Please choose a category", Toast.LENGTH_SHORT).show();
                    } else if(goalDeadline.length() == 0) {
                        //Notification to choose a deadline
                        Toast.makeText(getApplicationContext(), "Please select a deadline", Toast.LENGTH_SHORT).show();
                    }  else {
                            //Record goal amount
                            String amount = goalAmt.toString();
                            Log.v(TAG, amount);
                            //Update goal total
                            Log.v(TAG, "Goal Total: " + goalTotal);
                            goalTotal += goalAmt;
                            updateGoalTotal(goalTotal);

                            //Get goal category
                            String category = categoryTextView.getText().toString();
                            InitImage(category);
                            //Create new goal object
                            Goal newGoal = new Goal(image, category, goalText, deadline, amount);
                            //Add goal to goal list
                            loadData();
                            goalList.add(newGoal);
                            saveData();
                            finish();
                    }
                } catch (Exception e) {
                    //Notification to enter amount
                    Toast.makeText(getApplicationContext(), "Invalid amount, please try again", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //Button back to goal fragment
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Back to goal fragment");
                finish();
            }
        });

        //RecyclerViewCategory
        final RecyclerView recyclerViewCustom = findViewById(R.id.goalCategory);
        final recycleViewAdaptorCategory mAdaptor = new recycleViewAdaptorCategory(categoryList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());
        InitData();
    }


    public void InitData() {
        String uncategorized = "Uncategorized";
        String food = "Food";
        String clothing = "Clothing";
        String utilities = "Utilities";
        String transport = "Transport";
        String entertainment = "Entertainment";
        categoryList.add(uncategorized);
        categoryList.add(food);
        categoryList.add(clothing);
        categoryList.add(utilities);
        categoryList.add(transport);
        categoryList.add(entertainment);
    }

    public int InitImage(String category) {
        if (category.equals("Food")) {
            image = R.raw.food;
        } else if (category.equals("Uncategorized")) {
            image = R.raw.uncategorized;
        } else if (category.equals("Clothing")) {
            image = R.raw.clothing;
        } else if (category.equals("Utilities")) {
            image = R.raw.utilities;
        } else if (category.equals("Transport")) {
            image = R.raw.transport;
        } else {
            image = R.raw.entertainment;
        }
        return image;
    }

    //Get goal total
    private Double getGoalTotal(){
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = getApplicationContext().openFileInput("goalTotal.txt");
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

    //Update goal total
    private void updateGoalTotal(Double newBal){
        String newData = newBal.toString();
        writeToFile(newData, getApplicationContext());
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("goalTotal.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            Log.v(TAG, "Updated goal total");
        }
        catch (IOException e) {
            Log.e(TAG, "Exception! File write failed: " + e.toString());
        }
    }

    //Save goal list
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(goalList);
        editor.putString("Goal List", json);
        editor.apply();
    }

    //Load goal list
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Goal List",null);
        Type type = new TypeToken<ArrayList<Goal>>(){}.getType();
        goalList =  gson.fromJson(json,type);

        if(goalList == null){
            goalList = new ArrayList<>();
        }
    }

    @Override
    public void onCategoryClick(int position) {
        String category = categoryList.get(position);
        TextView tvCategory = findViewById(R.id.categoryText);
        tvCategory.setText(category);
    }

    protected void onStop(){
        super.onStop();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}