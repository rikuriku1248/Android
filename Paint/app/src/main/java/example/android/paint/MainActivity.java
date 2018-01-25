package example.android.paint;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;


public class MainActivity extends Activity {

    public static PaintView paintView;
    //public static Preview preview;
    public FrameLayout frameLayout;//menuフラグメントが入るViewGroup
    public static View drawView;//描画するview
    public static View drawView1;//preview
    public static Button colorButton;//color
    public static Button styleButton;//style
    public static Button menuButton;//menu
    public static colorFragment colorFragment;
    public static styleFragment styleFragment;
    public static menuFragment menuFragment;
    public static colorPreview colorPreviewFragment;
    public static stylePreview stylePreviewFragment;
    public static Button undoButton;
    public static Button redoButton;
    public static Preference preference;
    public static byte[] b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //描画クラスのid取得
        paintView = (PaintView)findViewById(R.id.view);
/*
        preference = new Preference();
        SharedPreferences pref = getSharedPreferences("a",Context.MODE_PRIVATE);
        String s = pref.getString("a","");
        b = Base64.decode(s, Base64.DEFAULT);
*/


        //描画ビューの取得
        drawView = findViewById(R.id.view);
        //drawView1 = findViewById(R.id.view2);

        //カラーフラグメントのインスタンス生成
        colorFragment = new colorFragment();
        final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, colorFragment);
        transaction.hide(colorFragment);
        transaction.commit();

        //スタイルフラグメントのインスタンス生成
        styleFragment = new styleFragment();
        final android.app.FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
        transaction1.add(R.id.fragment_container, styleFragment);
        transaction1.hide(styleFragment);
        transaction1.commit();

        //メニューフラグメントのインスタンス生成
        menuFragment = new menuFragment();
        final android.app.FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
        transaction2.add(R.id.fragment_container, menuFragment);
        transaction2.hide(menuFragment);
        transaction2.commit();

        //カラープレビューフラグメントのインスタンス生成
        colorPreviewFragment = new colorPreview();
        final android.app.FragmentTransaction transaction3 = getFragmentManager().beginTransaction();
        transaction3.add(R.id.fragment_container1, colorPreviewFragment);
        transaction3.hide(colorPreviewFragment);
        transaction3.commit();

        //スタイルプレビューフラグメントのインスタンス生成
        stylePreviewFragment = new stylePreview();
        final android.app.FragmentTransaction transaction4 = getFragmentManager().beginTransaction();
        transaction4.add(R.id.fragment_container2, stylePreviewFragment);
        transaction4.hide(stylePreviewFragment);
        transaction4.commit();

        frameLayout = (FrameLayout)findViewById(R.id.fragment_container);

        //カラーボタンの処理
        colorButton = (Button)findViewById(R.id.ColorButton);
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.show(colorFragment);
                transaction.show(colorPreviewFragment);
                transaction.commit();

                colorPreview.colorPreview.drawColorPreviewFlag = true;

                colorButton.setVisibility(View.INVISIBLE);
                styleButton.setVisibility(View.INVISIBLE);
                menuButton.setVisibility(View.INVISIBLE);
                //描画ビューのタッチイベント有効化
                drawView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }
        });

        //スタイルボタンの処理
        styleButton = (Button)findViewById(R.id.StyleButton);
        styleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                transaction1.show(styleFragment);
                transaction1.show(stylePreviewFragment);
                transaction1.commit();

                stylePreview.stylePreview.drawStylePreviewFlag = true;

                colorButton.setVisibility(View.INVISIBLE);
                styleButton.setVisibility(View.INVISIBLE);
                menuButton.setVisibility(View.INVISIBLE);
                //描画ビューのタッチイベント有効化
                drawView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }
        });

        //メニューボタンの処理
        menuButton = (Button)findViewById(R.id.MenuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.show(menuFragment);
                transaction2.commit();
                colorButton.setVisibility(View.INVISIBLE);
                styleButton.setVisibility(View.INVISIBLE);
                menuButton.setVisibility(View.INVISIBLE);
                //描画ビューのタッチイベント有効化
                drawView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return true;
                    }
                });
            }
        });

        //undoボタンの処理
        undoButton = (Button)findViewById(R.id.UndoButton);
        undoButton.setEnabled(false);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.undo();
            }
        });

        //redoボタンの処理
        redoButton = (Button)findViewById(R.id.RedoButton);
        redoButton.setEnabled(false);
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.redo();
            }
        });
    }



    @Override
    protected void onDestroy(){

        //Toast.makeText(getApplicationContext(), "Destroy", Toast.LENGTH_SHORT).show();
        //preference.saveBitmap(getApplicationContext(), paintView.bitmap);
        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        paintView.bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String bitmapStr = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        SharedPreferences pref = getSharedPreferences("a", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("a", bitmapStr);
        editor.commit();
        */
        super.onDestroy();

    }
}
