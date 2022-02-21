package com.example.myapplication;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> namelistData;

    public AutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        namelistData = new ArrayList<>();
    }

    public void setData(List<String> list) {
        namelistData.clear();
        namelistData.addAll(list);
    }

    @Override
    public int getCount() {
        return namelistData.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return namelistData.get(position);
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    public String getObject(int position) {
        return namelistData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = namelistData;
                    filterResults.count = namelistData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}