package cn.guolf.android.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

/**
 * Author：guolf on 9/19/15 10:59
 * Email ：guo@guolingfa.cn
 */
public class BaseActivity extends Activity {
    protected Context context;


    protected void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);


        context = getApplicationContext();
    }

}
