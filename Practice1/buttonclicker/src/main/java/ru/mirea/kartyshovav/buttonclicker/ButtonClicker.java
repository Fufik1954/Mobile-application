package ru.mirea.kartyshovav.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ButtonClicker extends AppCompatActivity {

    private  TextView textViewStudent;
    private  Button btnWhoAmI;
    private Button btnItsNotMe;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

         textViewStudent = findViewById(R.id.tvOut);
         btnWhoAmI = findViewById(R.id.btnWhoAmI);
         btnItsNotMe = findViewById(R.id.btnItsNotMe);
         checkBox = findViewById(R.id.checkBox);

        View.OnClickListener oclBtnWhoAmI = new View.OnClickListener() {
        @Override
            public void onClick(View v) {
            textViewStudent.setText("Мой номер по списку № 9");
            }
        };
        btnWhoAmI.setOnClickListener(oclBtnWhoAmI);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onItsNotMeClick(View view)
    {
        checkBox.setChecked(!checkBox.isChecked());
    }
}