package com.example.callattime;

import java.util.Calendar;

public class utilities {
    public static boolean isNull(Calendar param) {
        return param == null;
    }
    public static boolean isNullOrBlank(String param) {
        return param == null || param.trim().length() == 0;
    }
    public static boolean isTimeInRange(ContactsAvailability contactsAvailability, Calendar calendarTest) {
        return (calendarTest.after(contactsAvailability.getCalendarAvailableFrom()) && calendarTest.before(contactsAvailability.getCalendarAvailableTo()));
    }
    public static boolean CompareContent(String param1, String param2) {
        return param1.compareToIgnoreCase(param2) == 0;
    }
    public static boolean ContainsContent(String source, String ifIn) {
        return source.toLowerCase().contains(ifIn.toLowerCase());
    }
    public static long getTimeCallInMilli(ContactsAvailability contact) {
        return contact.getCallAt().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

}
