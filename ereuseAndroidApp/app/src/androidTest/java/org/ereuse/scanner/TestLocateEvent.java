package org.ereuse.scanner;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.ereuse.scanner.activities.FormActivity;
import org.ereuse.scanner.activities.LocateTestActivity;
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
public class TestLocateEvent {

    @Rule
    public ActivityTestRule<LocateTestActivity> mActivityRule = new ActivityTestRule<LocateTestActivity>(LocateTestActivity.class, true, true);

    @Before
    public void setUserData() {

        User testUser = new User();
        testUser.email = GlobalTestParams.DEMO_USER_EMAIL;
        testUser.password = GlobalTestParams.DEMO_USER_PASSWORD;
        testUser.token = GlobalTestParams.DEMO_USER_TOKEN;
        testUser.databases = GlobalTestParams.DATABASES;
        testUser.role = User.ADMIN;
        mActivityRule.getActivity().getScannerApplication().setUser(testUser);
    }

    @Test
    public void testLocateEvent() {

        Intent formIntent = new Intent();
        formIntent.putExtra(FormActivity.EXTRA_MODE, FormActivity.MODE_LOCATE);
        mActivityRule.launchActivity(formIntent);

        onView(withId(R.id.titleEditText)).perform(typeText("This is a LOCATION test"), closeSoftKeyboard());
        onView(withId(R.id.commentsEditText)).perform(typeText("Everything OK"), closeSoftKeyboard());

        onView(withId(R.id.scrollFormView)).perform(swipeUp());
        onView(withId(R.id.formSendButton)).perform(click());
      }
}

