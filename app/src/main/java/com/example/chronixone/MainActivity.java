package com.example.chronixone;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText editTextDate;
    Button buttonCalculate;
    TextView textViewResult;

    Spinner spinnerDay, spinnerMonth, spinnerYear;

    private static final int MIN_YEAR = 1872;
    private static final int MAX_YEAR = 2040;

    // flags to avoid feedback loops
    private boolean updatingFromSpinners = false;
    private boolean updatingFromText = false;

    private static final Pattern FULL_DATE = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate = findViewById(R.id.editTextDate);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewResult = findViewById(R.id.textViewResult);

        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);

        // Allow only digits and '/' in the EditText, but DO NOT auto-format.
        editTextDate.setFilters(new InputFilter[]{
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
                        if (!(Character.isDigit(c) || c == '/')) {
                            return "";
                        }
                    }
                    return null; // keep input
                }
        });

        setupSpinners();
        wireSyncBetweenTextAndSpinners();

        buttonCalculate.setOnClickListener(v -> {
            String input = editTextDate.getText().toString().trim();

            try {
                // ensure full dd/MM/yyyy before parsing (prevents partials)
                if (!FULL_DATE.matcher(input).matches()) {
                    textViewResult.setText("Invalid format! Use DD/MM/YYYY");
                    return;
                }

                String[] parts = input.split("/");
                int day   = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year  = Integer.parseInt(parts[2]);

                if (year < MIN_YEAR || year > MAX_YEAR) {
                    textViewResult.setText("Year must be between " + MIN_YEAR + " and " + MAX_YEAR);
                    return;
                }
                if (!isValidDate(day, month, year)) {
                    textViewResult.setText("Invalid date entered!");
                    return;
                }

                int[] next = getNextDate(day, month, year);

                // Use them AFTER they exist
                String inputDow = dayOfWeek(day, month, year);
                String nextDow  = dayOfWeek(next[0], next[1], next[2]);

                // NEW: leap-year message for the OUTPUT year (next[2])
                String leapMsg = getYearType(next[2]);

                textViewResult.setText(
                        "Next Date: " + String.format("%02d/%02d/%04d", next[0], next[1], next[2]) +
                                " (" + nextDow + ")" + "\n" +
                                String.format("%02d/%02d/%04d", day, month, year) + " is a (" + inputDow + ")" +
                                "\n" + leapMsg
                );

            } catch (Exception e) {
                textViewResult.setText("Invalid format! Use DD/MM/YYYY");
            }
        });


    }

    /* ---------- Spinners setup & sync ---------- */
    private String dayOfWeek(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar is 0-based for month
        cal.set(Calendar.DAY_OF_MONTH, day);
        return new SimpleDateFormat("EEE", Locale.getDefault()).format(cal.getTime());
    }

    private void setupSpinners() {
        // Month: 1..12
        List<Integer> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) months.add(i);
        spinnerMonth.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, months));

        // Year: 1872..2025
        List<Integer> years = new ArrayList<>();
        for (int y = MIN_YEAR; y <= MAX_YEAR; y++) years.add(y);
        spinnerYear.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, years));

        // Day: depends on month/year; initialize to 1..31
        updateDayAdapter(1, MIN_YEAR);

        // Listeners
        spinnerMonth.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> {
            int m = (int) spinnerMonth.getSelectedItem();
            int y = (int) spinnerYear.getSelectedItem();
            updateDayAdapter(m, y);
            writeSpinnersToEditText(); // reflect in text (only after selection)
        }));
        spinnerYear.setOnItemSelectedListener(new SimpleItemSelectedListener(() -> {
            int m = (int) spinnerMonth.getSelectedItem();
            int y = (int) spinnerYear.getSelectedItem();
            updateDayAdapter(m, y);
            writeSpinnersToEditText();
        }));
        spinnerDay.setOnItemSelectedListener(new SimpleItemSelectedListener(this::writeSpinnersToEditText));

        // Defaults
        spinnerMonth.setSelection(0); // 1
        spinnerYear.setSelection(0);  // 1872
        spinnerDay.setSelection(0);   // 1
        writeSpinnersToEditText();
    }

    private void updateDayAdapter(int month, int year) {
        int max = daysInMonth(month, year);
        int current = spinnerDay.getSelectedItem() == null ? 1 : (int) spinnerDay.getSelectedItem();

        List<Integer> days = new ArrayList<>();
        for (int d = 1; d <= max; d++) days.add(d);

        spinnerDay.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, days));

        int newSel = Math.min(current, max) - 1;
        if (newSel < 0) newSel = 0;
        spinnerDay.setSelection(newSel);
    }

    private void writeSpinnersToEditText() {
        // ⛔ Never rewrite while typing, that causes the "01" + cursor jump
        if (editTextDate.hasFocus()) return;

        updatingFromSpinners = true;
        int d = (int) spinnerDay.getSelectedItem();
        int m = (int) spinnerMonth.getSelectedItem();
        int y = (int) spinnerYear.getSelectedItem();

        String newText = String.format("%02d/%02d/%04d", d, m, y);
        editTextDate.setText(newText);
        // leave cursor as-is (don’t force to end while not focused)
        updatingFromSpinners = false;
    }


    private void wireSyncBetweenTextAndSpinners() {
        editTextDate.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (updatingFromSpinners) return;

                String input = s.toString().trim();
                if (!FULL_DATE.matcher(input).matches()) return; // ← only when 10 chars

                try {
                    String[] parts = input.split("/");
                    int d = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    if (y < MIN_YEAR || y > MAX_YEAR) return;
                    if (d < 1 || m < 1 || m > 12 || d > daysInMonth(m, y)) return;

                    updatingFromText = true;
                    spinnerYear.setSelection(y - MIN_YEAR);
                    spinnerMonth.setSelection(m - 1);
                    updateDayAdapter(m, y);
                    spinnerDay.setSelection(Math.max(0, d - 1));
                } finally {
                    updatingFromText = false;
                }
            }

        });
    }

    /* ---------- Date utilities (unchanged logic) ---------- */

    private int[] getNextDate(int day, int month, int year) {
        day++;
        int dim = daysInMonth(month, year);
        if (day > dim) {
            day = 1;
            month++;
        }
        if (month > 12) {
            month = 1;
            year++;
        }
        return new int[]{day, month, year};
    }

    private boolean isValidDate(int day, int month, int year) {
        if (month < 1 || month > 12 || day < 1) return false;
        return day <= daysInMonth(month, year);
    }

    private int daysInMonth(int month, int year) {
        switch (month) {
            case 2:  return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11: return 30;
            default: return 31;
        }
    }

    private boolean isLeapYear(int year) {
        if (year % 400 == 0) return true;
        if (year % 100 == 0) return false;
        return year % 4 == 0;
    }
    private String getYearType(int year) {
        boolean leap = isLeapYear(year);
        boolean century = (year % 100 == 0);

        if (leap && century)
            return year + " is both a Leap Year and a Century Year.";
        else if (leap)
            return year + " is a Leap Year.";
        else if (century)
            return year + " is a Century Year but not a Leap Year.";
        else
            return year + " is neither a Leap Year nor a Century Year.";
    }
    private static class SimpleItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        private final Runnable onSelect;
        SimpleItemSelectedListener(Runnable onSelect) { this.onSelect = onSelect; }
        @Override public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) { onSelect.run(); }
        @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
    }
}
