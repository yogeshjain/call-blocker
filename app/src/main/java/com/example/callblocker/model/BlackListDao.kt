package com.example.callblocker.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BlackListDao {
    /**
     * Block the number
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun blockNumber(blockedNumber: BlockedNumber)

    /**
     * Block the number with no name
     */
    @Query("INSERT INTO BlockedNumber VALUES(:number,null)")
    fun blockNumber(number: String)

    /**
     * Get all blocked number
     */
    @Query("SELECT * from BlockedNumber ORDER BY name ASC")
    fun getBlacklist() : LiveData<List<BlockedNumber>>

    /**
     * Get all blocked number in a blocking way.
     * Warning: DO NOT USE ON MAIN THREAD
     */
    @Query("SELECT * from BlockedNumber ORDER BY name ASC")
    fun getBlacklistSync() : List<BlockedNumber>

    /**
     * Unblock a number
     */
    @Delete()
    fun deleteNumber(blockedNumber: BlockedNumber)

    /**
     * Unblock all numbers
     */
    @Query("DELETE FROM BlockedNumber")
    fun deleteAll()
}