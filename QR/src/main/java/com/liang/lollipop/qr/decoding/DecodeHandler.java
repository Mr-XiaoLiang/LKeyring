/*
 * Copyright (C) 2010 ZXing authors
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.liang.lollipop.qr.util.LogUtil;

import java.util.Hashtable;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  public static final int DECODE = 12;
  public static final int DECODE_PHOTO = 13;
  public static final int QUIT = 14;
  public static final int DECODE_SUCCEEDED = 15;
  public static final int DECODE_FAILED = 16;

  private final Handler resultHandler;
  private final MultiFormatReader multiFormatReader;
  private final Rect bounds;

  DecodeHandler(Handler resultHandler, Hashtable<DecodeHintType, Object> hints,Rect bounds) {
    multiFormatReader = new MultiFormatReader();
    multiFormatReader.setHints(hints);
    this.resultHandler = resultHandler;
    this.bounds = bounds;
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case DECODE:
        decode((Image) message.obj);
        break;
      case DECODE_PHOTO:
        decodePhoto((Bitmap) message.obj);
        break;
      case QUIT:
        Looper.myLooper().quit();
        break;
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @update Lollipop on 2017-08-25
   * 弃用Camera API,为了兼容Camera2以及解耦
   * 因此，将byte[] data替换为Image data
   * Image对象，包含图像的信息以及图像数据，满足需求
   */
  private void decode(Image image) {
    long start = System.currentTimeMillis();
    Result rawResult = null;

    LuminanceSource source = getDataForImage(image);
    if(source!=null)
      rawResult = decodeQR(source);

    if (rawResult != null) {
      long end = System.currentTimeMillis();
        LogUtil.logD(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
      sendMsg(DECODE_SUCCEEDED,rawResult);
    } else {
      sendMsg(DECODE_FAILED);
    }
  }

  //获取相机返回的图像数据格式，然后判断是否可用以及判断是否进行转码
  private LuminanceSource getDataForImage(Image image){
    //当数据为空或者数据格式不明确的时候，直接返回为空，放弃解析
    if(image==null||image.getPlanes().length<1||image.getFormat()==ImageFormat.UNKNOWN)
      return null;
    Image.Plane[] planes = image.getPlanes();
    //根据数据格式来返回不同的LuminanceSource
      switch (image.getFormat()){
        case ImageFormat.YV12:{//YV12: YYYYYYYY VV UU    =>YUV420P
          //YV12是4:2:0 YCrCb平面格式由WxH xy平面紧随其后(W / 2)x(H / 2)Cr和Cb的planes。
          //因此此格式的返回值planes数量为1，所以可以直接创建一个YUVLuminanceSource，并且传入输数据
          return new PlanarYUVLuminanceSource(
                  planes[0].getBuffer().array(),
                  image.getWidth(),image.getHeight(),
                  bounds.left,bounds.top, bounds.width(),bounds.height(),false);
        }
        case ImageFormat.NV21:{//NV21: YYYYYYYY VUVU     =>YUV420SP
          //YCrCb format used for images, which uses the NV21 encoding format.
          //格式仅仅与YV12在VU上有区别，而二维码解析仅仅取灰度数据，因此，不做处理。
          return new PlanarYUVLuminanceSource(
                  planes[0].getBuffer().array(),
                  image.getWidth(), image.getHeight(),
                  bounds.left,bounds.top, bounds.width(),bounds.height(),false);
        }
        case ImageFormat.YUV_420_888:{//YUV 4:2:0采样，每四个Y共用一组UV分量。
          //<p>The order of planes in the array returned by
          //{@link android.media.Image#getPlanes() Image#getPlanes()} is guaranteed such that
          //plane #0 is always Y, plane #1 is always U (Cb), and plane #2 is always V (Cr).</p>
          //以上内容摘自YUV_420_888原文注释，可以看出，灰度在第一个plane，我们不在意U和V的数据，
          // 所以我们使用第一个plane
          return new PlanarYUVLuminanceSource(
                  planes[0].getBuffer().array(),
                  image.getWidth(),
                  image.getHeight(),
                  bounds.left,bounds.top,
                  bounds.width(),bounds.height(),false);
        }
        case ImageFormat.YUV_422_888:{//YUV 4:2:2采样，每两个Y共用一组UV分量。
          //<p>The order of planes in the array returned by
          //{@link android.media.Image#getPlanes() Image#getPlanes()} is guaranteed such that
          // plane #0 is always Y, plane #1 is always U (Cb), and plane #2 is always V (Cr).</p>
          return new PlanarYUVLuminanceSource(
                  planes[0].getBuffer().array(),
                  image.getWidth(),
                  image.getHeight(),
                  bounds.left,bounds.top,
                  bounds.width(),bounds.height(),false);
        }

        case ImageFormat.YUV_444_888:{//YUV 4:4:4采样，每一个Y对应一组UV分量。
          //<p>The order of planes in the array returned by
          //{@link android.media.Image#getPlanes() Image#getPlanes()} is guaranteed such that
          //plane #0 is always Y, plane #1 is always U (Cb), and plane #2 is always V (Cr).</p>
          return new PlanarYUVLuminanceSource(
                  planes[0].getBuffer().array(),
                  image.getWidth(),
                  image.getHeight(),
                  bounds.left,bounds.top,
                  bounds.width(),bounds.height(),false);
        }
        case ImageFormat.YUY2:{//这是YUV 4:2:2 [Y0 U0 Y1 V0]
          //这仍然是YUV格式，但是储存方式为交叉储存，每个Y使用他左右相邻的U和V
          //因此，当需要获取灰度数据的话，需要对数据进行采集
          byte[] srcPlane = planes[0].getBuffer().array();//原始数据
          byte[] plane = new byte[srcPlane.length/2];//结果数组
          for(int i = 0,count = plane.length;i<count;i++){
            //将原始数组中的数据，隔位取下来，得到新的灰度数组
              plane[i] = srcPlane[i*2];
          }
          return new PlanarYUVLuminanceSource(
                  plane,
                  image.getWidth(),
                  image.getHeight(),
                  bounds.left,bounds.top,
                  bounds.width(),bounds.height(),false);
        }
        case ImageFormat.JPEG:{
          byte[] srcPlane = planes[0].getBuffer().array();
          Bitmap bitmap = BitmapFactory.decodeByteArray(srcPlane,0,srcPlane.length);
          int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
          bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
          return new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),pixels);
        }
        case ImageFormat.FLEX_RGBA_8888:
        case ImageFormat.FLEX_RGB_888:{//
          //<p>The order of planes in the array returned by
          // {@link android.media.Image#getPlanes() Image#getPlanes()} is guaranteed such that
          // plane #0 is always R (red), plane #1 is always G (green), and plane #2 is always B
          // (blue).</p>
          int width = image.getWidth();
          int height = image.getHeight();
          int size = width * height;
          byte[] luminances = new byte[size];
          byte[] red = planes[0].getBuffer().array();
          byte[] green = planes[1].getBuffer().array();
          byte[] blue = planes[2].getBuffer().array();
          for (int offset = 0; offset < size; offset++) {
            int r = red[offset]; // red
            int g2 = green[offset]*2; // 2 * green
            int b = blue[offset]; // blue
            // Calculate green-favouring average cheaply
            luminances[offset] = (byte) ((r + g2 + b) / 4);
          }
          return new PlanarYUVLuminanceSource(luminances,width,height,
                  bounds.left,bounds.top,
                  bounds.width(),bounds.height(),false);
        }
      }
      return null;
  }

  private void decodePhoto(Bitmap bitmap){
    long start = System.currentTimeMillis();
    try{
      if(bitmap==null){
        sendMsg(DECODE_FAILED);
        return;
      }
      int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
      bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
      RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(),bitmap.getHeight(),pixels);
      Result rawResult = decodeQR(source);

      if (rawResult != null) {
        long end = System.currentTimeMillis();
        LogUtil.logD(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());

        sendMsg(DECODE_SUCCEEDED,rawResult);
      } else {
        sendMsg(DECODE_FAILED);
      }
    }catch (Exception e){
      sendMsg(DECODE_FAILED);
    }
  }

  void onBoundsChange(Rect bound){
    bounds.set(bound);
  }

  private void sendMsg(int what){
    Message message = Message.obtain(resultHandler, what);
    message.sendToTarget();
  }

  private void sendMsg(int what,Result rawResult){
    Message message = Message.obtain(resultHandler, what, rawResult);
    message.sendToTarget();
  }

  private Result decodeQR(LuminanceSource source){
    Result rawResult = null;
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
    try {
      rawResult = multiFormatReader.decodeWithState(bitmap);
    } catch (ReaderException re) {
      // continue
    } finally {
      multiFormatReader.reset();
    }
    return rawResult;
  }

}
