package com.xingdhl.www.storehelper.trading;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.xingdhl.www.storehelper.R;

import java.util.Calendar;
import java.util.Locale;

public class CalenderActivity extends AppCompatActivity {
    private DatePicker mStart;
    private DatePicker mEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        mStart = (DatePicker)findViewById(R.id.calendar_start);
        mEnd = (DatePicker)findViewById(R.id.calendar_end);
        calendar.setTimeInMillis(getIntent().getLongExtra("start_time", System.currentTimeMillis()));
        mStart.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        calendar.setTimeInMillis(getIntent().getLongExtra("end_time", System.currentTimeMillis()));
        mEnd.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.set(mStart.getYear(), mStart.getMonth(), mStart.getDayOfMonth(), 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                intent.putExtra("start_time", calendar.getTimeInMillis());
                calendar.set(mEnd.getYear(), mEnd.getMonth(), mEnd.getDayOfMonth(), 23, 59, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                intent.putExtra("end_time", calendar.getTimeInMillis());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
