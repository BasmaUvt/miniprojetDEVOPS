package com.example.tunisavia.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.databinding.ActivitySplashScreenBinding
import com.example.tunisavia.databinding.ActivityUpdateVolBinding
import com.example.tunisavia.signup.SignInActivity
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity() {

    lateinit var binding: ActivitySplashScreenBinding
    val defineLayoutBinding: () -> View
        get() = {
            binding = ActivitySplashScreenBinding.inflate(layoutInflater)
            val view = binding.root
            view
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(defineLayoutBinding())

        CoroutineScope(Dispatchers.Main).launch(Job()){
            delay(DELAY)
            startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
        }
        //binding.splashLogo.animate().alpha(1f).duration = DELAY
    }

    companion object {
        const val DELAY = 6000L
    }
}