package com.example.callblocker

import com.example.callblocker.utils.isValidNumber
import com.example.callblocker.utils.purify
import org.junit.Assert
import org.junit.Test

class NumberUtilsTest {
    @Test
    fun testPurify() {
        Assert.assertEquals("1234567890", "1234567890".purify())
        Assert.assertEquals("1234567890", "      1234567890".purify())
        Assert.assertEquals("1234567890", "1234567890      ".purify())
        Assert.assertEquals("1234567890", "    1234567890     ".purify())
        Assert.assertEquals("1234567890", "1 2 3 4 5 6 7 8 9 0".purify())
        Assert.assertEquals("1234567890", "123 4567    890".purify())
        Assert.assertEquals("1234567890", "(1234567890)".purify())
        Assert.assertEquals("1234567890", "#%$%^%$$#$1234567%#^%^$%890%^%$^".purify())

        Assert.assertEquals("", "".purify())
        Assert.assertEquals("", "    ".purify())
        Assert.assertEquals("", "@#%@$%$#%^".purify())
        Assert.assertEquals("", "asfajndjfn".purify())
        Assert.assertEquals("", "+#########".purify())
        Assert.assertEquals("0000000000", "(000) 000 0000".purify())
        Assert.assertEquals("911234567890", "+91-1234567890".purify())
        Assert.assertEquals("0222345987", "(022)2345-987".purify())
    }

    @Test
    fun testValidNumber() {
        Assert.assertEquals(true, "1234567890".isValidNumber())
        Assert.assertEquals(true, "+911234567890".isValidNumber())
        Assert.assertEquals(true, "+11234567890".isValidNumber())
        Assert.assertEquals(true, "01234567890".isValidNumber())
        Assert.assertEquals(true, "1111111111".isValidNumber())
        Assert.assertEquals(true, "+651234567890".isValidNumber())
        Assert.assertEquals(true, "12 34 56 78 90".isValidNumber())

        Assert.assertEquals(false, "".isValidNumber())
        Assert.assertEquals(false, "1".isValidNumber())
        Assert.assertEquals(false, " ".isValidNumber())
        Assert.assertEquals(false, "+".isValidNumber())
        Assert.assertEquals(false, "0".isValidNumber())
        Assert.assertEquals(false, "123456789".isValidNumber())
        Assert.assertEquals(false, "12345123451234".isValidNumber())
    }

}