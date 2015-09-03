package com.obviz.review.managers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import com.obviz.review.models.Category;
import com.obviz.review.models.SuperCategory;
import com.obviz.review.webservice.GeneralWebService;
import com.obviz.review.webservice.RequestCallback;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by gaylor on 08/07/2015.
 * Manage the different categories
 */
public class CategoryManager {

    private static CategoryManager instance;

    private Map<String, Category> mCategories;
    private Map<Integer, List<Category>> mTypes;
    private Map<Integer, SuperCategory> mTypeTitles;
    private Set<CategoryObserver> mObservers;
    private Context mContext;
    private ReentrantLock mLock;

    private CategoryManager() {}

    public static void init(Context context) {
        instance = new CategoryManager();

        instance.mLock = new ReentrantLock();
        instance.mLock.lock();

        instance.mCategories = new HashMap<>();
        instance.mTypes = new HashMap<>();
        instance.mTypeTitles = new HashMap<>();
        instance.mObservers = new HashSet<>();
        instance.mContext = context;

        GeneralWebService.instance().getSuperCategories(new SuperCategoryCallback());
    }

    public static CategoryManager instance() {
        return instance;
    }

    public Category getFrom(String category, CategoryObserver observer) {

        mLock.lock();

        if (mCategories.containsKey(category)) {

            mLock.unlock();
            return mCategories.get(category);

        } else if(isInternetAvailable()) {

            mObservers.add(observer);
            GeneralWebService.instance().getSuperCategories(new SuperCategoryCallback());
        }

        return Category.instanceDefault;
    }

    public Collection<SuperCategory> getSupers() {

        mLock.lock();

        Collection<SuperCategory> list = mTypeTitles.values();

        mLock.unlock();

        return list;
    }

    public List<Category> getCategories(int id) {

        if (id == 0) {
            return Collections.emptyList();
        }

        mLock.lock();
        List<Category> list = mTypes.get(id);
        mLock.unlock();

        return list;
    }

    public interface CategoryObserver {
        void onCategoriesLoaded();
    }

    private static class SuperCategoryCallback implements RequestCallback<List<SuperCategory>> {

        @Override
        public void onSuccess(List<SuperCategory> result) {

            instance.mTypes.clear();
            instance.mTypeTitles.clear();
            instance.mCategories.clear();

            for (SuperCategory type : result) {
                instance.mTypes.put(type._id, new LinkedList<Category>());
                instance.mTypeTitles.put(type._id, type);
            }

            GeneralWebService.instance().getCategories(new CategoryCallback());
        }

        @Override
        public void onFailure(Errors error) {
            Log.e("__SUPER_CATEGORIES__", "Error when loading: "+error);
            instance.mLock.unlock();
        }

        @Override
        public Type getType() {
            return new TypeToken<List<SuperCategory>>(){}.getType();
        }
    }

    private static class CategoryCallback implements RequestCallback<List<Category>> {

        @Override
        public void onSuccess(List<Category> result) {
            for (Category category : result) {
                instance.mCategories.put(category.category, category);

                for (int type : category.types) {
                    instance.mTypes.get(type).add(category);
                }
            }

            instance.mLock.unlock();
            for (CategoryObserver observer : instance.mObservers) {
                observer.onCategoriesLoaded();
            }
            instance.mObservers.clear();
        }

        @Override
        public void onFailure(Errors error) {
            Log.e("__CATEGORIES__", "Error when loading: "+error);
            instance.mLock.unlock();
        }

        @Override
        public Type getType() {
            return new TypeToken<List<Category>>(){}.getType();
        }
    }

    /* PRIVATE FUNCTIONS */

    private boolean isInternetAvailable() {

        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        return info != null && info.isConnected();
    }
}
