package sg.edu.np.financetracker2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TransactionHistoryFragment extends Fragment implements recycleViewAdaptorHistory.onHistoryListener{
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
    private recycleViewAdaptorHistory mAdaptor;
    final String TAG = "FinanceTracker";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View historyView = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        // load transaction history
        loadData();

        RelativeLayout rlNoTransactionMessage = historyView.findViewById(R.id.rlNoTransactionMessage);

        // remove no transactions message if there are transactions
        if (!historyList.isEmpty()){
            rlNoTransactionMessage.setVisibility(View.GONE);
        }

        //RecycleViewHistory
        final RecyclerView recyclerViewCustom = historyView.findViewById(R.id.rvAllHistory);
        recyclerViewCustom.setHasFixedSize(true);
        mAdaptor = new recycleViewAdaptorHistory(historyList,this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewCustom);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());

        setHasOptionsMenu(true);

        return historyView;
        
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<transactionHistoryItem>>() {
        }.getType();
        historyList = gson.fromJson(json, type);

        if (historyList == null) {
            historyList = new ArrayList<>();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdaptor.getFilter().filter(newText);
                return false;
            }
        });
    }

    //on transaction item click in recyclerview
    @Override
    public void onHistoryClick(int position) {
        //sent item position in recyclerview to transaction detail activity
        transactionHistoryItem obj = historyList.get(position);
        Intent intent = new Intent(getActivity(),TransactionDetailActivity.class);
        intent.putExtra("position",position);
        startActivity(intent);
    }

    /**
     * Swipe left to delete item
     * balance and item in recylerview will be updated
     * after swiping there is undo button at the bottom
     * undo to get back item
     */
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            //retrieve balance
            Double balanceAmount = getBalance();
            final transactionHistoryItem obj = historyList.get(position);

            //formatting price
            String price = obj.getmPrice().replace("SGD","");
            if (obj.getmPrice().contains("+")){
                price = price.replace("+","");
                //removing item price from balance
                balanceAmount = (balanceAmount-Double.parseDouble(price));
            }
            else{
                price = price.replace("-","");
                //removing item price from balance
                balanceAmount = (balanceAmount+Double.parseDouble(price));
            }
            //update balance after item deleted
            updateBalance(balanceAmount);

            //remove item on swipe
            historyList.remove(position);
            saveData();
            mAdaptor.notifyDataSetChanged();


            final RecyclerView recyclerViewCustom = getView().findViewById(R.id.rvAllHistory);
            //balance for inner class
            final Double balance = getBalance();
            Snackbar.make(recyclerViewCustom, "Deleting Item",Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Double innerBal;
                            //formatting price
                            String price = obj.getmPrice().replace("SGD","");
                            if (obj.getmPrice().contains("+")){
                                price = price.replace("+","");
                                //removing item price from balance
                                innerBal = (balance+Double.parseDouble(price));
                            }
                            else{
                                price = price.replace("-","");
                                //removing item price from balance
                                innerBal = (balance-Double.parseDouble(price));
                            }
                            //update balance after item deleted
                            updateBalance(innerBal);

                            //add item back to history list
                            historyList.add(position,obj);
                            saveData();
                            mAdaptor.notifyItemInserted(position);
                        }
                    }).show();
        }

        //recycler view swipe decorator
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    // read balance.txt file and get current balance
    private Double getBalance(){
        String data = "";
        StringBuffer stringBuffer = new StringBuffer();

        try{
            InputStream inputStream = getActivity().getApplicationContext().openFileInput("balance.txt");
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
        writeToFile(newData, getActivity().getApplicationContext());
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
