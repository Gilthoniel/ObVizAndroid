package com.obviz.review.managers;

import android.content.Context;
import android.os.AsyncTask;
import com.obviz.review.models.Category;
import com.obviz.review.models.SuperCategory;
import com.obviz.review.service.NetworkChangeReceiver;
import com.obviz.review.webservice.GeneralWebService;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaylor on 08/07/2015.
 * Manage the different categories
 */
public class CategoryManager {

    private static CategoryManager instance;

    private Map<String, Category> mCategories;
    private Map<Integer, SuperCategory> mTypes;
    private Set<CategoryObserver> mObservers;
    private ReentrantLock mLock;
    private Context mContext;

    private CategoryManager(Context context) {
        mLock = new ReentrantLock();
        mObservers = new HashSet<>();
        mContext = context;

        new Initialization().execute();
    }

    public static void init(Context context) {
        instance = new CategoryManager(context);
    }

    public static CategoryManager instance() {
        return instance;
    }

    public void awake() {
        if (mObservers.size() > 0 && !mLock.isLocked()) {
            new Initialization().execute();
        }
    }

    public Category getFrom(String category, CategoryObserver observer) {

        if (mCategories.containsKey(category)) {

            return mCategories.get(category);

        }

        plannedInit(observer);
        return Category.instanceDefault;
    }

    public Collection<SuperCategory> getSupers(CategoryObserver observer) {

        if (mTypes != null && !mTypes.isEmpty()) {
            return mTypes.values();
        }

        plannedInit(observer);
        return Collections.emptyList();
    }

    public interface CategoryObserver {
        void onCategoriesLoaded();
    }

    /* PRIVATE FUNCTIONS */

    private void plannedInit(CategoryObserver observer) {
        mObservers.add(observer);

        if (!mLock.isLocked() && NetworkChangeReceiver.isInternetAvailable(mContext)) {
            new Initialization().execute();
        }
    }

    private class Initialization extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            mLock.lock();

            mCategories = new HashMap<>();
            mTypes = new HashMap<>();

            List<Category> categories = GeneralWebService.instance().getCategories();
            List<SuperCategory> types = GeneralWebService.instance().getSuperCategories();

            if (types != null) {
                for (SuperCategory type : types) {
                    type.categories = new LinkedList<>();
                    mTypes.put(type._id, type);
                }

                if (categories != null) {
                    for (Category category : categories) {
                        mCategories.put(category.category, category);

                        for (int type : category.types) {
                            mTypes.get(type).categories.add(category);
                        }
                    }
                }
            }

            mLock.unlock();

            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            for (CategoryObserver observer : instance.mObservers) {
                observer.onCategoriesLoaded();
            }
            mObservers.clear();
        }
    }
}
