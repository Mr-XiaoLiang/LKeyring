package com.liang.lollipop.qr.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.liang.lollipop.qr.R;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by liuj on 2016/9/27.
 * 二维码识别中的扫描框
 */

public class QRFinderView extends View {

    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 10L;
    /**
     * 扫描框大小
     */
    private Rect frame;
    /**
     * 扫描角边长度
     */
    private int angleLength;
    /**
     * 扫描角边宽度
     */
    private int angleWidth;
    /**
     * 步长
     */
    private float stepLength;
    /**
     * 步数
     */
    private int step = 0;
    /**
     * 扫描框头部
     */
    private int scanTop;
    /**
     * 扫描框左侧
     */
    private int scanLeft;
    /**
     * 扫描框头部
     */
    private int scanRight;
    /**
     * 扫描框左侧
     */
    private int scanBottom;
    /**
     * 扫描框宽度
     */
    private int scanWidth;
    /**
     * 扫描框高度
     */
    private int scanHeight;
    /**
     * 扫描方向
     */
    private boolean scanDirection = true;
    /**
     * 绘制画笔
     */
    private Paint paint;
    /**
     * 绘制的颜色
     */
    private int color = 0;
    /**
     * 黑色画笔
     */
    private Paint blackPaint;
    /**
     * 黑色画笔
     */
    private Paint boxPaint;
    /**
     * 透明色
     */
    private int TRANSPARENT = Color.argb(0,255,255,255);

    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;

    private void init(){
        createPaint();
        if(frame==null){
            initFrame();
        }

        scanWidth = frame.width();
        scanHeight = frame.height();
        scanTop = frame.top;
        scanLeft = frame.left;
        scanRight = frame.right;
        scanBottom = frame.bottom;
        angleLength = (int) (scanWidth*0.1);
        angleWidth = (int) (angleLength*0.1);
        stepLength = scanHeight*0.01f;
        paint.setStrokeWidth(angleWidth);
        LinearGradient linearGradient = new LinearGradient(
                scanLeft,scanTop,scanRight,scanTop,
                new int[]{TRANSPARENT,color,TRANSPARENT},
                null, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
    }

    private void createPaint(){
        if(paint==null){
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setDither(true);
        }
        paint.setShadowLayer(10,0,0,color);
        if(blackPaint==null){
            blackPaint = new Paint();
            blackPaint.setAntiAlias(true);
            blackPaint.setDither(true);
            blackPaint.setColor(Color.BLACK);
            blackPaint.setAlpha(128);
        }
        if(boxPaint==null){
            boxPaint = new Paint();
            boxPaint.setAntiAlias(true);
            boxPaint.setDither(true);
        }
        boxPaint.setColor(color);
        if(possibleResultPoints==null)
            possibleResultPoints = new HashSet<>(5);
    }

    private void initFrame(){
        if(frame==null)
        frame = new Rect(0,0,0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(frame==null){
            init();
        }
        canvas.save();
        canvas.clipRect(frame);
        canvas.drawColor(0x80000000);
        canvas.restore();

        canvas.drawRect(scanLeft,scanTop+stepLength*step,scanRight,scanTop+stepLength*step+angleWidth,paint);//画扫描线

            canvas.drawRect(scanLeft,scanTop,scanLeft+angleLength,scanTop+angleWidth,boxPaint);
            canvas.drawRect(scanLeft,scanTop,scanLeft+angleWidth,scanTop+angleLength,boxPaint);
            canvas.drawRect(scanRight-angleLength,scanTop,scanRight,scanTop+angleWidth,boxPaint);
            canvas.drawRect(scanRight-angleWidth,scanTop,scanRight,scanTop+angleLength,boxPaint);
            canvas.drawRect(scanLeft,scanBottom-angleWidth,scanLeft+angleLength,scanBottom,boxPaint);
            canvas.drawRect(scanLeft,scanBottom-angleLength,scanLeft+angleWidth,scanBottom,boxPaint);
            canvas.drawRect(scanRight-angleWidth,scanBottom-angleLength,scanRight,scanBottom,boxPaint);
            canvas.drawRect(scanRight-angleLength,scanBottom-angleWidth,scanRight,scanBottom,boxPaint);

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<>(5);
                lastPossibleResultPoints = currentPossible;
                for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(scanLeft + point.getX(), scanTop
                                + point.getY(), 6.0f, boxPaint);
                }
            }
            if (currentLast != null) {
                for (ResultPoint point : currentLast) {
                        canvas.drawCircle(scanLeft + point.getX(), scanTop
                                + point.getY(), 3.0f, boxPaint);
                }
            }
            if(scanDirection){
                step++;
            }else{
                step--;
            }
            if(step==100||step==0){
                scanDirection = !scanDirection;
            }
            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, scanLeft, scanTop,
                    scanRight, scanBottom);
    }

    public QRFinderView(Context context) {
        this(context,null);
    }
    public QRFinderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public QRFinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void drawViewfinder() {
        invalidate();
    }

    public void setFrame(Rect frame) {
        this.frame = frame;
        init();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

    public void setColor(int color) {
        this.color = color;
    }
}
