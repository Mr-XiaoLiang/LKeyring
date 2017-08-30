/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liang.lollipop.qr.decoding;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;


/**
 * This thread does all the heavy lifting of decoding the images.
 * 解码线程
 */
final class DecodeThread extends Thread {

  private final Hashtable<DecodeHintType, Object> hints;
  private DecodeHandler handler;
  private Handler resultHanlder;
  private Rect bounds;
  private final CountDownLatch handlerInitLatch;

  DecodeThread(Handler resultHanlder,
               ResultPointCallback resultPointCallback,
               Rect bounds) {

    this.resultHanlder = resultHanlder;

    this.bounds = bounds;

    handlerInitLatch = new CountDownLatch(1);

    hints = new Hashtable<>(3);

    Vector<BarcodeFormat> decodeFormats = new Vector<>();
    decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
    decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
    decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);

    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(resultHanlder, hints,bounds);
    handlerInitLatch.countDown();
    Looper.loop();
  }

  void onBoundsChange(Rect bound){
    handler.onBoundsChange(bound);
  }

}
