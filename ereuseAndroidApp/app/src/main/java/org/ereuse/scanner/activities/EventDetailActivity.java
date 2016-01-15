package org.ereuse.scanner.activities;


import android.os.Bundle;
import android.widget.TextView;

import org.ereuse.scanner.R;
import org.ereuse.scanner.data.Event;

/**
 * Created by Jamgo SCCL.
 */
public class EventDetailActivity extends BaseActivity {

    public static final String EXTRA_EVENT = "event";

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        this.event = (Event) this.getIntent().getSerializableExtra(EXTRA_EVENT);

        TextView tv;

        tv = (TextView) this.findViewById(R.id.eventTypeTextView);
        tv.setText(this.event.getType());
        tv.invalidate();

        tv = (TextView) this.findViewById(R.id.eventDevicesSizeTextView);
        tv.setText(Integer.toString(this.event.getDevicesSize()));
        tv.invalidate();

        tv = (TextView) this.findViewById(R.id.eventIncidenceTextView);
        tv.setText(Boolean.toString(this.event.isIncidence()));
        tv.invalidate();

        tv = (TextView) this.findViewById(R.id.eventSecuredTextView);
        tv.setText(Boolean.toString(this.event.isSecured()));
        tv.invalidate();

        tv = (TextView) this.findViewById(R.id.eventCreatedTextView);
        tv.setText(this.event.getCreated());
        tv.invalidate();

        tv = (TextView) this.findViewById(R.id.eventUpdatedTextView);
        tv.setText(this.event.getUpdated());
        tv.invalidate();

        tv = (TextView) this.findViewById(R.id.eventByUserTextView);
        tv.setText(this.event.getByUser());
        tv.invalidate();

        setToolbar();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkLogin();
    }
}
