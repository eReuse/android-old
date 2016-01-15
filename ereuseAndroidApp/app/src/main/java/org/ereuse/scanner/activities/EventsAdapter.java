package org.ereuse.scanner.activities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.Event;

import java.util.Collection;

/**
 * Created by Jamgo SCCL.
 */
public class EventsAdapter extends ArrayAdapter<Event> {

    private static LayoutInflater inflater;

    public EventsAdapter(Activity activity) {
        super(activity, R.layout.event_list_item);
        EventsAdapter.inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void addAll(Collection<? extends Event> collection) {
        for (Event event : collection) {
            this.add(event);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = this.getItem(position);

        View rowView = convertView;
        if (rowView == null) {
            rowView = EventsAdapter.inflater.inflate(R.layout.event_list_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.type = (TextView) rowView.findViewById(R.id.eventTypeTextView);
            viewHolder.devicesSize = (TextView) rowView.findViewById(R.id.eventDevicesSizeTextView);
            viewHolder.created = (TextView) rowView.findViewById(R.id.eventCreatedTextView);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.type.setText(event.getType());
        holder.devicesSize.setText(this.getContext().getText(R.string.event_devices_size) + ": " + Integer.toString(event.getDevicesSize()));
        holder.created.setText(event.getCreated());

        return rowView;
    }

    private static class ViewHolder {
        public TextView type;
        public TextView devicesSize;
        public TextView created;
    }

}
