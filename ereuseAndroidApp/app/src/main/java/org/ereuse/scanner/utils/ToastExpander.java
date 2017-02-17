package org.ereuse.scanner.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

/**
 * Created by Jamgo SCCL on 16/02/17.
 */

public class ToastExpander {

    public static void showFor(Context context, String message, int duration) {
        final Toast tag = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        tag.show();

        new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
                tag.show();
            }

            public void onFinish() {
                tag.show();
            }

        }.start();
    }
}
