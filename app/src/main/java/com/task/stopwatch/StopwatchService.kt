package com.task.stopwatch

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*

class StopwatchService : Service() {

    private var timeElapsed: Int = 0
    private var isStopWatchRunning = false
    private lateinit var notificationManager: NotificationManager
    private lateinit var updateTimer:Timer
    companion object{
        const val STOPWATCH_ACTION = "STOPWATCH_ACTION"
        const val STOPWATCH_STATUS = "STATUS"
        const val TICK = "TICK"
        const val TIME_ELAPSED = "ELAPSED"
        const val MOVE_TO_FOREGROUND = "MOVE_FOREGROUND"
        const val MOVE_TO_BACKGROUND = "MOVE_BACKGROUND"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        getNotificationManager()
        val action = intent?.getStringExtra(STOPWATCH_ACTION)

        when(action){
            MOVE_TO_FOREGROUND->moveToForeground()
            MOVE_TO_BACKGROUND -> pauseStopWatch()

        }
        return START_STICKY
    }

    private fun pauseStopWatch() {
        updateTimer.cancel()
        isStopWatchRunning = false
        timeElapsed = 0
    }

    private fun moveToForeground() {
        startForeground(1,createNotification())
        updateTimer = Timer()
        updateTimer.scheduleAtFixedRate(object:TimerTask(){
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                isStopWatchRunning = true
                val stopWatchIntent = Intent()
                stopWatchIntent.action = TICK
                timeElapsed++
                stopWatchIntent.putExtra(TIME_ELAPSED,timeElapsed)
                sendBroadcast(stopWatchIntent)
                updateNotification()
            }

        },0,1000)


    }

    private fun updateNotification() {
      notificationManager.notify(1,createNotification())
    }

    private fun createNotification(): Notification? {
        val title = if(isStopWatchRunning){
            "Timer is Running"
        }else{
            "Timer Stopped"
        }

       val totalTime = 900
        val timeLeft = totalTime - timeElapsed
        val minute = (timeLeft)/60
        val second = (timeLeft)%60
        Log.d("Paras","timeleft:"+timeLeft+"minute: "+minute+" second: "+second)
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE)
            }

        return NotificationCompat.Builder(this, "StopWatch")
            .setContentTitle(title)
            .setContentText("${"%02d".format(minute)} : ${"%02d".format(second)}")
            .setSmallIcon(androidx.activity.R.drawable.notification_bg)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun getNotificationManager() {
        notificationManager = ContextCompat.getSystemService(this,NotificationManager::class.java) as NotificationManager
    }

    private fun createChannel() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val channelId = "StopWatch"
            val channelName = "Timer"
            val chan = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_HIGH
            )
            chan.importance = NotificationManager.IMPORTANCE_NONE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}