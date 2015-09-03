package com.obviz.review;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.obviz.review.adapters.AppBoxAdapter;
import com.obviz.review.adapters.GaugeAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;


public class ComparisonActivity extends AppCompatActivity implements GaugeAdapter.GaugeAdaptable {

    private AndroidApp mApplication;
    private AndroidApp mComparison;

    @Override
    public List<AndroidApp> getListApplications() {
        List<AndroidApp> list = new LinkedList<>();
        list.add(mApplication);
        list.add(mComparison);

        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        Intent intent = getIntent();
        mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
        mComparison = intent.getParcelableExtra(Constants.INTENT_COMPARISON_APP);

        if (mApplication != null && mComparison != null) {

            GridRecyclerView grid = (GridRecyclerView) findViewById(R.id.grid_view);
            final GaugeAdapter adapter = new GaugeAdapter(this);
            adapter.addOnItemClickListener(new GridAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(ComparisonActivity.this, ReviewsComparisonActivity.class);

                    intent.putExtra(Constants.INTENT_APP, (Parcelable) mApplication);
                    intent.putExtra(Constants.INTENT_COMPARISON_APP, (Parcelable) mComparison);
                    intent.putExtra(Constants.INTENT_TOPIC_ID, adapter.getItem(position).topicID);

                    startActivity(intent);
                }
            });
            grid.setAdapter(adapter);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comparison, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_go_to:
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra(Constants.INTENT_APP, (Parcelable) mComparison);

                startActivity(intent);
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
