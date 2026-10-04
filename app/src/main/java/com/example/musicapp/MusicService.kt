package com.example.musicapp

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MusicService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action=intent?.action
        action?.let {
            when(action){
                Constant.SHOW->{
                    sendBroadCast1(Constant.SHOW)
                }
                Constant.HIGH -> {
                    sendBroadCast1(Constant.HIGH)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun sendBroadCast1(action: String){
        val intent= Intent(action)
        sendBroadcast(intent)
    }
}