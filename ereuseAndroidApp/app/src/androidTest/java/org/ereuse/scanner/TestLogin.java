package org.ereuse.scanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.ereuse.scanner.activities.LoginTestActivity;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.ereuse.scanner.activities.BaseActivity.SHARED_PREFERENCES_NAME;

/**
 * Created by Jamgo SCCL on 4/07/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestLogin {

    private static final String DEMO_USER_EMAIL = "a@a.a";
    private static final String DEMO_USER_PASSWORD = "1234";

    @Rule
    public ActivityTestRule<LoginTestActivity> mActivityRule = new ActivityTestRule<>(LoginTestActivity.class);

    /**
     * This method uses {@link LoginTestActivity} and does few things:
     *
     * 1. Writes "UserLogin" into login field
     * 2. Writes "UserPassword" into password field
     * 3. Presses "Perform login" button
     *
     * 4. Waits for {@link org.ereuse.scanner.activities.LoginTestActivity.LoginTestCallback#onHandleResponseCalled(LoginResponse loginResponse)}
     * and validates that user is logged in successfully.
     *
     * NOTE: It makes synchronization on local variable but in our case it doesn't matter.
     *
     * @throws Exception
     */
    @Before
    public void setUserAndPassword() {

        Context context = getInstrumentation().getTargetContext().getApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user",DEMO_USER_EMAIL);
            editor.putString("password", DEMO_USER_PASSWORD);
            editor.commit();
    }

    @Test
    public void testLoginAction() {

        Intent formIntent = new Intent();
        mActivityRule.launchActivity(formIntent);
        onView(withId(R.id.loginButton)).perform(click());
      }

}

