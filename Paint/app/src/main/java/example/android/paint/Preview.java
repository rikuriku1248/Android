package example.android.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by riku on 2017/09/13.
 */

public class Preview extends View {

    //private Path path;
    private Paint colorPaint;
    private Paint stylePaint;
    private Canvas canvas;
    private Bitmap bitmap;

    //α,r,g,b値を格納
    public static int α = 255;
    public static int r, g, b = 0;
    public static int size = 0;

    public static boolean drawColorPreviewFlag = false;
    public static boolean drawStylePreviewFlag = false;

    public Preview(Context context){
        this(context, null);
    }

    public Preview(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        colorPaint = new Paint();
        stylePaint = new Paint();

        //path = new Path();
        colorPaint.setStyle(Paint.Style.STROKE);
        colorPaint.setStrokeJoin(Paint.Join.ROUND);
        colorPaint.setStrokeCap(Paint.Cap.ROUND);
        colorPaint.setStrokeWidth(200);
        colorPaint.setARGB(α, r, g, b);

        stylePaint.setStyle(Paint.Style.STROKE);
        stylePaint.setStrokeJoin(Paint.Join.ROUND);
        stylePaint.setStrokeCap(Paint.Cap.ROUND);
        stylePaint.setStrokeWidth(200);
        stylePaint.setARGB(255, 0, 0, 0);
        stylePaint.setARGB(α, r, g, b);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
        super.onSizeChanged(width,height,oldWidth,oldHeight);
        //ビットマップ,キャンバスの作成
        bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
    }

    protected void onDraw(Canvas canvas){

        if(drawColorPreviewFlag) {
            colorPaint.setARGB(α, r, g, b);
            canvas.drawPoint(250, 180, colorPaint);
        }

        if(drawStylePreviewFlag) {
            stylePaint.setStrokeWidth(size);
            canvas.drawPoint(250, 180, stylePaint);
        }

    }

    //透明度、RED、GREEN、BLUEをセットする
    public void setColor(int α, int r, int g, int b){
        this.α = α;
        this.r = r;
        this.g = g;
        this.b = b;
        invalidate();
    }

    public void setSize(int size){
        this.size = size;
        invalidate();
    }
}
