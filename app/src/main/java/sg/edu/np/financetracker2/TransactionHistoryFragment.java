package sg.edu.np.financetracker2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TransactionHistoryFragment extends Fragment {
    ArrayList<transactionHistoryItem> historyList = new ArrayList<>();
    private recycleViewAdaptorHistory mAdaptor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View historyView = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        //RecycleViewHistory
        final RecyclerView recyclerViewCustom = historyView.findViewById(R.id.rvAllHistory);
        recyclerViewCustom.setHasFixedSize(true);
        mAdaptor = new recycleViewAdaptorHistory(historyList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewCustom.setLayoutManager(mLayoutManager);
        recyclerViewCustom.setAdapter(mAdaptor);
        recyclerViewCustom.setItemAnimator(new DefaultItemAnimator());

        setHasOptionsMenu(true);

        return historyView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
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
}
