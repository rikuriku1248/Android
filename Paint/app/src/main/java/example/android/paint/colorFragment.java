package example.android.paint;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by riku on 2017/08/17.
 */

public class colorFragment extends Fragment implements SeekBar.OnSeekBarChangeListener{

    public SeekBar seekBar1, seekBar2, seekBar3, seekBar4;
    public TextView textView1, textView2, textView3, textView4;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.color_fragment, null);
        ImageView ColorCloseButton = (ImageView) view.findViewById(R.id.ColorCloseButton);
        ColorCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(MainActivity.colorFragment);
                transaction.hide(MainActivity.colorPreviewFragment);
                transaction.commit();

                colorPreview.colorPreview.drawColorPreviewFlag = false;

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


        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

        if(seekBar == seekBar1){
            textView1.setText(String.format("R:%d", progress));
        }else if(seekBar == seekBar2){
            textView2.setText(String.format("G:%d", progress));
        }else if(seekBar == seekBar3){
            textView3.setText(String.format("B:%d", progress));
        }else if(seekBar == seekBar4){
            textView4.setText(String.format("α:%d", progress));
        }
        MainActivity.paintView.setColor(seekBar4.getProgress(),seekBar1.getProgress(),seekBar2.getProgress(),seekBar3.getProgress());
        colorPreview.colorPreview.setColor(seekBar4.getProgress(),seekBar1.getProgress(),seekBar2.getProgress(),seekBar3.getProgress());
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

        seekBar1 = (SeekBar)view.findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar)view.findViewById(R.id.seekBar2);
        seekBar3 = (SeekBar)view.findViewById(R.id.seekBar3);
        seekBar4 = (SeekBar)view.findViewById(R.id.seekBar4);
        textView1 = (TextView)view.findViewById(R.id.textView1);
        textView2 = (TextView)view.findViewById(R.id.textView2);
        textView3 = (TextView)view.findViewById(R.id.textView3);
        textView4 = (TextView)view.findViewById(R.id.textView4);
        textView4.setText(String.format("α:%d", seekBar4.getProgress()));
        seekBar1.setOnSeekBarChangeListener(this);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar3.setOnSeekBarChangeListener(this);
        seekBar4.setOnSeekBarChangeListener(this);

        MainActivity.paintView.setColor(seekBar4.getProgress(),seekBar1.getProgress(),seekBar2.getProgress(),seekBar3.getProgress());
        //colorPreview.colorPreview.setColor(seekBar4.getProgress(),seekBar1.getProgress(),seekBar2.getProgress(),seekBar3.getProgress());
    }
}
