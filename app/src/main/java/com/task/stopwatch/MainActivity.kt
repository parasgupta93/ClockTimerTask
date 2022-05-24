package com.task.stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var isTimerRunning = false
    private var totalTime = 0;
    private lateinit var statusReciever : BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_button.setOnClickListener {
            totalTime = edit_text_timer.text.toString().toInt()

            if(isTimerRunning){
                pauseStopWatch()
            }
            else{
                startStopWatch()
                start_button.setText("STOP")
            }
        }
    }


    override fun onResume() {
        super.onResume()

        val statusFilter = IntentFilter()
        statusFilter.addAction(StopwatchService.TICK)
         statusReciever = object : BroadcastReceiver() {

            override fun onReceive(contxt: Context?, intent: Intent?) {
                Log.d("Paras","timeleft.....:")
                when (intent?.action) {
                 StopwatchService.TICK ->{
                     Log.d("Paras","timeleft3333.....:")
                     isTimerRunning = true
                     val timeElapsed = intent.getIntExtra(StopwatchService.TIME_ELAPSED,0)
                     val timeLeft = totalTime*60 - timeElapsed
                     val minute = (timeLeft)/60
                     val second = (timeLeft)%60
                     text_minutes.setText("${"%02d".format(minute)} : ${"%02d".format(second)}")
                     edit_text_timer.visibility = View.GONE
                 }
                }
            }
        }
        registerReceiver(statusReciever,statusFilter)
    }
    private fun startStopWatch(){
        val stopwatchService = Intent(this, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION,StopwatchService.MOVE_TO_FOREGROUND)
        startService(stopwatchService)
    }

    private fun pauseStopWatch(){
        val stopwatchService = Intent(this, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION,StopwatchService.MOVE_TO_BACKGROUND)
        startService(stopwatchService)
    }
}