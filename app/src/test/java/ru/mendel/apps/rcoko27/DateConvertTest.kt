package ru.mendel.apps.rcoko27

import org.junit.Assert
import org.junit.Test
import ru.mendel.apps.rcoko27.data.EventData
import ru.mendel.apps.rcoko27.data.MessageData

class DateConvertTest {

    @Test
    fun dateConvertTest() {
        var date = "2019-12-10"
        Assert.assertEquals("10.12.2019", EventData.convertDate(date))

        date = "2019-12-10 10:10:10"
        Assert.assertEquals("10.12.2019", EventData.convertDate(date))

    }

    @Test
    fun dateMessageConvert(){
        val date = MessageData.convertDate("2019-10-10 10:10:00","+0230")
        assert(date=="17:40:00 10.10.2019")
    }
}