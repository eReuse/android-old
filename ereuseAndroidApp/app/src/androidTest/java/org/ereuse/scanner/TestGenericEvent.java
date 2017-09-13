package org.ereuse.scanner;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.ereuse.scanner.activities.FormGenericActivity;
import org.ereuse.scanner.activities.GenericEventTestActivity;
import org.ereuse.scanner.data.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestGenericEvent {

    @Rule
    public ActivityTestRule<GenericEventTestActivity> mActivityRule = new ActivityTestRule<GenericEventTestActivity>(GenericEventTestActivity.class, true, true);

    @Before
    public void setUserData() {

        User testUser = new User();
        testUser.email = GlobalTestParams.DEMO_USER_EMAIL;
        testUser.password = GlobalTestParams.DEMO_USER_PASSWORD;
        testUser.token = GlobalTestParams.DEMO_USER_TOKEN;
        mActivityRule.getActivity().getScannerApplication().setUser(testUser);
    }

    @Test
    public void testGenericReadyEvent() {

        Intent formIntent = new Intent();
        formIntent.putExtra(FormGenericActivity.EXTRA_MODE, GlobalTestParams.GENERIC_EVENT_TEST_TYPE);
        mActivityRule.launchActivity(formIntent);

        onView(withId(R.id.titleEditText)).perform(typeText("this is a READY EVENT test"), closeSoftKeyboard());
        onView(withId(R.id.genericFormSendButton)).perform(click());
      }

}

