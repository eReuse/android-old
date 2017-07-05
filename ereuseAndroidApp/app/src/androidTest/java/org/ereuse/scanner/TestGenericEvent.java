package org.ereuse.scanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.ereuse.scanner.activities.FormActivity;
import org.ereuse.scanner.activities.FormGenericActivity;
import org.ereuse.scanner.activities.GenericEventTestActivity;
import org.ereuse.scanner.activities.LoginTestActivity;
import org.ereuse.scanner.data.User;
import org.ereuse.scanner.services.api.ActionResponse;
import org.ereuse.scanner.services.api.ApiResponse;
import org.ereuse.scanner.services.api.LoginResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Context.MODE_PRIVATE;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.ereuse.scanner.activities.BaseActivity.SHARED_PREFERENCES_NAME;

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

        onView(withId(R.id.titleEditText)).perform(typeText("this is a READY EVENT test"), pressBack());
        onView(withId(R.id.genericFormSendButton)).perform(click());
      }

}

