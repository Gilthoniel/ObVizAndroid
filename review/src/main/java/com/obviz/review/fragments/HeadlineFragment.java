package com.obviz.review.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import com.obviz.review.ActivitySearch;
import com.obviz.review.Constants;
import com.obviz.review.DetailsActivity;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.managers.TutorialManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.Headline;
import com.obviz.review.models.OpinionValue;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;
import com.obviz.reviews.R;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;

import java.lang.reflect.Type;

/**
 * Created by gaylor on 09/18/2015.
 * Search bar and headlines fragment
 */
public class HeadlineFragment extends Fragment implements HomeFragment, TopicsManager.TopicsObserver, ImageObserver {

    private View mView;
    private GaugeChart mChart;
    private Headline mHeadline;
    private SearchView mSearchView;

    @Override
    public void showTutorial() {

        if (mView != null) {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    MaterialShowcaseSequence sequence = TutorialManager.sequence(getActivity());
                    sequence.addSequenceItem(
                            mView.findViewById(R.id.tutorial_target),
                            getString(R.string.tutorial_headline_1),
                            "Got it"
                    );
                    sequence.addSequenceItem(
                            getView().findViewById(R.id.gauge_chart),
                            getString(R.string.tutorial_headline_2),
                            "Got it"
                    );

                    sequence.singleUse(Constants.TUTORIAL_HEADLINE_KEY);
                    sequence.start();
                }
            });
        }
    }

    @Override
    public void refresh() {
        if (mView == null) {
            return;
        }

        GeneralWebService.instance().getHeadline(new RequestCallback<Headline>() {
            @Override
            public void onSuccess(Headline result) {
                final AndroidApp app = result.getApps().get(0);

                ((TextView) mView.findViewById(R.id.headline_title)).setText(result.getTitle());
                ((TextView) mView.findViewById(R.id.app_description)).setText(app.getDescription(128));

                mView.findViewById(R.id.button_more).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), DetailsActivity.class);
                        intent.putExtra(Constants.INTENT_APP, (Parcelable) app);

                        startActivity(intent);
                    }
                });

                ImagesManager.instance().get(app.getLogo(), HeadlineFragment.this);

                mHeadline = result;
                onTopicsLoaded();
            }

            @Override
            public void onFailure(Errors error) {
                Log.e("--HEADLINE--", "Message: " + error.name());
                mView.findViewById(R.id.layout_headline).setVisibility(View.GONE);
            }

            @Override
            public Type getType() {
                return Headline.class;
            }
        });
    }

    @Override
    public String getTitle() {
        return "Home";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_home_black_24dp;
    }

    @Override
    public void onTopicsLoaded() {
        GaugeChart.GaugeChartData mChartData = new GaugeChart.GaugeChartData(100);
        mChartData.addSegments(Constants.CHART_SEGMENTS);
        mChartData.setTextSize(10);

        GaugeChart.Arrow arrow = new GaugeChart.Arrow();
        arrow.setHeight(1.0f);
        arrow.setInnerRadius(0.4f);
        arrow.setBaseLength(5);
        arrow.setColor(mView.getResources().getColor(R.color.appColor));

        OpinionValue opinion;
        if (mHeadline.getTopicID() != null &&
                (opinion = mHeadline.getApps().get(0).getOpinion(mHeadline.getTopicID())) != null) {

            arrow.setValue(opinion.percentage());
            mChartData.setText(TopicsManager.instance().getTitle(mHeadline.getTopicID(), HeadlineFragment.this));

        } else {
            arrow.setValue(mHeadline.getApps().get(0).getGlobalOpinion());
            mChartData.setText("Global");
        }

        mChartData.addArrow(arrow);
        mChart.setData(mChartData);
    }

    @Override
    public void onImageLoaded(String url, Bitmap bitmap) {
        ((ImageView) mView.findViewById(R.id.app_logo)).setImageBitmap(bitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle states) {

        mView = inflater.inflate(R.layout.fragment_headline, parent, false);

        mSearchView = (SearchView) mView.findViewById(R.id.searchView);
        mSearchView.setFocusable(false);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Intent intent = new Intent(getContext(), ActivitySearch.class);
                intent.putExtra(Constants.INTENT_SEARCH, s);

                startActivity(intent);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        mChart = (GaugeChart) mView.findViewById(R.id.gauge_chart);

        refresh();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() != null)
            getView().requestFocus();
    }
}
