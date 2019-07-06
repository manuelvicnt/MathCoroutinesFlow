package com.manuelvicnt.coroutinesflow.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.manuelvicnt.coroutinesflow.R
import com.manuelvicnt.coroutinesflow.obtainViewModel
import com.manuelvicnt.coroutinesflow.user.UserActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = obtainViewModel()
        prepareUI()
    }

    private fun prepareUI() {
        prepareColdFibonacci()
        prepareNeverEndingFibonacci()
        setUpSettingsButton()
    }

    /**
     * Listens to the LiveData exposed by the ViewModel
     *
     * When the View is destroyed, it'll unsubscribe from the LiveData and the flow will stop.
     * Whenever the View is created again, the flow will also start and will emit the
     * Fibonacci sequence from the beginning.
     */
    private fun prepareColdFibonacci() {
        val coldFibonacciText = findViewById<TextView>(R.id.cold_fibonacci)
        viewModel.coldFibonacci.observe(this) {
            coldFibonacciText.text = it.toString()
        }
    }

    /**
     * Listens to the Channel exposed by the ViewModel
     *
     * When the View is destroyed, it'll unsubscribe from the LiveData and the flow will stop.
     * Whenever the View is created again, the flow will also start and will emit the
     * Fibonacci sequence from the beginning.
     */
    private fun prepareNeverEndingFibonacci() {
        val neverEndingFibonacciText = findViewById<TextView>(R.id.never_ending_fibonacci)
        lifecycleScope.launchWhenStarted {
            viewModel.neverEndingFibonacci.consumeEach {
                neverEndingFibonacciText.text = it.toString()
            }
        }
    }

    private fun setUpSettingsButton() {
        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
    }
}
