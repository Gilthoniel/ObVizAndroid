package com.obviz.review.managers;

/**
 * Created by gaylor on 30.07.15.
 * Actions to perform when we want to pause some activity
 */
public interface Pausable {
    void onPause();
    void onResume();
}
