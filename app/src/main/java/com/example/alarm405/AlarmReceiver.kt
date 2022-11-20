package com.example.alarm405

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.*


class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    var sCpuWakeLock = PowerManager.PARTIAL_WAKE_LOCK

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("onReceive", "수신 받음!!!!!!!!!!!!!!!")
//        val intent1 = Intent(context, MainActivity::class.java)
//        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        //context.startActivity(intent1)
//        context.startService(intent1)



        val intent2 = Intent(context, MainActivity2::class.java)
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //context.startForegroundService(intent2)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent2)
        } else {
            startActivity(context, intent2, null)
        }



        createNotificationChannel(context)
        notifyNotification(context)
        Toast.makeText(context, "good morning", Toast.LENGTH_SHORT).show()


        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val effect = VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mp = MediaPlayer.create(context, R.raw.wake_up_song)
        //mp.start()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 호환성 고려
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun notifyNotification(context: Context) {
        val tapResultIntent = Intent(context, MainActivity::class.java)
            .apply {
            // action = "fullscreen_activity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapResultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        // 여기서부터 추가한 내용
        val fullscreenIntent = Intent(context, MainActivity2::class.java).apply {
            action = "fullscreen_activity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullscreenPendingIntent = PendingIntent.getActivity(context, 0, fullscreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        // 여기까지
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("일어날 시간입니다.")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)        // 이 부분이 fullscreenPendingIntent가 아니여도 되는가?
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)    // 추가
                .setPriority(NotificationCompat.PRIORITY_MAX)          // High에서 MAX로 변경
                .setFullScreenIntent(fullscreenPendingIntent, true)
            notify(NOTIFICATION_ID, build.build())
        }


        val intent1 = Intent(context, MainActivity::class.java)
        intent1.putExtra("change", true)
    }
//    private fun turnScreenOnAndKeyguardOff(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//            set
//            setTurnScreenOn(true)
//            window.addFlags(
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
//        } else {
//            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED    // deprecated api 27
//                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD     // deprecated api 26
//                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON   // deprecated api 27
//                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
//        }
//        val keyguardMgr = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            keyguardMgr.requestDismissKeyguard(this, null)
//        }
//    }
}