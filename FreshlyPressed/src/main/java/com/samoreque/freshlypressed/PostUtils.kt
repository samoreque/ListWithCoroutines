package com.samoreque.freshlypressed

import java.text.DateFormat
import java.util.Date
import java.util.TimeZone
import java.util.Locale


object PostUtils {
    private const val GMT_TIMEZONE = "GMT"

    /**
     * Gets the date format defined for whole app
     * It is using the [GMT_TIMEZONE] timezone by default
     */
    fun printDate(date: Date, style: Int = DateFormat.MEDIUM): String {
        val dateFormat = DateFormat.getDateInstance(style, Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone(GMT_TIMEZONE)
        return dateFormat.format(date)
    }

    /**
     * Indicates if the dates are in the same day according to [printDate] representation
     */
    @JvmStatic
    fun isSameDateRepresentation(date: Date, otherDay: Date): Boolean {
        return printDate(date) == printDate(otherDay)
    }
}
