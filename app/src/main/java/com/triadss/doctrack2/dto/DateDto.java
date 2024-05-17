package com.triadss.doctrack2.dto;

import android.widget.DatePicker;

import com.google.firebase.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DateDto {
    public DateDto(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private int year;
    private int month;
    private int day;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public DateDto Clone() {
        return new DateDto(year, month, day);
    }

    public String ToString(boolean isMonth0Index)
    {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year,
                month + (isMonth0Index ? 1 : 0)
                , day);
    }

    public String ToString()
    {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year,
                month + 1
                , day);
    }

    public Timestamp ToStartDateTimestamp()
    {
        LocalDate localDate = LocalDate.of(year, month, day);

        // Convert LocalDate to LocalDateTime at the start of the day
        LocalDateTime localDateTime = localDate.atStartOfDay();

        DateTimeDto dateTimeDto = DateTimeDto.ToDateTimeDto(localDateTime);
        return dateTimeDto.ToTimestamp();
    }

    public Timestamp ToEndDateTimestamp()
    {
        LocalDate localDate = LocalDate.of(year, month, day);

        // Convert LocalDate to LocalDateTime at the start of the day
        LocalDateTime localDateTime = localDate.atTime(LocalTime.MAX);

        DateTimeDto dateTimeDto = DateTimeDto.ToDateTimeDto(localDateTime);
        return dateTimeDto.ToTimestamp();
    }

    public static DateDto fromDatePicker(DatePicker datepicker)
    {
        DateDto extractedDate = new DateDto(datepicker.getYear(), datepicker.getMonth() + 1, datepicker.getDayOfMonth());
        return extractedDate;
    }

    public static boolean isDayWeekend(DateDto dateDto){
        return isDayWeekend(dateDto.getYear(), dateDto.getMonth() - 1, dateDto.getDay());
    }

    public static boolean isDayWeekend(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek == Calendar.SUNDAY;
    }

    public static boolean checkDayIfHoliday(Map<String, Timestamp> holidayList, int year, int month, int day) {
        boolean isHoliday = false;

        for (Timestamp holidayTimestamp : holidayList.values()) {
            LocalDate holidayDate = holidayTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            isHoliday = (holidayDate.getMonthValue() == month + 1 && holidayDate.getDayOfMonth() == day);
            if(isHoliday)
            {
                return isHoliday;
            }
        }
        return isHoliday;
    }
}
