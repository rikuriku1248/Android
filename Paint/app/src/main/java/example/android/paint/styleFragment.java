package example.android.paint;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by riku on 2017/09/13.
 */

public class styleFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{

    public SeekBar sizeSeekBar;
    public TextView sizeText;
    private boolean eraserFlag = false;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.style_fragment, null);
        ImageView StyleCloseButton = (ImageView) view.findViewById(R.id.StyleCloseButton);
        StyleCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(MainActivity.styleFragment);
                transaction.hide(MainActivity.stylePreviewFragment);
                transaction.commit();

                previewFragment.stylePreview.drawStylePreviewFlag = false;

                MainActivity.colorButton.setVisibility(View.VISIBLE);
                MainActivity.styleButton.setVisibility(View.VISIBLE);
                MainActivity.menuButton.setVisibility(View.VISIBLE);
                //描画ビューのタッチイベント無効化
                MainActivity.drawView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
        });

        final ImageView eraserButton = (ImageView) view.findViewById(R.id.eraserButton);
        eraserButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eraserFlag == true){
                    MainActivity.paintView.eraserFlag = true;
                    eraserButton.setColorFilter(0x88000000);
                    eraserFlag = false;
                }else{
                    MainActivity.paintView.eraserFlag = false;
                    eraserButton.setColorFilter(0x00000000);
                    eraserFlag = true;
                }
            }
        });
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

        sizeText.setText(String.format("Size:%d", progress));


        MainActivity.paintView.setSize(sizeSeekBar.getProgress());
        stylePreview.stylePreview.setSize(sizeSeekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#ffa0a0a0"));

        sizeSeekBar = (SeekBar)view.findViewById(R.id.sizeSeekBar);
        sizeText = (TextView)view.findViewById(R.id.sizeText);
        sizeSeekBar.setOnSeekBarChangeListener(this);
        sizeText.setText(String.format("Size:%d", sizeSeekBar.getProgress()));

        MainActivity.paintView.setSize(sizeSeekBar.getProgress());
        //stylePreview.stylePreview.setSize(sizeSeekBar.getProgress());
    }
}

