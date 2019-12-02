package com.example.cameraxdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_into)

        val thread = Thread(startActivityThread())
        thread.start()
    }



    inner class startActivityThread: Runnable{
        override fun run() {
            Thread.sleep(3000)
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }



}
