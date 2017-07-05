package org.ereuse.scanner;

import android.content.Intent;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.ereuse.scanner.activities.FormGenericActivity;
import org.ereuse.scanner.activities.GenericEventTestActivity;
import org.ereuse.scanner.activities.SnapshotActivity;
import org.ereuse.scanner.activities.SnapshotTestActivity;
import org.ereuse.scanner.data.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestSnapshotEvent {

    @Rule
    public ActivityTestRule<SnapshotTestActivity> mActivityRule = new ActivityTestRule<SnapshotTestActivity>(SnapshotTestActivity.class, true, true);

    @Before
    public void setUserData() {

        User testUser = new User();
        testUser.email = GlobalTestParams.DEMO_USER_EMAIL;
        testUser.password = GlobalTestParams.DEMO_USER_PASSWORD;
        testUser.token = GlobalTestParams.DEMO_USER_TOKEN;
        testUser.databases = Arrays.asList("db1");
        mActivityRule.getActivity().getScannerApplication().setUser(testUser);
    }

    @Test
    public void testSnapshotEvent() {

        Intent formIntent = new Intent();
        formIntent.putExtra(FormGenericActivity.EXTRA_MODE, SnapshotActivity.MODE_EXTERNAL_DEVICE);
        mActivityRule.launchActivity(formIntent);

        onView(withId(R.id.snapshotModelEditText)).perform(typeText("Aquaris_A4.5"), pressBack());
        onView(withId(R.id.snapshotSerialNumberEditText)).perform(typeText("AC011540"), pressBack());

        onView(withId(R.id.scrollSnapshotView)).perform(swipeUp());
        onView(withId(R.id.sendSnapshotButton)).perform(click());
      }
}

