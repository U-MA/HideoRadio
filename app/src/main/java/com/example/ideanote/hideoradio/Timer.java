package com.example.ideanote.hideoradio;

import android.os.Handler;
import android.os.Message;

/**
 * Timer
 * 一定時間ごとに設定されているコールバックを呼び出す
 */
public class Timer extends Handler {

    private static final int INTERVAL_MILLIS = 1000;

    private TimerCallback timerCallback;
    private boolean isUpdate;
    private long startMillis;

    public Timer(TimerCallback timerCallback) {
        this.timerCallback = timerCallback;
    }

    /**
     * 定期実行開始
     */
    public void start() {
        startMillis = System.currentTimeMillis();
        isUpdate = true;
        handleMessage(new Message());
    }

    /**
     * 定期実行停止
     */
    public void stop() {
        isUpdate = false;
    }

    /**
     * INTERVAL_MILLISミリ秒ごとにコールバックを呼び出す
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        removeMessages(0);
        if (isUpdate) {
            timerCallback.onTick(System.currentTimeMillis() - startMillis);
            sendMessageDelayed(obtainMessage(0), INTERVAL_MILLIS);
        }
    }

    public interface TimerCallback {
        void onTick(long millis);
    }
}
