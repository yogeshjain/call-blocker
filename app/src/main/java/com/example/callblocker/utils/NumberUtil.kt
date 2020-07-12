package com.example.callblocker.utils

class NumberUtil {

}

fun String.isValidNumber(): Boolean {
    val temp = trim().purify()
    if(temp.length < 10) return false
    if(temp[0] != '+' && !temp[0].isDigit()) {
        return false
    } else if (temp.length > 13) {
        return false
    } else {
        for(i in 1 until temp.length) {
            if(!temp[i].isDigit()) {
                return false
            }
        }
    }
    return true
}

fun String.purify(): String {
    return String(toCharArray().filter { it.isDigit() }.toCharArray())
}