package com.example.movieapp.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.movieapp.databinding.ActivitySplashScreenBinding
import com.example.movieapp.home.HomeActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private val splashScreenTime: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Handler(mainLooper)
            .postDelayed({
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, splashScreenTime)
    }
}
