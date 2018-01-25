package example.android.paint;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by riku on 2017/09/12.
 */

public class menuFragment extends Fragment {

    private Uri cameraUri;
    public ImageView imageView;
    private String AttachName;
    public Bitmap bitmap;
    public Bitmap import_bitmap;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.menu_fragment, null);
        ImageView menuCloseButton = (ImageView) view.findViewById(R.id.MenuCloseButton);

        imageView = (ImageView) view.findViewById(R.id.image);

        menuCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.hide(MainActivity.menuFragment);
                transaction.commit();
                MainActivity.menuButton.setVisibility(View.VISIBLE);
                MainActivity.styleButton.setVisibility(View.VISIBLE);
                MainActivity.colorButton.setVisibility(View.VISIBLE);
                //描画ビューのタッチイベント無効化
                MainActivity.drawView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });
            }
        });

        Button clearButton = (Button)view.findViewById(R.id.ClearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.paintView.clear();
            }
        });

        Button saveButton = (Button)view.findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.paintView.saveFile();
            }
        });

        Button importButton = (Button)view.findViewById(R.id.ImportButton);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, 2);
            }
        });

        Button cameraButton = (Button)view.findViewById(R.id.CameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MainActivity.paintView.saveFile();
                cameraSave();

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#ffa0a0a0"));
    }

    public void cameraSave(){
        // 保存先のフォルダーを作成

        File file = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),"IMG");
        file.mkdirs();

        //File file = new File(Environment.getExternalStorageDirectory().getPath()+"/saveCamera/");

        // 保存ファイル名
        AttachName = file.getAbsolutePath() + "/";
        //AttachName += System.currentTimeMillis()+".png";
        AttachName += "camera"+".png";
        File cameraFile = new File(AttachName);
        while (cameraFile.exists()){
            //AttachName = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png";
            cameraFile.delete();
            AttachName = file.getAbsolutePath() + "/" + "camera" + ".png";
            cameraFile = new File(AttachName);
        }

        // capture画像のファイルパス

        //File cameraFile = new File(AttachName);
        cameraUri = FileProvider.getUriForFile(
                MainActivity.paintView.getContext(),
                MainActivity.paintView.getContext().getPackageName() + ".provider",
                cameraFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, 1);
        //savaFile.delete();
        cameraFile.delete();
    }

    @Override
    public void onActivityResult(int requestCode,
                                    int resultCode, Intent intent) {
        if (requestCode == 1) {

            if(cameraUri != null){

                imageView.setImageURI(cameraUri);
                try {
                    InputStream stream = MainActivity.paintView.getContext().getContentResolver().openInputStream(cameraUri);
                    bitmap = BitmapFactory.decodeStream(new BufferedInputStream(stream));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                MainActivity.paintView.setBitmap(bitmap);
                //MainActivity.paintView.registAndroidDB(AttachName);
            }
        }

        if (requestCode == 2
                ) {

            if (intent.getData() != null) {

                ParcelFileDescriptor pfDescriptor = null;
                try {
                    Uri uri = intent.getData();
                    pfDescriptor = MainActivity.paintView.getContext().getContentResolver().openFileDescriptor(uri, "r");
                    if (pfDescriptor != null) {
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        import_bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                        MainActivity.paintView.setBitmap(import_bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
