package com.obviz.review.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.obviz.review.ActivitySearch;
import com.obviz.review.Constants;
import com.obviz.review.adapters.HistoryAdapter;
import com.obviz.review.database.DatabaseService;
import com.obviz.review.models.HistoryItem;
import com.obviz.reviews.R;

/**
 * Created by gaylor on 8/3/2015.
 * Display the list of the last researches
 */
public class HistoryFragment extends ListFragment implements HomeFragment {

    private HistoryAdapter mAdapter;

    @Override
    public void showTutorial() {

    }

    @Override
    public String getTitle() {
        return "Search History";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_history_black_24dp;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle states) {

        View view = inflater.inflate(R.layout.history_fragment, parent, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_history);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    // Clear the adapter and the database
                    case R.id.action_clear:
                        DatabaseService.instance().dropHistory();

                        HistoryAdapter adapter = (HistoryAdapter) getListAdapter();
                        adapter.clear();
                }

                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            Cursor cursor = DatabaseService.instance().selectHistory();
            if (cursor != null && cursor.getCount() > 0) {
                mAdapter.clear();

                while (!cursor.isLast()) {
                    cursor.moveToNext();

                    mAdapter.add(new HistoryItem(cursor));
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle states) {
        super.onActivityCreated(states);

        mAdapter = new HistoryAdapter(getActivity());
        setListAdapter(mAdapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), ActivitySearch.class);
                intent.putExtra(Constants.INTENT_SEARCH, mAdapter.getItem(i));

                startActivity(intent);
            }
        });
    }
}
