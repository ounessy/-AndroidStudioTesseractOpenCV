package bmcek.ma;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class confirmation extends AppCompatActivity {

    Button validate;

    EditText codevalidation;

    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        code = getIntent().getStringExtra("code");

        validate = findViewById(R.id.validate);

        codevalidation = findViewById(R.id.codevalidation);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String insCode = codevalidation.getText().toString();

                if(code == insCode){
                    Toast.makeText(getApplicationContext(), "Inscription réussite", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Code érronné", Toast.LENGTH_LONG).show();
                }
            }
        });
    }




}
