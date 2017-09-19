package com.rinc.imsys;

import android.app.Activity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhouzhi on 2017/8/10.
 */

public class ActivityCollector {

    public static List<Activity> activities = new CopyOnWriteArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
