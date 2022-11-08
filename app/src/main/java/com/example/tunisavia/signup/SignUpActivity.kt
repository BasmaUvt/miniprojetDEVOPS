package com.example.tunisavia.signup

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.flatdialoglibrary.dialog.FlatDialog
import com.example.tunisavia.MainActivity
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.entity.User
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.utils.changeVisibilityPassword
import com.example.tunisavia.utils.recyclerpickerdialog.Item
import com.example.tunisavia.utils.recyclerpickerdialog.RecyclerPickerDialogFragment
import com.example.tunisavia.utils.recyclerpickerdialog.SelectionType
import com.example.tunisavia.utils.recyclerpickerdialog.SelectorType
import com.example.tunisavia.visitor.ListPlanesActivity
import com.example.tunisavia.visitor.VisitorActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@ExperimentalCoroutinesApi
class SignUpActivity : BaseActivity() {
    lateinit var fullName: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var confirmPassword: EditText
    lateinit var emailError: TextView
    lateinit var typeUser: TextView
    lateinit var fullNameError: TextView
    lateinit var passwordError: TextView
    lateinit var confirmPasswordError: TextView
    lateinit var valid: Button

    var isFirstTime = true
    var isAllValid = false

    val listCheck = arrayListOf(false, false, false, false)

   /* private fun initVisibilityPassword(inputFullName: EditText) {
        inputFullName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_baseline_visibility_off_24,
            0
        )
        inputFullName.transformationMethod = PasswordTransformationMethod.getInstance()
    }
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        sharedPreference = providePreferencesHelper(this)
        fullName = findViewById(R.id.full_name_in)
        fullNameError = findViewById(R.id.full_name_error)

        password = findViewById(R.id.password_in)
        passwordError = findViewById(R.id.password_error)

        email = findViewById(R.id.email_in)
        emailError = findViewById(R.id.email_error)

        confirmPassword = findViewById(R.id.confirm_password_in)
        confirmPasswordError = findViewById(R.id.confirm_password_error)
        valid = findViewById(R.id.valid)
        typeUser = findViewById(R.id.type_user_in)

        val signInLink = findViewById<LinearLayout>(R.id.signin_link_container)
        signInLink.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        }

        typeUser.setOnClickListener {
            val picker =
                RecyclerPickerDialogFragment
                    .newInstance(
                        type = SelectionType.SINGLE,
                        selector = SelectorType.CHECK_BOX,
                        withImage = false,
                        theme = R.style.RecyclerPickerDialogTheme,
                        onItemsPicked = { selected ->
                            typeUser.text = selected[0].text
                        }
                    )
                    .apply {
                        title = "Choose Your Type"
                        data = arrayListOf<Item>().also {
                            it.add(Item("Technical"))
                            it.add(Item("Visitor"))
                        }
                        showSearchBar = false
                        inputHint = "search"
                        //itemsLayoutAnimator = R.anim.layout_animation_slide_up
                        dialogHeight =
                            (300 * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
                        isChoiceMandatory = true
                        isCancelable = true
                        lifecycleOwner = this
                    }

            picker.show(supportFragmentManager, "MyPickerDialogFragment")
        }

        password.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        password.typeface = Typeface.DEFAULT
        initVisibilityPassword(password)
        password.changeVisibilityPassword()

        email.doAfterTextChanged {
            if (!isFirstTime) checkEmail()
        }

        fullName.doAfterTextChanged {
            if (!isFirstTime) checkFullName()
        }

        password.doAfterTextChanged {
            if (!isFirstTime) checkPassword()
        }

        valid.setOnClickListener {

            if (isFirstTime) {
                checkFullName()
                checkEmail()
                checkPassword()
                checkConfirmPassword()
                isFirstTime = false
            }
            Log.i("valid", "onCreate: $isFirstTime  $isAllValid")

            if (listCheck.none { !it }) {
                findViewById<ConstraintLayout>(R.id.loading_anim_signup).visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.Main) {
                    val service = NetworkModuleFactory.makeService()
                    val user = User()
                    user.id = 4
                    user.fullName = fullName.text.toString()
                    user.email = email.text.toString()
                    user.password = password.text.toString()
                    user.type = typeUser.text.toString()
                    val message: String = service.saveUser(user).message.toString()

                    if (message == "ok") {
                        if (user.type == "Technical") sharedPreference.putBoolean("fromTeck", true)
                        else sharedPreference.putBoolean("fromTeck", false)
                        delay(2000)
                        findViewById<ConstraintLayout>(R.id.loading_anim_signup).visibility = View.GONE
                        startActivity(Intent(this@SignUpActivity, ListPlanesActivity::class.java))
                    } else {
                        findViewById<ConstraintLayout>(R.id.loading_anim_signup).visibility = View.GONE
                        val flatDialog = FlatDialog(this@SignUpActivity)
                        flatDialog.setIcon(R.drawable.crying)
                            .setTitle("Somthing went wrong !")
                            .setTitleColor(Color.parseColor("#FFFFFF"))
                            .setSubtitle("Try again")
                            .setSubtitleColor(Color.parseColor("#FFFFFF"))
                            .setBackgroundColor(Color.parseColor("#FF3700B3"))
                            .setFirstButtonColor(Color.parseColor("#FFFFFF"))
                            .setFirstButtonTextColor(Color.parseColor("#000000"))
                            .setFirstButtonText("Try again")
                            .withFirstButtonListner { flatDialog.dismiss() }
                            .show()
                    }
                }
            }
        }
    }

    private fun checkConfirmPassword() {
        val pass = password.text.toString()
        val confirmPass = confirmPassword.text.toString()
        confirmPasswordError.text = if (pass != confirmPass) "password is not the same" else ""
        listCheck[3] = pass == confirmPass
    }

    private fun checkPassword() {
        listCheck[2] = password.text.length >= 8
        passwordError.text = if (password.text.length < 8) "password is invalid" else ""
    }

    private fun checkFullName() {
        val p = Pattern.compile("^([A-Z][a-z]*((\\s)))+[A-Z][a-z]*\$")
        val m = p.matcher(fullName.text.toString())
        listCheck[0] = m.matches()
        fullNameError.text = if (!m.matches()) "Name is invalid" else ""
    }

    private fun checkEmail() {
        val isValid = Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()
        listCheck[1] = isValid
        emailError.text = if (!isValid) "Email must match pattern" else ""
    }

    override fun onResume() {
        super.onResume()
        isFirstTime = true
    }
}