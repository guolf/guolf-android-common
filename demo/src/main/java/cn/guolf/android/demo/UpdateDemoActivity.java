package cn.guolf.android.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import cn.guolf.android.common.updatehelper.AbsUpdateInfoParser;
import cn.guolf.android.common.updatehelper.UpdateHelper;
import cn.guolf.android.common.updatehelper.UpdateInfoBean;
import cn.guolf.android.common.updatehelper.UpdateListener;
import cn.guolf.android.common.util.log.LogUtils;

public class UpdateDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_demo);
        UpdateHelper helper = new UpdateHelper(this, new AbsUpdateInfoParser() {
            @Override
            public UpdateInfoBean parse(String info) {
                return null;
            }
        }, new UpdateListener.NormalUpdateListener() {
            @Override
            public void onCancel() {
                LogUtils.i("onCancel");
            }

            @Override
            public void onCheckStart() {
                LogUtils.i("onCheckStart");
            }

            @Override
            public void onDownloadStart() {
                LogUtils.i("onDownloadStart");
            }

            @Override
            public void onCheckFinish() {
                LogUtils.i("onCheckFinish");
            }
        });
        helper.check("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
