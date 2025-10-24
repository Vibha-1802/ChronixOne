package com.example.chronixone;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editTextDate;
    Button buttonCalculate;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.editTextDate);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewResult = findViewById(R.id.textViewResult);

        buttonCalculate.setOnClickListener(v -> {
            String input = editTextDate.getText().toString().trim();

            try {
                String[] parts = input.split("/");
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                if (year < 1872 || year > 2025) {
                    textViewResult.setText("Year must be between 1872 and 2025");
                    return;
                }

                if (!isValidDate(day, month, year)) {
                    textViewResult.setText("Invalid date entered!");
                    return;
                }

                int[] next = getNextDate(day, month, year);
                textViewResult.setText("Next Date: " + next[0] + "/" + next[1] + "/" + next[2]);

            } catch (Exception e) {
                textViewResult.setText("Invalid format! Use DD/MM/YYYY");
            }
        });
    }

    private int[] getNextDate(int day, int month, int year) {
        int[] daysInMonth = {31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30,
                31, 31, 30, 31, 30, 31};

        day++;

        if (day > daysInMonth[month - 1]) {
            day = 1;
            month++;
        }

        if (month > 12) {
            month = 1;
            year++;
        }

        return new int[]{day, month, year};
    }

    private boolean isLeapYear(int year) {
        if (year % 400 == 0) return true;
        if (year % 100 == 0) return false;
        return year % 4 == 0;
    }

    private boolean isValidDate(int day, int month, int year) {
        if (month < 1 || month > 12 || day < 1) return false;
        int[] daysInMonth = {31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30,
                31, 31, 30, 31, 30, 31};
        return day <= daysInMonth[month - 1];
    }
}
