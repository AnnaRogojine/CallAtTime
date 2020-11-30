package com.example.callattime;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.callattime.utilities.CompareContent;
import static com.example.callattime.utilities.isNull;
import static com.example.callattime.utilities.isNullOrBlank;

public class ContactsAvailability {

    private String Name;
    private int PhoneNumber;
    private Calendar CalendarAvailableFrom;
    private Calendar CalendarAvailableTo;
    private Calendar CallAt;


    public ContactsAvailability(String name, int phoneNumber, Calendar calendarAvailableFrom, Calendar calendarAvailableTo) {
        this.Name = name;
        this.PhoneNumber = phoneNumber;
        this.CalendarAvailableFrom = calendarAvailableFrom;
        this.CalendarAvailableTo = calendarAvailableTo;

    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getName() {
        return Name;
    }

    public void setNumber(int number) {
        this.PhoneNumber = number;
    }

    public int getNumber() {
        return PhoneNumber;
    }


    public void setCalendarAvailableFrom(Calendar calendarAvailableFrom) {
        this.CalendarAvailableFrom = calendarAvailableFrom;
    }

    public Calendar getCalendarAvailableFrom() {
        return CalendarAvailableFrom;
    }

    public void setCalendarAvailableTo(Calendar calendarAvailableTo) {
        this.CalendarAvailableTo = calendarAvailableTo;
    }

    public Calendar getCalendarAvailableTo() {
        return CalendarAvailableTo;
    }


    public String getAvailableTimeString(String caseTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String time = null;
        if (CompareContent(caseTime, "from")) {
            time = format.format(CalendarAvailableFrom.getTime());
        }

        if (CompareContent(caseTime,"to")) {
            time = format.format(CalendarAvailableTo.getTime());
        }
        return isNullOrBlank(time) ? "" : time;
    }

    public void setCallAt(Calendar callAt) {
        this.CallAt = callAt;
    }

    public Calendar getCallAt() {
        return CallAt;
    }

    public String getDisplayString()
    {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String prefix = this.getName()+" is available in "+getAvailableTimeString("from")+"-"+getAvailableTimeString("to");
        String suffix = "";
        if(!isNull(getCallAt())){
            suffix = " \ncall set to "+ format.format(getCallAt().getTime());
        }
        return prefix + suffix;
    }
}


