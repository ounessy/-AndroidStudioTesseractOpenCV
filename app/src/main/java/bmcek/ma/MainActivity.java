package bmcek.ma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    String currentImagePath = null;
    private int IMAGE_REQUEST = 1;

    public static final String TESS_DATA = "/tessdata";
    private static final String TAG = MainActivity.class.getSimpleName();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //prepareTessData();



    }


    public void captureImage(View view) {
        Intent cameraIntent = new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(cameraIntent.resolveActivity(getPackageManager()) != null){
            File imageFile = null;

            try {
                imageFile =  getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(imageFile != null){
                Uri imageUri = FileProvider.getUriForFile(this, "bmcek.ma.fileprovider", imageFile);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                startActivityForResult(cameraIntent, IMAGE_REQUEST);


            }
        }

    }

    public void DisplayImage(View view) {

        Intent intent = new Intent(this, DisplayImage.class);
        intent.putExtra("image_path", currentImagePath);
        startActivity(intent);

    }


    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);

        currentImagePath = imageFile.getPath();

        return imageFile;
    }

    private void prepareTessData(){
        try{
            File dir = getExternalFilesDir(TESS_DATA);
            if(!dir.exists()){
                if (!dir.mkdir()) {
                    Toast.makeText(getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String[] fileList = getAssets().list("");

            Toast.makeText(getApplicationContext(), "files" + fileList[0], Toast.LENGTH_SHORT).show();

            for(String fileName : fileList){
                String pathToDataFile = dir + "/" + fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
