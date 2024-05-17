package com.triadss.doctrack2.utils;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.triadss.doctrack2.dto.TimeDto;
import com.triadss.doctrack2.repoositories.ConstantRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppointmentFunctions {
    public static boolean IsValidHour(int hour) {
        return hour < 8 || hour > 17;
    }

    public static boolean IsValidHour(TimeDto time) {
        return IsValidHour(time.getHour());
    }

    public static void FetchHolidays(Map<String, Timestamp> holidayList) {
        String TAG = "FETCH HOLIDAYS";

        ConstantRepository.getHolidays(new ConstantRepository.HolidayFetchCallback() {
            @Override
            public void onHolidaysFetched(Map<String, Timestamp> holidays) {
                holidayList.putAll(holidays);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, errorMessage);
            }
        });
    }
}