package com.ahmedabdelmeged.swissborg.btcusd.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ahmedabdelmeged.swissborg.btcusd.R

/**
 * Just a container for the application fragments. In a production application. Maybe we should consider
 * using the new Navigation component from the Android Architecture Components.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment())
                    .commit()
        }
    }

}