package com.example.callblocker.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.internal.telephony.ITelephony
import com.example.callblocker.R
import com.example.callblocker.model.BlockRepo
import com.example.callblocker.model.BlockedNumber
import com.example.callblocker.utils.purify
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.managers.BroadcastReceiverComponentManager
import dagger.hilt.internal.UnsafeCasts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


const val NOTIF_CHANNEL_ID = "call_blocker"

@AndroidEntryPoint
class PhoneCallReceiver() : BroadcastReceiver() {
    @Inject
    lateinit var blockRepo: BlockRepo

    override fun onReceive(context: Context?, intent: Intent?) {
        //Enforce DI when onReceive is called, otherwise Hilt wont inject
        val injector =
            BroadcastReceiverComponentManager.generatedComponent(context) as PhoneCallReceiver_GeneratedInjector
        injector.injectPhoneCallReceiver(UnsafeCasts.unsafeCast(this))

        try {
            val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
            CoroutineScope(Dispatchers.IO).launch {
                //Get block list
                val blockList = blockRepo.getBlackListSync()

                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING, ignoreCase = true)) {
                    //Get number
                    val number =
                        intent?.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)

                    if (number != null && number.length > 9) {
                        //check if blocked
                        if (isNumberblockList(number, blockList)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                //On Android P, use new method as iTelephony is depricated and will throw exception
                                val telecomManager =
                                    context?.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                                telecomManager.endCall()
                            } else {
                                //Before P, use iTelephony to disconnect call
                                val tm =
                                    context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                                val clazz = Class.forName(tm.javaClass.getName())
                                val method = clazz.getDeclaredMethod("getITelephony")
                                method.isAccessible = true
                                val telephonyService = method.invoke(tm) as ITelephony
                                telephonyService.endCall()
                            }
                            context?.let { showNotification(context, number) }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isNumberblockList(number: String, blockList: List<BlockedNumber>?): Boolean {
        blockList?.forEach {
            if (it.number.purify().endsWith(number.drop(number.length - 10))) {
                return true
            }
        }
        return false
    }

    private fun showNotification(context: Context, number: String) {
        createNotificationChannel(context)
        var builder = NotificationCompat.Builder(context, NOTIF_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Call blocked")
            .setContentText("Incoming call from $number blocked")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(number.drop(number.length - 5).toInt(), builder.build())
        }
    }

    private fun createNotificationChannel(context: Context?) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Call Blocker"
            val descriptionText = "Details of blocked calls"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIF_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}