package com.obviz.review.managers;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by gaylor on 08/21/2015.
 * Collection of functions for different things
 */
public class Utils {

    public static <T extends Comparable<T>> boolean checkSorting(Collection<T> collection) {

        Iterator<T> it = collection.iterator();
        T old = null;
        while (it.hasNext()) {
            T current = it.next();

            if (old != null && current.compareTo(old) < 0) {
                return false;
            }

            old = current;
        }

        return true;
    }
}
