package org.ereuse.scanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.Event;
import org.ereuse.scanner.services.AsyncService;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.EventsResponse;

/**
 * Created by Jamgo SCCL.
 */
public class EventsActivity extends AsyncActivity implements AdapterView.OnItemClickListener {

    private EventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        this.adapter = new EventsAdapter(this);
        ListView eventsListView = (ListView) this.findViewById(R.id.eventsListView);
        eventsListView.setAdapter(this.adapter);
        eventsListView.setOnItemClickListener(this);

        new AsyncService(this).doEvents(this.getServer(), this.getUser());

        setToolbar();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkLogin();
    }
    @Override
    public void onSuccess(ApiResponse response) {
        super.onSuccess(response);

        EventsResponse eventsResponse = (EventsResponse) response;
        this.adapter.addAll(eventsResponse.getEvents());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        Event selectedEvent = this.adapter.getItem(position);

        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT, selectedEvent);
        this.startActivity(intent);
    }
}
