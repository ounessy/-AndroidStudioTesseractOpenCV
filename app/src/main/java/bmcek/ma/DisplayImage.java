package bmcek.ma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Objects;

public class DisplayImage extends AppCompatActivity {

    private TessBaseAPI tessBaseAPI;
    private static final String TAG = MainActivity.class.getSimpleName();

    ImageView imageView;

    ImageView v1;
    ImageView v2;
    ImageView v3;


    TextView info1;
    TextView info2;
    TextView info3;

    int[][] coords = {{0,100,150,50},{0,150,150,40},{420,280,100,40}};

    Button sendSMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        OpenCVLoader.initDebug();
        TextView[] infos = initiateInfos();

        ImageView[] vs = initiateViews();

        imageView = findViewById(R.id.imageView);


        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        //Bitmap bitmap1 = preProcessImage(bitmap);

        Bitmap resized = resizeImage(bitmap);
        imageView.setImageBitmap(resized);


        int n = coords.length;

        for (int i = 0; i < n; i++ ){
            int [] coord = coords[i];
            TextView info = infos[i];

            ImageView v = vs[i];

            preprocessAndExtract(resized, coord, info, v);


        }

        sendSMS  = findViewById(R.id.sendSMS);

        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

    }

    private void preprocessAndExtract(Bitmap reszized_btm, int[] coord, TextView info, ImageView v){
        Bitmap source = reszized_btm;

        Bitmap cropped = Bitmap.createBitmap(source, coord[0], coord[1], coord[2], coord[3]);

        Bitmap processed = preProcessImage(cropped);

        //Set cropped image
        v.setImageBitmap(processed);

        //Extract text
        String text = getText(processed);

        //Set text
        info.setText(text);

    }

    private  TextView[] initiateInfos(){
        info1 = findViewById(R.id.info1);
        info2 = findViewById(R.id.info2);
        info3 = findViewById(R.id.info3);
        return new TextView[]{info1, info2, info3};
    }

    private ImageView[] initiateViews(){
        v1 = findViewById(R.id.imginfo1);
        v2 = findViewById(R.id.imginfo2);
        v3 = findViewById(R.id.imginfo3);

        return new ImageView[] {v1, v2, v3};
    }

    // Extract text
    private String getText(Bitmap bitmap){
        try{

            tessBaseAPI = new TessBaseAPI();
        }catch (Exception e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        String dataPath = Objects.requireNonNull(getExternalFilesDir("/")).getPath() + "/";
        tessBaseAPI.init(dataPath, "fra");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        }catch (Exception e){
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        tessBaseAPI.end();
        return retStr;
    }


    // Image preprocessing
    public Bitmap  preProcessImage(Bitmap img){
        Mat source = new Mat();
        Mat processed = new Mat();

        int width = img.getWidth();
        int height = img.getHeight();

        Bitmap processedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        //Bitmap to map
        Utils.bitmapToMat(img, source);

        //Processing

        Imgproc.cvtColor(source, processed, Imgproc.COLOR_RGB2GRAY);

        //Imgproc.threshold(processed, processed, 140, 255,Imgproc.THRESH_BINARY);

        // Mat to Bitmap
        Utils.matToBitmap(processed, processedBitmap);


        return processedBitmap;
    }

    public static Bitmap RotateBitmap(Bitmap source)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap resizeImage(Bitmap btm){
        Bitmap source = btm;

        int ratio = 5;

        int w = btm.getWidth()/ratio;
        int h = btm.getHeight()/ratio;

        //Bitmap resized = Bitmap.createBitmap(source, 0, 0, 100, 100);

        Bitmap resized = Bitmap.createScaledBitmap(btm, w, h, false);

        return RotateBitmap(resized);
    }

    public Bitmap cropArea(Bitmap btm){
        Bitmap source = btm;


        return Bitmap.createBitmap(source, 1, 150, 150, 30);
    }

    private void sendSMS(){



        String num = "0679391211";
        String code = "2684";

        String token = "92D2wjL3kyoDTRfXOCYUk94YO";

        //String url = "https://bulksms.ma/developer/sms/send" + "token="+ token + "tel=" + "0624157731"  + "message=BMCE Capital vous remercie pour votre confiance. Votre code d'activation est : " + code;

        String url = "https://bulksms.ma/developer/sms/send?token=" + token + "&tel=" + num + "&message=BMCE Capital vous remercie pour votre confiance. Votre code d'activation est : " + code;

        Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();


        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);


        Intent intent = new Intent(this, confirmation.class);
        intent.putExtra("code", code);
        startActivity(intent);


    }


}
