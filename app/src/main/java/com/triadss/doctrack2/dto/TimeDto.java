package com.triadss.doctrack2.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeDto {
    public TimeDto(int hour, int minute)
    {
        this.hour = hour;
        this.minute = minute;
    }

    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public TimeDto Clone() {
        return  new TimeDto(hour, minute);
    }

    public String ToString()
    {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    public String ToAMPMString() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");

            Date date = inputFormat.parse(ToString());
            return outputFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }
}
