package com.xingdhl.www.storehelper.CustomStuff;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.xingdhl.www.storehelper.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Strong on 17/10/21.
 *
 */

public class DatePickerFragment extends DialogFragment {
    private DatePicker mDatePicker;
    private String mTitle;

    public static DatePickerFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        mTitle = getArguments().getString("title");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View viewDatePicker = LayoutInflater.from(getActivity()).inflate(R.layout.diaglog_date, null);

        mDatePicker = (DatePicker)viewDatePicker.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear() - 1900;
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        //Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, new Date(year, month, day));
                    }
                })
                .setTitle(mTitle)
                .setView(viewDatePicker)
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if( getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("date_picker", date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
