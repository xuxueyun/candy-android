package net.dearcode.candy.controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.forlong401.log.transaction.log.manager.LogManager;

import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.controller.component.DB;
import net.dearcode.candy.controller.component.ServiceBinder;
import net.dearcode.candy.controller.component.UserInfo;
import net.dearcode.candy.controller.service.MessageService;
import net.dearcode.candy.localdb.localpreferences.LocalPreferences;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.model.User;

/**
 * Created by 水寒 on 2016/9/17.
 * 自定义application
 */

public class CustomeApplication extends Application {

    private static CustomeApplication mInstance;
    private LocalPreferences localPreferences = null;
    private static ServiceBinder binder;
    public static DB db;

    private User mMyself = new User();

    private boolean isLogin = false;

    public boolean isLogin() {
        return isLogin;
    }

    public CustomeApplication() {
        mInstance = this;
    }

    public LocalPreferences getLocalPreferences() {
        return localPreferences;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogManager.getManager(this).registerCrashHandler();

        db = new DB(this);

        localPreferences = new LocalPreferences(this);

        String login = localPreferences.get("login");
        if(login == null || "".equals(login)) {
            isLogin = false;
        } else {
            String[] split = login.split("\\#\\@\\#");
            int i = split.length;
            mMyself.setName(split[0]);
            mMyself.setPassword(split[1]);
            mMyself.setID(Long.parseLong(split[2]));
            isLogin = true;
        }
        Intent i = new Intent(this, MessageService.class);
        i.putExtra("type", "1");
        i.putExtra("account", mMyself.getName());
        i.putExtra("passwd", mMyself.getPassword());
        this.startService(i);
    }

    public User getMyself() {
        return mMyself;
    }

    public static CandyMessage getService() {
        return binder.getCandy();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        binder.Disconnect();
        LogManager.getManager(this).unregisterCrashHandler();
    }

    public Context getContext(){
        return mInstance.getContext();
    }

    public static CustomeApplication getInstance(){
        return mInstance;
    }
}
