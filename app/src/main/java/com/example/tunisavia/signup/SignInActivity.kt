package com.example.tunisavia.signup

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.flatdialoglibrary.dialog.FlatDialog
import com.example.tunisavia.MainActivity
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.base.Constants
import com.example.tunisavia.databinding.ActivitySignInBinding
import com.example.tunisavia.entity.LoginParam
import com.example.tunisavia.entity.NotificationData
import com.example.tunisavia.entity.PushNotification
import com.example.tunisavia.entity.User
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.stepp.SignUpFour
import com.example.tunisavia.utils.changeVisibilityPassword
import com.example.tunisavia.visitor.ListPlanesActivity
import com.example.tunisavia.visitor.VisitorActivity
import kotlinx.coroutines.*
import java.util.*

@ExperimentalCoroutinesApi
class SignInActivity : BaseActivity() {

    lateinit var binding: ActivitySignInBinding
    val defineLayoutBinding: () -> View
        get() = {
            binding = ActivitySignInBinding.inflate(layoutInflater)
            val view = binding.root
            view
        }

    var isFirstTime = true
    var isAllValid = false
    val listCheck = arrayListOf(false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(defineLayoutBinding())
        sharedPreference = providePreferencesHelper(this)
        binding.apply {

            passwordIn.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordIn.typeface = Typeface.DEFAULT
            initVisibilityPassword(passwordIn)
            passwordIn.changeVisibilityPassword()

            emailIn.doAfterTextChanged {
                if (!isFirstTime) checkEmail()
            }

            passwordIn.doAfterTextChanged {
                if (!isFirstTime) checkPassword()
            }

            signupLinkContainer.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            }

            valid.setOnClickListener {
                if (isFirstTime) {
                    checkEmail()
                    checkPassword()
                    isFirstTime = false
                }

                if (listCheck.none { !it }) {
                    loadingAnim.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        val service = NetworkModuleFactory.makeService()
                        val user = LoginParam()
                        user.email = emailIn.text.toString()
                        user.password = passwordIn.text.toString()
                        val list: MutableList<User> = service.getUserByEmail(user).results as MutableList<User>
                        if (list.isEmpty()) {
                            loadingAnim.visibility = View.GONE
                            val flatDialog = FlatDialog(this@SignInActivity)
                            flatDialog.setIcon(R.drawable.crying)
                                .setTitle("Somthing went wrong !")
                                .setTitleColor(Color.parseColor("#FFFFFF"))
                                .setSubtitle("Wrong credentials")
                                .setSubtitleColor(Color.parseColor("#FFFFFF"))
                                .setBackgroundColor(Color.parseColor("#FF3700B3"))
                                .setFirstButtonColor(Color.parseColor("#FFFFFF"))
                                .setFirstButtonTextColor(Color.parseColor("#000000"))
                                .setFirstButtonText("Try again")
                                .withFirstButtonListner { flatDialog.dismiss() }
                                .show()
                        } else {
                            if (list[0].type == "Technical") sharedPreference.putBoolean("fromTeck", true)
                            else sharedPreference.putBoolean("fromTeck", false)
                            delay(2000)
                            loadingAnim.visibility = View.GONE
                            startActivity(Intent(this@SignInActivity, ListPlanesActivity::class.java))
                        }
                        Log.i("providePreferencesHelper", "onCreate: $list")
                    }
                }
            }
        }
    }

    private fun checkPassword() {
        listCheck[1] = binding.passwordIn.text.length >= 8
        binding.passwordError.text =
            if (binding.passwordIn.text.length < 8) "password is invalid" else ""
    }

    private fun checkEmail() {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(binding.emailIn.text.toString()).matches()
        listCheck[0] = isValid
        binding.emailError.text = if (!isValid) "Email must match pattern" else ""
    }

    override fun onResume() {
        super.onResume()
        isFirstTime = true
    }
}