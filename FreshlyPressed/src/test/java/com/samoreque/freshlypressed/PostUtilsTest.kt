package com.samoreque.freshlypressed

import java.util.Date

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class PostUtilsTest {
    private val currentDay = 1597410033000
    @Before
    fun setup() {

    }
    @Test
    fun printsDateCorrectly() {
        val result = PostUtils.printDate(Date(1230000000000))

        assertThat(result).isEqualTo("Dec 23, 2008")
    }

    @Test
    fun `Should be a new day when the dates representation have different date values`() {
        val differentDay = Date(1597327237000)
        val date = Date(currentDay)
        val result = PostUtils.isSameDateRepresentation(date, differentDay)
        assertThat(result).isEqualTo(false)
    }

    @Test
    fun `Should be a similar day when the dates representation have the same date values`() {
        val differentDay = Date(currentDay + 15)
        val date = Date(currentDay)
        val result = PostUtils.isSameDateRepresentation(date, differentDay)
        assertThat(result).isEqualTo(true)
    }
}