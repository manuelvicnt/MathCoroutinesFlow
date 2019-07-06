package com.manuelvicnt.coroutinesflow.user

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.manuelvicnt.coroutinesflow.R
import com.manuelvicnt.coroutinesflow.obtainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class UserActivity : AppCompatActivity() {

    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        viewModel = obtainViewModel()

        val textView = findViewById<TextView>(R.id.user)
        lifecycleScope.launchWhenCreated {
            textView.text = viewModel.loadUser()
        }
    }
}
