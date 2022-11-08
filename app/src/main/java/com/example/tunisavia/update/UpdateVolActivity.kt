package com.example.tunisavia.update

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager.widget.ViewPager
import com.aceinteract.android.stepper.StepperNavListener
import com.aceinteract.android.stepper.StepperNavigationView
import com.example.tunisavia.R
import com.example.tunisavia.StepperViewModel
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.databinding.ActivityUpdateVolBinding
import com.example.tunisavia.entity.StepperSettings
import com.example.tunisavia.signup.SignInActivity
import com.example.tunisavia.stepp.*
import com.example.tunisavia.stepp.adapter.ViewPagerAdapter
import com.example.tunisavia.utils.viewpager.ViewPagerUtils
import com.example.tunisavia.visitor.ListPlanesActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ng.softcom.android.utils.ui.findNavControllerFromFragmentContainer
import ng.softcom.android.utils.ui.showToast

@ExperimentalCoroutinesApi
class UpdateVolActivity : BaseActivity(), StepperNavListener,
    FragmentInteraction.BasicInfoFragmentInteraction {

    lateinit var binding: ActivityUpdateVolBinding
    val defineLayoutBinding: () -> View
        get() = {
            binding = ActivityUpdateVolBinding.inflate(layoutInflater)
            val view = binding.root
            view
        }

    private val viewModel: StepperViewModel by lazy {
        ViewModelProvider(this)[StepperViewModel::class.java]
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(defineLayoutBinding())

        //setSupportActionBar(binding.toolbar)
        //binding.toolbar.overflowIcon = getDrawable(R.drawable.empty_user)
        /*binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }*/

        binding.toolbarLogout.setOnClickListener {
            val i = Intent(this@UpdateVolActivity, SignInActivity::class.java)
            startActivity(i)
        }

        binding.toolbarBack.setOnClickListener {
            onBackPressed()
        }

        /*binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.menu_log_out_tool -> {
                    Log.i("TestAla", "onCreate: azadaeff")
                }
            }
            true
        }*/

        sharedPreference = providePreferencesHelper(this)
        binding.stepper.initializeStepper()

        val list = mutableListOf(
            SignUpOne.newInstance(this),
            SignUpTwo.newInstance(this),
            SignUpThree.newInstance(this),
            SignUpFour.newInstance(this),
            SignUpFive.newInstance(this)
        )

        val adapter = ViewPagerAdapter(supportFragmentManager, list)
        binding.viewpagerStepperUpdate.adapter = adapter
        binding.toolbarText.text = "Checking"
        binding.viewpagerStepperUpdate.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.toolbarText.text = when(position) {
                    0 -> "Checking"
                    1 -> "Fueling"
                    2 -> "Briefing"
                    3 -> "Cargo and passengers"
                    4 -> "Ops"
                    else -> "Checking"
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        //setupActionBarWithNavController(findNavControllerFromFragmentContainer(R.id.frame_stepper_update))
        findNavControllerFromFragmentContainer(R.id.frame_stepper_update).addOnDestinationChangedListener { _, destination, arguments ->
            //binding.toolBarHome.setTitle(destination.label, titleTextView, arguments)
           // binding.toolbarText.text = destination.label
        }
        collectStateFlow()
    }

   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_log_out_tool -> Log.i("TestAla", "onCreate: azadaeff")
        }
        return true
    }
*/
    fun changeToolbarTitle(title: String) {
        Log.i("testToolbar", "changeToolbarTitle: $title")
        //binding.toolbarText.text = title
    }

    private fun StepperNavigationView.initializeStepper() {
        viewModel.updateStepper(
            StepperSettings(
                widgetColor,
                textColor,
                textSize,
                iconSize
            )
        )

        //menu.add(0 /* or 0 */, R.id.step_4_dest, 0, "New Step")

        stepperNavListener = this@UpdateVolActivity
        setupWithNavController(findNavControllerFromFragmentContainer(R.id.frame_stepper_update))
    }

    fun returnToList() {
        startActivity(Intent(this, ListPlanesActivity::class.java))
    }

    private fun collectStateFlow() {
        viewModel.stepperSettings.onEach {
            binding.stepper.widgetColor = it.iconColor
            binding.stepper.textColor = it.textColor
            binding.stepper.textSize = it.textSize
            binding.stepper.iconSize = it.iconSize
        }.launchIn(lifecycleScope)
    }

    override fun onStepChanged(step: Int) {
        if (step == 3) {
            binding.buttonNext.setImageResource(R.drawable.ic_check)
        } else {
            binding.buttonNext.setImageResource(R.drawable.ic_right)
        }
    }

    override fun onCompleted() {
        showToast("Stepper completed")
    }

    /**
     * Use navigation controller to navigate up.
     */
    override fun onSupportNavigateUp(): Boolean =
        findNavControllerFromFragmentContainer(R.id.frame_stepper_update).navigateUp()

    /**
     * Navigate up when the back button is pressed.
     */
    override fun onBackPressed() {
        if (binding.stepper.currentStep == 0) {
            super.onBackPressed()
        } else {
            //findNavControllerFromFragmentContainer(R.id.frame_stepper).navigateUp()
            binding.stepper.goToPreviousStep()
            ViewPagerUtils.onPrevious(binding.viewpagerStepperUpdate)
        }
    }

    override fun onClick(index: Int?) {
        binding.stepper.goToNextStep()
        ViewPagerUtils.onNext(binding.viewpagerStepperUpdate)
    }
}