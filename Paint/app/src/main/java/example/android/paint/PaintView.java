package example.android.paint;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.content.Context.CAMERA_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by riku on 2017/08/16.
 */

public class PaintView extends View {

    private Canvas canvas;
    public Bitmap bitmap;
    Bitmap mutableBitmap;
    Bitmap mutableBitmap1;
    boolean mutableBitmapFlag = false;

    int w;
    int h;

    public ImageView imageview;
    public Bitmap cameraBitmap;

    public Context context;

    //Save用のキャンバスとビットマップ
    private Canvas canvas_save;
    private Bitmap bitmap_save;

    private Paint paint;//描いている途中のPaint
    private Paint paint1;//リストに保存するPaint
    private Paint paintPoint;//点を描くためのPaint
    private Path path = null;//描いている途中のPath
    private Path path1 = null;//リストに保存するPath

    //Pointの位置,色などをリストにして保存
    private ArrayList<Float> drawPointX_list = new ArrayList<Float>();
    private ArrayList<Float> drawPointY_list = new ArrayList<Float>();
    private ArrayList<Paint> paintPoint_list = new ArrayList<Paint>();

    //RedoButtonクリック時に使用
    private ArrayList<Float> drawPointX_list_redo = new ArrayList<Float>();
    private ArrayList<Float> drawPointY_list_redo = new ArrayList<Float>();
    private ArrayList<Paint> paintPoint_list_redo = new ArrayList<Paint>();

    //Path,色などをリストにして保存
    private ArrayList<Path> draw_list = new ArrayList<Path>();
    private ArrayList<Paint> paint_list = new ArrayList<Paint>();

    //RedoButtonクリック時に使用
    private ArrayList<Path> draw_list_redo = new ArrayList<Path>();
    private ArrayList<Paint> paint_list_redo = new ArrayList<Paint>();

    //α,r,g,b値を格納
    public static int α = 255;
    public static int r, g, b = 0;
    public static int size = 10;

    float x1 = 0;
    float y1 = 0;

    private int redoCount;
    private boolean redoFlag = false;

    private int count = 64;//戻れる回数

    public boolean eraserFlag = false;

    //コンストラクタ
    public  PaintView(Context context){
        this(context, null);
    }

    //コンストラクタ
    public PaintView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        paint = new Paint();
        paint1 = new Paint();
        paintPoint = new Paint();
        checkColor();
        path = new Path();
        path1 = new Path();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        //paint.setStrokeWidth(10);

        this.context = context;
    }


    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight){
        super.onSizeChanged(width,height,oldWidth,oldHeight);
        //ビットマップ,キャンバスの作成
        bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mutableBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mutableBitmap1 = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Resources res = context.getResources();
        BitmapFactory.Options options = new  BitmapFactory.Options();
        options.inMutable = true;

        canvas = new Canvas(bitmap);

        canvas.drawColor(Color.WHITE);

        if(cameraBitmap != null) {
            bitmap = cameraBitmap;
            mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            w = mutableBitmap.getWidth();
            h = mutableBitmap.getHeight();
            //canvas = new Canvas(mutableBitmap);
            Matrix mat = new Matrix();
            mat.postRotate(90);
            //mat.postScale(100,100);
            mutableBitmapFlag = true;
            mutableBitmap1 = Bitmap.createBitmap(mutableBitmap, 0, 0, w, h, mat, true);
            //canvas = new Canvas(mutableBitmap1);
            //canvas.rotate(180);
            //canvas.setBitmap(mutableBitmap1);
        }

        bitmap_save = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
    }

    public void setBitmap(Bitmap bitmap){
        cameraBitmap = bitmap;
        if(cameraBitmap != null) {
            this.bitmap = cameraBitmap;
            mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            w = mutableBitmap.getWidth();
            h = mutableBitmap.getHeight();
            //canvas = new Canvas(mutableBitmap);
            Matrix mat = new Matrix();
            //mat.postRotate(90, w/2, h/2);
            //mat.postTranslate(100,100);
            mutableBitmapFlag = true;
            mutableBitmap1 = Bitmap.createBitmap(mutableBitmap, 0, 0, w, h, mat, true);
            //canvas = new Canvas(mutableBitmap1);

            //canvas.rotate(180);
            //canvas.drawBitmap(mutableBitmap1,0,0,null);
            //imageview.setImageBitmap(mutableBitmap1);
        }
    }

    //描画するメソッド
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap,0,0,null);
        //canvas.drawColor(Color.WHITE);
        if(mutableBitmapFlag) {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(mutableBitmap1, 0, 0, null);
        }
        //Pointの描画
        for (int i = 0; i < drawPointX_list.size(); i++) {
            if(paint_list.size() > i) {
                canvas.drawPoint(drawPointX_list.get(i), drawPointY_list.get(i), paintPoint_list.get(i));
            }
        }

        //Pathの描画
        for (int i = 0; i < draw_list.size(); i++) {
            canvas.drawPath(draw_list.get(i), paint_list.get(i));
        }

        ///Pathの描画(current)
        if(path != null){
            canvas.drawPath(path, paint);

        }
    }

    //イベント処理
    @Override
    public boolean onTouchEvent(MotionEvent event){

        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN://指を置いたとき
                x1 = event.getX();
                y1 = event.getY();
                //インスタンスの生成
                path1 = new Path();
                paint1 = new Paint();
                paintPoint = new Paint();
                //paint1の設定
                paint1.setStyle(Paint.Style.STROKE);
                paint1.setStrokeJoin(Paint.Join.ROUND);
                paint1.setStrokeCap(Paint.Cap.ROUND);
                paint1.setStrokeWidth(10);
                //paintPointの設定
                paintPoint.setStyle(Paint.Style.STROKE);
                paintPoint.setStrokeJoin(Paint.Join.ROUND);
                paintPoint.setStrokeCap(Paint.Cap.ROUND);
                paintPoint.setStrokeWidth(10);
                checkColor();
                checkSize();
                checkEraser();
                //リストに追加
                paint_list.add(paint1);
                paintPoint_list.add(paintPoint);
                path.moveTo(x, y);
                path1.moveTo(x, y);
                drawPointX_list.add(x);
                drawPointY_list.add(y);
                //描いている最中はRedo,Undoボタンを無効化
                MainActivity.redoButton.setEnabled(false);
                MainActivity.undoButton.setEnabled(false);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE://指を動かしたとき
                path.lineTo(x, y);
                path1.lineTo(x, y);

                if(x1 != event.getX() && y1 != event.getY()){
                    paintPoint.setARGB(0, 0, 0, 0);
                    paintPoint_list.set(paintPoint_list.size()-1,paintPoint);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP://指を離したとき

                draw_list.add(path1);
                if(draw_list.size() > count){
                    canvas.drawPoint(drawPointX_list.get(0), drawPointY_list.get(0), paintPoint_list.get(0));
                    //canvas_save.drawPoint(drawPointX_list.get(0), drawPointY_list.get(0), paintPoint_list.get(0));

                    canvas.drawPath(draw_list.get(0), paint_list.get(0));
                    //canvas_save.drawPath(draw_list.get(0), paint_list.get(0));

                    drawPointX_list.remove(0);
                    drawPointY_list.remove(0);
                    paintPoint_list.remove(0);
                    draw_list.remove(0);
                    paint_list.remove(0);

                }

                path.reset();
                draw_list_redo.clear();
                paint_list_redo.clear();
                drawPointX_list_redo.clear();
                drawPointY_list_redo.clear();
                MainActivity.undoButton.setEnabled(true);

                invalidate();
                break;
        }
        return true;
    }

    //画面をクリアする
    public void clear(){
        MainActivity.undoButton.setEnabled(false);
        MainActivity.redoButton.setEnabled(false);
        //各リストをリセット
        draw_list.clear();
        paint_list.clear();
        drawPointX_list.clear();
        drawPointY_list.clear();
        paintPoint_list.clear();
        paintPoint_list_redo.clear();
        canvas.drawColor(Color.WHITE);
        invalidate();
    }

    public void undo() {

        //Pointの処理
        if(paintPoint_list.size() != 0) {
            drawPointX_list_redo.add(drawPointX_list.get(drawPointX_list.size() - 1));
            drawPointY_list_redo.add(drawPointY_list.get(drawPointY_list.size() - 1));
            drawPointX_list.remove(drawPointX_list.size() - 1);
            drawPointY_list.remove(drawPointY_list.size() - 1);

            paintPoint_list_redo.add(paintPoint_list.get(paintPoint_list.size() - 1));
            paintPoint_list.remove(paintPoint_list.size() - 1);
            redoFlag = false;
        }

        //Pathの処理
        if (paint_list.size() != 0) {

            draw_list_redo.add(draw_list.get(draw_list.size() - 1));
            paint_list_redo.add(paint_list.get(paint_list.size() - 1));
            draw_list.remove(draw_list.size() - 1);
            paint_list.remove(paint_list.size() - 1);
            redoFlag = false;
        }

        if (draw_list.size() == 0) {
            MainActivity.undoButton.setEnabled(false);
        }

        if (draw_list_redo.size() != 0) {
            MainActivity.redoButton.setEnabled(true);
        }

        invalidate();
    }

    public void redo() {
        if(redoFlag == false) {
            redoCount = draw_list_redo.size()-1;
            redoFlag = true;
        }
        //Pointの処理
        if(drawPointX_list_redo.size() != redoCount && paintPoint_list_redo.size() != redoCount){
            drawPointX_list.add(drawPointX_list_redo.get(redoCount));
            drawPointY_list.add(drawPointY_list_redo.get(redoCount));
            paintPoint_list.add(paintPoint_list_redo.get(redoCount));

            drawPointX_list_redo.remove(drawPointX_list_redo.get(redoCount));
            drawPointY_list_redo.remove(drawPointY_list_redo.get(redoCount));
            paintPoint_list_redo.remove(paintPoint_list_redo.get(redoCount));
        }

        //Pathの処理
        if (draw_list_redo.size() != redoCount && paint_list_redo.size() != redoCount){
            draw_list.add(draw_list_redo.get(redoCount));
            paint_list.add(paint_list_redo.get(redoCount));

            draw_list_redo.remove(draw_list_redo.get(redoCount));
            paint_list_redo.remove(paint_list_redo.get(redoCount));
        }

        redoCount--;

        if(draw_list.size() != 0){
            MainActivity.undoButton.setEnabled(true);
        }

        if(draw_list_redo.size() == 0){
            MainActivity.redoButton.setEnabled(false);
        }

        invalidate();
    }

    //透明度、RED、GREEN、BLUEをセットする
    public void setColor(int α, int r, int g, int b){
        this.α = α;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setSize(int size){
        this.size = size;
    }

    private void checkColor() {
        paint.setARGB(α, r, g, b);
        paint1.setARGB(α, r, g, b);
        paintPoint.setARGB(α, r, g, b);
    }

    private void checkSize(){
        paint.setStrokeWidth(size);
        paint1.setStrokeWidth(size);
        paintPoint.setStrokeWidth(size);
    }

    private void checkEraser(){
        if(eraserFlag) {
            paint.setARGB(255, 255, 255, 255);
            paint1.setARGB(255, 255, 255, 255);
            paintPoint.setARGB(255, 255, 255, 255);
        }
    }

    //ファイルへの保存処理
    public void saveFile(){
        if(sdcardWriteReady() == false){
            Toast.makeText(getContext(), "SDカードが認識されません。", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/savePaint/");

        try{
            if(file.exists() == false){
                file.mkdir();
            }
        }catch (SecurityException e){
            Toast.makeText(getContext(), "例外発生1。", Toast.LENGTH_SHORT).show();
        }

        String AttachName = file.getAbsolutePath() + "/";
        AttachName += System.currentTimeMillis()+".png";
        File savaFile = new File(AttachName);
        while (savaFile.exists()){
            AttachName = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";
            savaFile = new File(AttachName);
        }

        try {
            FileOutputStream fos = new FileOutputStream(AttachName);

            //セーブ用キャンバスへ書き込み
            //Save用ビットマップ,キャンバスの作成

            //bitmap_save = bitmap.copy(bitmap.getConfig(), true);
            if(mutableBitmapFlag) {
                bitmap_save = mutableBitmap1.copy(mutableBitmap1.getConfig(), true);
            }else{
                bitmap_save = bitmap.copy(bitmap.getConfig(), true);
                //canvas_save.drawColor(Color.WHITE);
            }
            canvas_save = new Canvas(bitmap_save);
            canvas_save.drawBitmap(bitmap_save, 0, 0, null);
            for (int i = 0; i < drawPointX_list.size(); i++) {
                if(paint_list.size() > i) {
                    canvas_save.drawPoint(drawPointX_list.get(i), drawPointY_list.get(i), paintPoint_list.get(i));
                }
            }

            for (int i = 0; i < draw_list.size(); i++) {
                canvas_save.drawPath(draw_list.get(i), paint_list.get(i));
            }

            bitmap_save.compress(Bitmap.CompressFormat.PNG, 100, fos);
            canvas_save.drawColor(Color.WHITE);
            fos.close();
            registAndroidDB(AttachName);
            Toast.makeText(getContext(), "保存されました。", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "例外発生2。", Toast.LENGTH_SHORT).show();
            Log.e("Hello","例外出力",e);
        }
    }



    //書き込める状態か
    public boolean sdcardWriteReady(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    //保存を反映させる
    public void registAndroidDB(String path) {
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = getContext().getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put("_data", path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }



}
