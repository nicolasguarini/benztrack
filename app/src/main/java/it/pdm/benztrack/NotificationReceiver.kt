package it.pdm.benztrack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import it.pdm.benztrack.data.Utilities
import kotlin.math.roundToInt

class NotificationReceiver : BroadcastReceiver() {
    private val channelId = "BenzTrack Notifications"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = context?.getSharedPreferences("it.pdm.benztrack", Context.MODE_PRIVATE)

        if(sharedPreferences != null && sharedPreferences.getFloat("consumptionPrevMonth", -1f) != -1f){
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val repeatingIntent = Intent(context, DashboardActivity::class.java)
            repeatingIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = channelId
                val mChannel = NotificationChannel(
                    channelId,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                mChannel.description = "This is default channel used for all other notifications"
                notificationManager.createNotificationChannel(mChannel)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                100,
                repeatingIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val spentThisMonth = sharedPreferences.getFloat("spentThisMonth", -1f)
            val emittedThisMonth = sharedPreferences.getFloat("emittedThisMonth", -1f)
            val consumptionThisMonth = sharedPreferences.getFloat("consumptionThisMonth", -1f)
            val consumptionPrevMonth = sharedPreferences.getFloat("consumptionPrevMonth", -1f)

            val monthYear = Utilities.getThisMonthYear()
            val deltaPercent = (getDeltaPercent(consumptionPrevMonth, consumptionThisMonth) * 10.0).roundToInt() / 10.0
            val contentTitle = context.getString(R.string.consumptions_update) + monthYear
            var contentText =
                context.getString(R.string.spent_this_month) + spentThisMonth + context.getString(R.string.emitting) + emittedThisMonth + context.getString(
                                    R.string.with_medium_consumption) + consumptionThisMonth + " " + context.getString(R.string.km_l) + ", "
            contentText += if(deltaPercent < 0f){
                "-$deltaPercent" + context.getString(R.string.doing_great)
            }else{
                "+$deltaPercent" + context.getString(R.string.can_do_better)
            }

            val builder = NotificationCompat.Builder(context, channelId)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                .setAutoCancel(true)

            notificationManager.notify(100, builder.build())
        }
    }

    private fun getDeltaPercent(consumption1: Float, consumption2: Float): Float{
        return if(consumption1 > consumption2){
            val delta = consumption1 - consumption2
            -(delta / consumption1 * 100)
        }else {
            val delta = consumption2 - consumption1
            delta / consumption2 * 100
        }
    }
}
