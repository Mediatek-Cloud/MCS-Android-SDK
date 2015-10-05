package com.mediatek.mcstutorial;

import android.app.Application;
import com.mediatek.mcs.Mcs;
import com.mediatek.mcs.domain.McsPushInstallation;
import com.mediatek.mcs.push.PushService;

public class TutorialApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    /**
     * Required: Initialize Mcs SDK with your
     * - Application context
     * - CLIENT_ID
     * - (optional) isDebuggable
     */
    Mcs.initialize(this, "YOUR_CLIENT_ID");

    /**
     * Optional: Enable debug messages on console.
     * Default value is true.
     */
    Mcs.setDebuggable(BuildConfig.DEBUG);

    /**
     * Optional: Enable push notification setup with your
     * - GCM SENDER ID
     * - GCM API KEY
     */
    McsPushInstallation.getInstance().registerInBackground(
        "YOUR_GCM_SENDER_ID", "YOUR_GCM_API_KEY"
    );

    /**
     * Optional: Specify an Activity to handle all pushes by default.
     * default is null
     */
    PushService.setDefaultPushCallback(MainActivity.class);
  }
}
