package org.ereuse.scanner.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.Manufacturer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jamgo SCCL on 24/05/17.
 */

public class SearchableListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<Manufacturer> items;
    private List<Manufacturer> filteredItems;
    private ValueFilter valueFilter;

    public SearchableListAdapter(Context context, List<Manufacturer> items) {
        this.context = context;
        this.items = items;
        this.filteredItems = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Set data into the view.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View searchableItemView = inflater.inflate(R.layout.searchable_item,
                parent, false);

        TextView label = (TextView) searchableItemView.findViewById(R.id.searchableItemTextview);

        Manufacturer item = this.items.get(position);
        label.setText(item.getLabel());

        return searchableItemView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List filterList = new ArrayList();
                for (int i = 0; i < filteredItems.size(); i++) {
                    if ((filteredItems.get(i).getLabel().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(filteredItems.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filteredItems.size();
                results.values = filteredItems;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            items = (List) results.values;
            notifyDataSetChanged();
        }

    }
}
