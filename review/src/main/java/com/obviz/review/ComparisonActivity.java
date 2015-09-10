package com.obviz.review;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.obviz.review.adapters.GaugeAdapter;
import com.obviz.review.adapters.GridAdapter;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.views.GridRecyclerView;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.reviews.R;

import java.util.LinkedList;
import java.util.List;


public class ComparisonActivity extends AppCompatActivity implements GaugeAdapter.GaugeAdaptable, ImageObserver {

    private AndroidApp mApplication;
    private AndroidApp mComparison;
    private Button mAppButton;
    private Button mComButton;

    @Override
    public List<AndroidApp> getListApplications() {
        List<AndroidApp> list = new LinkedList<>();
        list.add(mApplication);
        list.add(mComparison);

        return list;
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {

        if (url.equals(mApplication.getLogo())) {
            mAppButton.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);
            mAppButton.setCompoundDrawablePadding(10);
        }

        if (url.equals(mComparison.getLogo())) {
            mComButton.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), bitmap), null, null, null);
            mComButton.setCompoundDrawablePadding(10);
        }
    }

    @Override
    protected void onCreate(Bundle states) {
        super.onCreate(states);
        setContentView(R.layout.activity_comparison);

        if (states == null) {

            Intent intent = getIntent();
            mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
            mComparison = intent.getParcelableExtra(Constants.INTENT_COMPARISON_APP);
        } else {

            mApplication = states.getParcelable(Constants.STATE_APP);
            mComparison = states.getParcelable(Constants.STATE_COMPARISON);
        }

        if (mApplication != null && mComparison != null) {

            // Load images
            ImagesManager.instance().get(mApplication.getLogo(), this);
            ImagesManager.instance().get(mComparison.getLogo(), this);

            mAppButton = (Button) findViewById(R.id.app_button);
            mAppButton.setText(mApplication.getName());
            mAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            mComButton = (Button) findViewById(R.id.comparison_button);
            mComButton.setText(mComparison.getName());
            mComButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    intent.putExtra(Constants.INTENT_APP, (Parcelable) mComparison);

                    startActivity(intent);
                }
            });

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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        ConnectionService.instance().cancel();
    }

    @Override
    public void onSaveInstanceState(Bundle states) {

        states.putParcelable(Constants.STATE_APP, mApplication);
        states.putParcelable(Constants.STATE_COMPARISON, mComparison);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comparison, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
