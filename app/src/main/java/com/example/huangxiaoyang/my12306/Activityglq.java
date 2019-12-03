package com.example.huangxiaoyang.my12306;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangXiaoyang on 2018/08/19.
 */

public class Activityglq {
    public static List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void stopAll(){
        for(Activity activity:activities){
            activity.finish();
        }
    }
}
