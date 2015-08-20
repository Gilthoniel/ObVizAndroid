package com.obviz.review;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.obviz.review.adapters.ComparisonAdapter;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.webservice.ConnectionService;
import com.obviz.reviews.R;


public class ComparisonActivity extends AppCompatActivity implements ImageObserver {

    private AndroidApp mApplication;
    private AndroidApp mComparison;
    private ComparisonAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        Intent intent = getIntent();
        mApplication = intent.getParcelableExtra(Constants.INTENT_APP);
        mComparison = intent.getParcelableExtra(Constants.INTENT_COMPARISON_APP);

        if (mApplication != null && mComparison != null) {

            ListFragment list = (ListFragment) getFragmentManager().findFragmentById(R.id.fragment);
            mAdapter = new ComparisonAdapter(getApplicationContext(), mApplication, mComparison);
            list.setListAdapter(mAdapter);
            list.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(ComparisonActivity.this, ReviewsComparisonActivity.class);

                    intent.putExtra(Constants.INTENT_APP_ID, mApplication.getAppID());
                    intent.putExtra(Constants.INTENT_COMPARISON_APP_ID, mComparison.getAppID());
                    intent.putExtra(Constants.INTENT_TOPIC_ID, mAdapter.getItem(position));

                    startActivity(intent);
                }
            });

            // Populate views
            ImagesManager.getInstance().get(mApplication.getImage(), this);
            ImagesManager.getInstance().get(mComparison.getImage(), this);

            TextView appName = (TextView) findViewById(R.id.app_name);
            appName.setText(mApplication.getName());

            TextView comparisonName = (TextView) findViewById(R.id.comparison_name);
            comparisonName.setText(mComparison.getName());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    public void onImageLoaded(String url, Bitmap bitmap) {

        ImageView image;
        if (url.equals(mApplication.getImage())) {
            image = (ImageView) findViewById(R.id.app_logo);
        } else {
            image = (ImageView) findViewById(R.id.comparison_logo);
        }

        image.setImageBitmap(bitmap);
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
                intent.putExtra(Constants.INTENT_APP_ID, mComparison.getAppID());

                startActivity(intent);
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
