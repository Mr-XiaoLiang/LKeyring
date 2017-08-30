
package com.liang.lollipop.qr.decoding;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.ImageReader;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;
import com.liang.lollipop.qr.util.LogUtil;
import com.liang.lollipop.qr.view.QRFinderView;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class QRReadHandler extends Handler {

  private static final String TAG = QRReadHandler.class.getSimpleName();

  private final DecodeThread decodeThread;
  private State state;
  private OnDecodeSuccessCallBack decodeSuccessCallBack;
  private ImageReader reader;

  private enum State {
    PREVIEW,
    SUCCESS,
    DONE
  }

  public QRReadHandler(QRFinderView qrFinderView,Rect bounds,ImageReader reader,OnDecodeSuccessCallBack callBack) {
    this.reader = reader;
    this.decodeSuccessCallBack = callBack;
    decodeThread = new DecodeThread(this,
        new ViewFinderResultPointCallback(qrFinderView),bounds);
    decodeThread.start();
  }

  public void start(){
      LogUtil.logD("QRReadActivityHandler","onStart------------");
    state = State.SUCCESS;
    restartPreviewAndDecode();
  }

  public void stop(){
    LogUtil.logD("QRReadActivityHandler","onStop------------");
    state = State.DONE;
    //移除所有队列中的消息
    removeMessages(DecodeHandler.DECODE_SUCCEEDED);
    removeMessages(DecodeHandler.DECODE_FAILED);
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case DecodeHandler.DECODE_SUCCEEDED:
          LogUtil.logD(TAG, "Got decode succeeded message");
        state = State.SUCCESS;
        if(decodeSuccessCallBack!=null)
          decodeSuccessCallBack.onDecodeSuccess((Result) message.obj);
        break;
      case DecodeHandler.DECODE_FAILED:
        LogUtil.logD(TAG, "decode failed");
        // We're decoding as fast as possible, so when one decode fails, start another.
        preview();
        break;
    }
  }

  public void preview(){
    state = State.PREVIEW;
    Message message = Message.obtain(decodeThread.getHandler(), DecodeHandler.DECODE);
    message.obj = reader.acquireLatestImage();
    message.sendToTarget();
  }

  public void quitSynchronously() {
      LogUtil.logD("QRReadActivityHandler","onQuit------------");
    state = State.DONE;
    Message quit = Message.obtain(decodeThread.getHandler(), DecodeHandler.QUIT);
    quit.sendToTarget();
    try {
      decodeThread.join();
    } catch (InterruptedException e) {
      // continue
    }
    // Be absolutely sure we don't send any queued up messages
    removeMessages(DecodeHandler.DECODE_SUCCEEDED);
    removeMessages(DecodeHandler.DECODE_FAILED);
  }

  private void restartPreviewAndDecode() {
    if (state == State.SUCCESS) {
      preview();
    }
  }

  public void decodeImage(Bitmap bitmap){
    decodeThread.getHandler().obtainMessage(DecodeHandler.DECODE_PHOTO, bitmap);
  }

  public interface OnDecodeSuccessCallBack{
    void onDecodeSuccess(Result result);
  }

  public void onBoundsChange(Rect bound){
    decodeThread.onBoundsChange(bound);
  }

}
