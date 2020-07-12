package com.example.callblocker.model

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.BlockedNumberContract.BlockedNumbers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Repo to save and retrive block list
 */
class BlockRepo @Inject constructor(val application: Application) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var blackListDao: BlackListDao?

    init {
        val db = BlackListDatabase.getDatabase(application)
        blackListDao = db?.blackListDao()

        val prefs = application.getSharedPreferences("blockList", Context.MODE_PRIVATE)
        if (prefs.getBoolean("is_first_launch_done", true)) {
            launch {
                getSystemBlockList(application) {
                    prefs.edit().putBoolean("is_first_launch_done", true).apply()
                }
            }
        }
    }

    private suspend fun getSystemBlockList(application: Application, onDone: () -> Unit) {
        /*withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val c = application.contentResolver.query(
                    BlockedNumbers.CONTENT_URI, arrayOf(
                        BlockedNumbers.COLUMN_ID,
                        BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                        BlockedNumbers.COLUMN_E164_NUMBER
                    ), null, null, null
                )

                // If the cursor returned is valid, get the phone number
                while (c != null && c.moveToNext()) {
                    val numberIndex = c.getColumnIndex(BlockedNumbers.COLUMN_ORIGINAL_NUMBER)
                    val number = c.getString(numberIndex)

                    setBlockedNumber(number, false)
                }
                c?.close()
            }

            onDone.invoke()
        }*/
    }

    fun getBlackList() = blackListDao?.getBlacklist()

    fun getBlackListSync() = blackListDao?.getBlacklistSync()

    fun blockNumber(number: String) {
        launch { setBlockedNumber(number, true) }
    }

    fun blockNumber(blockedNumber: BlockedNumber) {
        launch { setBlockedNumber(blockedNumber, true) }
    }

    fun unblockNumber(blockedNumber: BlockedNumber) {
        launch { setUnblockedNumber(blockedNumber) }
    }

    private suspend fun setBlockedNumber(number: String, shouldBlockTelephony: Boolean) {
        withContext(Dispatchers.IO) {
            blackListDao?.blockNumber(number)
            if(shouldBlockTelephony) {
                block(number)
            }
        }
    }

    private suspend fun setBlockedNumber(blockedNumber: BlockedNumber, shouldBlockTelephony: Boolean) {
        withContext(Dispatchers.IO) {
            blackListDao?.blockNumber(blockedNumber)
            if(shouldBlockTelephony) {
                block(blockedNumber.number)
            }
        }
    }

    private suspend fun setUnblockedNumber(unblockNumber: BlockedNumber) {
        withContext(Dispatchers.IO) {
            blackListDao?.deleteNumber(unblockNumber)
            unblock(unblockNumber.number)
        }
    }

    private fun block(number: String) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val values = ContentValues()
            values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
            val uri = application.contentResolver.insert(BlockedNumbers.CONTENT_URI, values)
        }*/
    }

    private fun unblock(number: String) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val values = ContentValues()
            values.put(BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
            val uri = application.contentResolver.insert(BlockedNumbers.CONTENT_URI, values)
            uri?.let { application.contentResolver.delete(uri, null, null) }
        }*/
    }
}
