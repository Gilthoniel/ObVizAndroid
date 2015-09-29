package com.obviz.review.fragments;

/**
 * Created by gaylor on 09/07/2015.
 * Interface to let fragments have a tutorial
 */
public interface HomeFragment {

    /**
     * Show the tutorial for the given fragment
     */
    void showTutorial();

    /**
     * Must be refresh the data or load for the first time when called
     */
    void refresh();

    /**
     * Return the title of the fragment
     * @return Title of the fragment
     */
    String getTitle();

    /**
     * Return the resource ID of the icon of the fragment
     * @return ID of the icon resource
     */
    int getIcon();
}
