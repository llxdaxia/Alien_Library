package cn.alien95.alien95library.app;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

import cn.alien95.set.BuildConfig;
import cn.alien95.set.entrance.AlienSet;
import cn.alien95.set.http.request.HttpRequest;

/**
 * Created by linlongxin on 2015/12/28.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        if (BuildConfig.DEBUG) {
            HttpRequest.setDebug(true, "NetWork");
        }
        AlienSet.init(this);
    }
}
