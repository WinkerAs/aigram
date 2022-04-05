package com.example.aigram;

import android.Manifest;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Runnable, RecyclerViewAdapterStyle.OnNoteListenner{

    private static final int NUM_STYLES = 26;
    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";
    private static final String INPUT_NODE = "input";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";

    private static final int WANTED_WIDTH = 420; //300;
    private static final int WANTED_HEIGHT = 560; //400;

    private ImageView mImageView;
    TextView textView3;
    private Bitmap mTransferredBitmap;

    private TensorFlowInferenceInterface mInferenceInterface;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<Integer> mImages = new ArrayList<>();

    final float[] styleVals = new float[NUM_STYLES];

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            textView3.setText("Style Transfer");
            String text = (String)msg.obj;
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            mImageView.setImageBitmap(mTransferredBitmap);
        } };

    static final int GALLERY_REQUEST = 1;

    RecyclerView recyclerViewCategories;

    ImageButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewCategories = findViewById(R.id.recyclerViewStyle);
        recyclerViewCategories.setHasFixedSize(true);

        initRecyclerView();
        mImageView = findViewById(R.id.imageview);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
                Toast.makeText(getApplicationContext(), "Save image success", Toast.LENGTH_SHORT);
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        textView3 = findViewById(R.id.textView3);

    }

    private void saveImage(){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        FileOutputStream fileOutputStream = null;
        File file = getDisk();

        if(!file.exists() && !file.mkdir()){
            file.mkdirs();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "IMG"+date+".jpg";
        String file_name = file.getAbsolutePath()+"/"+name;
        File new_file = new File(file_name);

        try{

            BitmapDrawable drawable = (BitmapDrawable)mImageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            fileOutputStream = new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            Toast.makeText(getApplicationContext(), "Save image success", Toast.LENGTH_SHORT);
            fileOutputStream.flush();
            fileOutputStream.close();
        }catch (IOException ex){
            ex.getMessage();
        }

        refreshGallery(new_file);
    }

    private File getDisk(){
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(file, "Yes");
    }

    private void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getApplication().sendBroadcast(intent);
    }
    @Override
    public void run() {

    }

    Bitmap bitmap = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);


        ImageView imageView = (ImageView) findViewById(R.id.imageview);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                }
        }
    }

    @Override
    public void onNoteClick(int postition) {
    }

    public void initRecyclerView(){
        MyTask myTask = new MyTask();
        myTask.execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            int[] image = {R.drawable.a1s, R.drawable.a2s, R.drawable.a3s, R.drawable.a4s, R.drawable.a5s, R.drawable.a6s,
                    R.drawable.a7s, R.drawable.a8s, R.drawable.a9s, R.drawable.a10s, R.drawable.a11s, R.drawable.a12s,
                    R.drawable.a13s, R.drawable.a14s, R.drawable.a15s, R.drawable.a16s, R.drawable.a17s, R.drawable.a18s,
                    R.drawable.a19s, R.drawable.a20s, R.drawable.a21s, R.drawable.a22s, R.drawable.a23s, R.drawable.a24s,
                    R.drawable.a25s, R.drawable.a26s};

            for (int i = 0; i < 26; i++){
                    mTitle.add("Стиль "+(i+1));
                    mImages.add(image[i]);
            }

            RecyclerViewAdapterStyle adapterCategories = new RecyclerViewAdapterStyle(MainActivity.this, mTitle, mImages, new RecyclerViewAdapterStyle.OnNoteListenner() {
                @Override
                public void onNoteClick(int postition) {
                    textView3.setText("Processing...");
                    int[] intValues = new int[WANTED_WIDTH * WANTED_HEIGHT];
                    float[] floatValues = new float[WANTED_WIDTH * WANTED_HEIGHT * 3];
                    float[] outputValues = new float[WANTED_WIDTH * WANTED_HEIGHT * 3];

                    try {

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, WANTED_WIDTH, WANTED_HEIGHT, true);
                        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());

                        for (int i = 0; i < intValues.length; ++i) {
                            final int val = intValues[i];
                            floatValues[i * 3 + 0] = ((val >> 16) & 0x00FF);
                            floatValues[i * 3 + 1] = ((val >> 8) & 0x00FF);
                            floatValues[i * 3 + 2] = (val & 0x00FF);
                        }

                        AssetManager assetManager = getAssets();
                        mInferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);

                        // use pre-trained TensorFlow Magenta model
                        for (int i = 0; i < NUM_STYLES; ++i) {
                            styleVals[i] = 0.0f / NUM_STYLES;
                        }
                        styleVals[postition] = 1.0f;
                        //styleVals[19] = 1.0f;
                        //styleVals[4] = 0.5f;
                        mInferenceInterface.feed(INPUT_NODE, floatValues, 1, WANTED_HEIGHT, WANTED_WIDTH, 3);
                        mInferenceInterface.feed("style_num", styleVals, NUM_STYLES);
                        mInferenceInterface.run(new String[] {OUTPUT_NODE}, false);
                        mInferenceInterface.fetch(OUTPUT_NODE, outputValues);
                        for (int i = 0; i < intValues.length; ++i) {
                            intValues[i] = 0xFF000000
                                    | (((int) (outputValues[i * 3] * 21)) << 16)
                                    | (((int) (outputValues[i * 3 + 1] * 255)) << 8)
                                    | ((int) (outputValues[i * 3 + 2] * 255));
                        }


                        Bitmap outputBitmap = scaledBitmap.copy( scaledBitmap.getConfig() , true);
                        outputBitmap.setPixels(intValues, 0, outputBitmap.getWidth(), 0, 0, outputBitmap.getWidth(), outputBitmap.getHeight());
                        mTransferredBitmap = Bitmap.createScaledBitmap(outputBitmap, bitmap.getWidth(), bitmap.getHeight(), true);

                        Message msg = new Message();
                        msg.obj = "Tranfer Processing Done";
                        mHandler.sendMessage(msg);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            recyclerViewCategories.setAdapter(adapterCategories);
            recyclerViewCategories.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

            LayoutAnimationController controller = AnimationUtils
                    .loadLayoutAnimation(MainActivity.this, R.anim.list_layout_controller);
            recyclerViewCategories.setLayoutAnimation(controller);
        }
    }
}