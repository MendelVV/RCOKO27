package ru.mendel.apps.rcoko27

import org.junit.Assert
import org.junit.Test
import ru.mendel.apps.rcoko27.data.EventData

class DataUnitTest {

    @Test
    fun dateConvertTest() {
        var date = "2019-12-10"
        Assert.assertEquals("10.12.2019", EventData.convertDate(date))

        date = "2019-12-10 10:10:10"
        Assert.assertEquals("10.12.2019", EventData.convertDate(date))

    }
}