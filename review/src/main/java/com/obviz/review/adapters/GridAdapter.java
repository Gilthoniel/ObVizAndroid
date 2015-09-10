package com.obviz.review.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.obviz.reviews.R;

import java.util.*;

/**
 * Created by gaylor on 09/03/2015.
 * Base adapter for RecyclerView with empty view and error view
 */
public abstract class GridAdapter<T> extends RecyclerView.Adapter<GridAdapter<T>.ViewHolder> {

    public enum State { NONE, LOADING, ERRORS }

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_LOADER = 1;
    public static final int TYPE_ERROR = 2;
    public static final int TYPE_EMPTY = 3;

    private List<T> items;
    private Set<OnItemClickListener> listeners;
    private State mState;
    private int mMax;

    public GridAdapter() {
        items = new LinkedList<>();
        listeners = new HashSet<>();
        mState = State.NONE;
        mMax = 0;
    }

    /**
     * Change the state of the empty view
     * @param state Can be NONE, LOADING or ERRORS
     */
    public void setState(State state) {
        mState = state;
    }

    /**
     * With a value > 0, enable the maximum displayed items
     * @param value number of max items displayed
     */
    public void setMax(int value) {
        mMax = value;
    }

    /**
     * Add items in the list and notify the view
     * @param collection values to add
     */
    public void addAll(Collection<T> collection) {
        items.addAll(collection);
        notifyDataSetChanged();
    }

    /**
     * Add one item and notify the view
     * @param item Item to add
     */
    public void add(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    /**
     * Mix the order of the items
     */
    public void shuffle() {
        Collections.shuffle(items);
        notifyDataSetChanged();
    }

    /**
     * Get the object of the item at the position choosen
     * @param position of the item
     * @return the item
     */
    public T getItem(int position) {
        return items.get(position);
    }
    public List<T> getItems() {
        return items;
    }
    /**
     * Empty the list and notify the list
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Add a listener for the click on an item
     * @param listener OnItemClickListener
     */
    public void addOnItemClickListener(OnItemClickListener listener) {
        listeners.add(listener);
    }

    /**
     * Subclass must override this function to return a value for the TYPE_ITEM
     * @param parent Parent of the Grid
     * @param viewType Type of the item which must be created
     * @return the view fo the child
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the view for the good type
        switch (viewType) {
            case TYPE_LOADER:
                view = inflater.inflate(R.layout.grid_loader, parent, false);
                return new ViewHolder(view) {
                    @Override
                    public void onPopulate(T item) {

                    }
                };
            case TYPE_ERROR:
                view = inflater.inflate(R.layout.grid_empty, parent, false);
                return new ViewHolder(view) {
                    @Override
                    public void onPopulate(T item) {

                    }
                };
            default:
                view = inflater.inflate(R.layout.grid_empty, parent, false);
                return new ViewHolder(view) {
                    @Override
                    public void onPopulate(T item) {

                    }
                };
        }
    }

    @Override
    public void onBindViewHolder(GridAdapter<T>.ViewHolder view, int position) {

        view.position = position;

        if (items.size() > position) {
            // If we have an item type, we populate it with the information
            view.onPopulate(items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (items.size() > 0) {

            return TYPE_ITEM;
        } else {
            // If the list of items is empty, we return the type of the view related to the state
            switch (mState) {
                case NONE:
                    return TYPE_EMPTY;
                case LOADING:
                    return TYPE_LOADER;
                default:
                    return TYPE_ERROR;
            }
        }
    }

    @Override
    public int getItemCount() {

        // Return 1 if the list is zero, to display the empty view
        if (mMax <= 0) {

            return items.size() > 0 ? items.size() : 1;
        } else {

            return items.size() > 0 ? Math.max(mMax, items.size()) : 1;
        }
    }


    /**
     * Child of the RecyclerView
     */
    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // position of the child in the grid view
        private int position;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Notify the listeners that a click occurred on this child
            for (OnItemClickListener listener : listeners) {
                listener.onClick(position);
            }
        }

        public abstract void onPopulate(T item);
    }

    /**
     * Child click interface
     */
    public interface OnItemClickListener {
        void onClick(int position);
    }
}
