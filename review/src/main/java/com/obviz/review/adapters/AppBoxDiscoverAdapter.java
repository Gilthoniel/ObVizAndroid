package com.obviz.review.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.obviz.review.Constants;
import com.obviz.review.managers.ImageObserver;
import com.obviz.review.managers.ImagesManager;
import com.obviz.review.managers.TopicsManager;
import com.obviz.review.models.AndroidApp;
import com.obviz.review.models.Category;
import com.obviz.review.views.GaugeChart;
import com.obviz.review.views.InfiniteScrollable;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.reviews.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaylor on 05-Aug-15.
 * Adapter for AndroidApp in a RecyclerView
 */
public class AppBoxDiscoverAdapter extends AppBoxAdapter implements TopicsManager.TopicsObserver, ImageObserver, InfiniteScrollable {

}
