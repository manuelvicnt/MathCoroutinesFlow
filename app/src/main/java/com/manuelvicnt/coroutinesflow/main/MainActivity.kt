/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.manuelvicnt.coroutinesflow.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.manuelvicnt.coroutinesflow.R
import com.manuelvicnt.coroutinesflow.ViewModelFactory
import com.manuelvicnt.coroutinesflow.user.UserActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { ViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            viewModel.neverEndingFibonacci.collect {
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
