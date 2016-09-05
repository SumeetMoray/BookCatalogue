package com.nearbyshops.communityLibrary.database.Dialogs;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by sumeet on 20/8/16.
 */

public class DateDialogMeetup extends DialogFragment
                        implements DatePickerDialog.OnDateSetListener{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
//        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Create a new instance of DatePickerDialog and return it
        return dialog;
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        if(getActivity() instanceof NotifyDate)
        {
            ((NotifyDate)getActivity()).onDateNotified(year,month,day);
        }

    }


    public interface NotifyDate
    {
        void onDateNotified(int year, int month, int day);
    }

}
