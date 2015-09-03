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

    public void setState(State state) {
        mState = state;
    }

    public void setMax(int value) {
        mMax = value;
    }

    public void addAll(Collection<T> collection) {
        items.addAll(collection);
        notifyDataSetChanged();
    }

    public void add(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void shuffle() {
        Collections.shuffle(items);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addOnItemClickListener(OnItemClickListener listener) {
        listeners.add(listener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

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
            view.onPopulate(items.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (items.size() > 0) {

            return TYPE_ITEM;
        } else {

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

        if (mMax <= 0) {
            return items.size() > 0 ? items.size() : 1;
        } else {

            return items.size() > 0 ? Math.max(mMax, items.size()) : 1;
        }
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private int position;

        public ViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            for (OnItemClickListener listener : listeners) {
                listener.onClick(position);
            }
        }

        public abstract void onPopulate(T item);
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
