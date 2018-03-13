package debug;

import android.content.Intent;
import android.view.View;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.scanner.R;
import com.winsion.component.scanner.activity.CaptureActivity;

import static com.winsion.component.scanner.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

/**
 * Created by 10295 on 2018/3/13.
 */

public class MainActivity extends BaseActivity {
    @Override
    protected int setContentView() {
        return R.layout.scanner_activity_main;
    }

    @Override
    protected void start() {

    }

    public void toCaptureActivity(View view) {
        startActivityForResult(CaptureActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DEFAULT_REQUEST_CODE) {
            String dataStr = data.getStringExtra(INTENT_EXTRA_KEY_QR_SCAN);
            showToast("扫描成功:" + dataStr);
        }
    }
}
