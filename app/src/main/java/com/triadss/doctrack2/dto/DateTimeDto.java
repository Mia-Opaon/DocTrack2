package com.triadss.doctrack2.dto;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeDto {
    public DateTimeDto() {
    }

    private DateDto date;
    private TimeDto time;

    public DateDto getDate() {
        return date;
    }

    public void setDate(DateDto date) {
        this.date = date;
    }

    public TimeDto getTime() {
        return time;
    }

    public void setTime(TimeDto time) {
        this.time = time;
    }

    public Timestamp ToTimestamp() {
        Date extractedDate = new Date(date.getYear() - 1900,
                date.getMonth(), date.getDay(), time.getHour(), time.getMinute());
        return new Timestamp(extractedDate);
    }

    public Timestamp ToTimestampForTimePicker() {
        Date extractedDate = new Date(date.getYear() - 1900,
                date.getMonth() - 1, date.getDay(), time.getHour(), time.getMinute());
        return new Timestamp(extractedDate);
    }

    public String ToString() {
        return date.ToString() + " " + ToAMPMString(time.ToString());
    }

    public String ToAMPMString(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");

            Date date = inputFormat.parse(time);
            return outputFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public DateTimeDto Clone() {
        DateTimeDto newDateTimeDto = new DateTimeDto();
        newDateTimeDto.setTime(time.Clone());
        newDateTimeDto.setDate(date.Clone());
        return newDateTimeDto;
    }

    public static DateTimeDto ToDateTimeDto(Timestamp timestamp) {
        Date date = timestamp.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        DateTimeDto converted = new DateTimeDto();
        converted.setDate(new DateDto(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)));
        converted.setTime(new TimeDto(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));

        return converted;
    }

    public Timestamp ReschedToTimestamp() {
        return new Timestamp(
                new Date(date.getYear() - 1900, date.getMonth() - 1, date.getDay(), time.getHour(), time.getMinute()));
    }

    public static DateTimeDto ToDateTimeDto(LocalDateTime localDateTime) {
        DateTimeDto converted = new DateTimeDto();
        converted.setDate(
                new DateDto(localDateTime.getYear(),
                        localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth()));
        converted.setTime(new TimeDto(localDateTime.getHour(), localDateTime.getMinute()));
        return converted;
    }

    public static Timestamp GetCurrentTimeStamp() {
        Timestamp currentTimeStamp = GetCurrentDateTimeDto().ToTimestamp();

        return currentTimeStamp;
    }

    public static DateTimeDto GetCurrentDateTimeDto() {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeDto output = DateTimeDto.ToDateTimeDto(currentDate);
        String test = output.ToString();
        return output;
    }

    public static long GetTimestampDiffInSeconds(Timestamp futureTime) {
        long secondDiff = futureTime.getSeconds() - Timestamp.now().getSeconds();

        return secondDiff;
    }

    public static int ComputeAge(Timestamp birthday) {
        LocalDate birthdate = birthday.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        Period period = Period.between(birthdate, now);
        return period.getYears();
    }

    public String formatDateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDay(), time.getHour(), time.getMinute());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.ENGLISH);

        String formattedDateTime = dateFormat.format(calendar.getTime());

        return formattedDateTime;
    }

    public String formatDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDay());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);

        String formattedDate = dateFormat.format(calendar.getTime());

        return formattedDate;
    }

    public static boolean isToday(Timestamp timestamp) {
        LocalDate currentDate = LocalDate.now();
        LocalDate appointmentDate = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return currentDate.equals(appointmentDate);
    }

    public static boolean isCurrentTimeInRange(Timestamp currentTimeStamp, Timestamp appointmentTimeStamp) {
        int currentHour = currentTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).getHour();
        int appointmentHour = appointmentTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).getHour();

        return currentHour == appointmentHour || currentHour == (appointmentHour + 1);
    }

}
