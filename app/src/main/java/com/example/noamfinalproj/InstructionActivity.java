package com.example.noamfinalproj;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noamfinalproj.gemini.GeminiCallback;
import com.example.noamfinalproj.gemini.GeminiManager;


public class InstructionActivity extends AppCompatActivity {

    String TAG = "InstructionActivity";
    private EditText etQuestion;
    private Button btnAsk;
    private TextView tvAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        /// //////////////////////////////////////////////////////////////
        //  add
        //  implementation("com.google.ai.client.generativeai:generativeai:0.8.0")
        // to build.gradle.kts
        //
        //https://aistudio.google.com/app/apikey
        /// //////////////////////////////////////////////////////////////

        etQuestion = findViewById(R.id.etQuestion);
        tvAnswer = findViewById(R.id.tvAnswer);
        btnAsk = findViewById(R.id.btnAsk);
        btnAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String q = etQuestion.getText().toString();
                if(q.equals(""))
                    q = "מה השם הכי נפוץ בישראל";

                String prompt = q + "תשובה עד 30 מילים בעברית";
                //String prompt = "What is the capital of France?";
                GeminiManager.getInstance().sendMessage(prompt, new GeminiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() ->
                                {
                                    tvAnswer.setText(response);
                                }
                        );
                    }

/*                    @Override
                    public void onError(Throwable e) {
                        //runOnUiThread(() ->System.out.println("שגיאה: " + e.getMessage()));
                        runOnUiThread(() ->Log.e(TAG, "שגיאה: " + e.getMessage()));
                        //Toast.makeText(MainActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();


                    }*/

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Gemini error", e); // prints full stack trace, not just message
                        tvAnswer.setText("Error");
                        Toast.makeText(InstructionActivity.this,
                                "Error: " + e.getClass().getName() + " / " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onError(Exception e) {
                        //runOnUiThread(() -> System.out.println("שגיאה: " + e.getMessage()));
                        runOnUiThread(() ->Log.e(TAG, "שגיאה: " + e.getMessage()));
                        tvAnswer.setText("Error");
                        //Toast.makeText(InstructionActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(InstructionActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });
    }
}