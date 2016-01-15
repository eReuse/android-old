package org.ereuse.scanner.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RobotoTextView extends TextView {

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(typeface);
    }

}
