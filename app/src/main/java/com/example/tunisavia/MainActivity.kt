package com.example.tunisavia

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.flatdialoglibrary.dialog.FlatDialog
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.base.BaseFragment
import com.example.tunisavia.base.BaseViewPagerAdapter
import com.example.tunisavia.base.SocketInstance
import com.example.tunisavia.entity.*
import com.example.tunisavia.main.*
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.signup.SignInActivity
import com.example.tunisavia.utils.recyclerpickerdialog.Item
import com.example.tunisavia.utils.recyclerpickerdialog.RecyclerPickerDialogFragment
import com.example.tunisavia.utils.recyclerpickerdialog.SelectionType
import com.example.tunisavia.utils.recyclerpickerdialog.SelectorType
import com.example.tunisavia.utils.viewpager.KKViewPager
import com.example.tunisavia.utils.viewpager.ViewPagerUtils
import com.example.tunisavia.visitor.ListPlanesActivity
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


@ExperimentalCoroutinesApi
class MainActivity : BaseActivity(), AddInteraction {

    private lateinit var nextBtn: Button
    private lateinit var mediaVp : KKViewPager

    private lateinit var dateArrImg: TextView
    private lateinit var dateArrIn: EditText

    //toolbar
    private lateinit var titleToolbar: TextView
    private lateinit var logoutToolbar: TextView
    private lateinit var backToolbar: TextView

    private lateinit var countryArrIn: TextView
    private lateinit var cityArrIn: TextView
    private lateinit var addFlightOne: AddFlightOne
    private lateinit var addFlightTwo: AddFlightTwo
    private lateinit var addFlightThree: AddFlightThree
    private lateinit var loadingAnim: ConstraintLayout

    var dateArr = ""

    var selectedCountryArr: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = (this.application as SocketInstance)
        sharedPreference = providePreferencesHelper(this)

        try {
            mSocket = IO.socket("http://192.168.2.56:3000")
        } catch (e: URISyntaxException) {
            Log.i("MainActivityAla", "onCreate: ${e.message}")
        }

        mSocket?.on(Socket.EVENT_CONNECT) {
            Log.d("MainActivityAla", "Socket Connected!")

        }
        mSocket?.connect()

        addFlightOne = AddFlightOne.newInstance(this)
        addFlightTwo = AddFlightTwo.newInstance(this)
        addFlightThree = AddFlightThree.newInstance(this)

        val listFragment: MutableList<BaseFragment> = mutableListOf()
        listFragment.add(addFlightOne)
        listFragment.add(addFlightTwo)
        listFragment.add(addFlightThree)

        val mainContainerAdapter = BaseViewPagerAdapter(supportFragmentManager, listFragment)
        mediaVp = findViewById(R.id.media_vp)
        mediaVp.offscreenPageLimit = listFragment.size
        mediaVp.adapter = mainContainerAdapter
        mediaVp.setAnimationEnabled(true)
        mediaVp.setFadeEnabled(true)
        mediaVp.setFadeFactor(0.5f)

        mediaVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d("", "onPageScrolled: ")
            }

            override fun onPageSelected(position: Int) {
                //currentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("", "onPageScrollStateChanged: ")
            }
        })


        //Toolbar
        titleToolbar = findViewById(R.id.titleToolbar)
        logoutToolbar = findViewById(R.id.logoutToolbar)
        backToolbar = findViewById(R.id.backToolbar)

        backToolbar.setOnClickListener {
            if (mediaVp.currentItem == 0) finish()
            else ViewPagerUtils.onPrevious(mediaVp)
        }

        logoutToolbar.setOnClickListener {
            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
        }

        dateArrImg = findViewById(R.id.date_arr_img)
        dateArrIn = findViewById(R.id.date_arr_in)
        countryArrIn = findViewById(R.id.country_arr_in)
        cityArrIn = findViewById(R.id.city_arr_in)
        dateArrImg.setOnClickListener {
            val now: Calendar = Calendar.getInstance()
            val nowTime = Calendar.getInstance()
            val t = TimePickerDialog(
                this,
                { view11: TimePicker?, hour: Int, minute: Int ->
                    Log.d("OriginalArrTimeArr", "$hour  $minute")
                    dateArr = "$dateArr ${hour}:$minute"
                    dateArrIn.setText(dateArr)
                },
                nowTime[Calendar.HOUR_OF_DAY],
                nowTime[Calendar.MINUTE],
                true
            )
            t.show()
            val d = DatePickerDialog(
                this,
                { view1: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    Log.d(
                        "OriginalArrDate",
                        "$year  ${month + 1}  $dayOfMonth  ${getNameOfDay(year, dayOfMonth)}"
                    )
                    dateArr = "${getNameOfDay(year, dayOfMonth)} ${dayOfMonth}/${month + 1}/$year at"
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            d.show()
            //d.setOnShowListener { Log.i("DatePickerDialog", "onViewCreated: $it") }
            d.setOnCancelListener {
                Log.i("DatePickerDialog", "onViewCreated: $it")
                t.dismiss()
            }

        }
        cityArrIn.setOnClickListener {
            val picker =
                RecyclerPickerDialogFragment
                    .newInstance(
                        type = SelectionType.SINGLE, //default
                        selector = SelectorType.CHECK_BOX, //default
                        theme = R.style.RecyclerPickerDialogTheme, //default
                        onItemsPicked = { selected ->
                            cityArrIn.text = selected[0].text
                        }
                    )
                    .apply {
                        /* configure custom fields */
                        title = "Choose City"
                        data = arrayListOf<Item>().also {
                            for ((k, v) in app.allCitiesList) {
                                if (k == selectedCountryArr) {
                                    v.map { city ->
                                        it.add(Item(city ?: ""))
                                    }
                                }
                            }
                        }
                        showSearchBar = true
                        inputHint = "search"
                        //itemsLayoutAnimator = R.anim.layout_animation_slide_up
                        dialogHeight =
                            (500 * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
                        isChoiceMandatory = true
                        isCancelable = true
                        lifecycleOwner = this
                    }

            picker.show(supportFragmentManager, "MyPickerDialogFragment")
        }
        countryArrIn.setOnClickListener {
            val picker =
                RecyclerPickerDialogFragment
                    .newInstance(
                        type = SelectionType.SINGLE, //default
                        selector = SelectorType.CHECK_BOX, //default
                        theme = R.style.RecyclerPickerDialogTheme, //default
                        onItemsPicked = { selected ->
                            selectedCountryArr = selected[0].text
                            countryArrIn.text = selected[0].text
                        }
                    )
                    .apply {
                        /* configure custom fields */
                        title = "Choose Country"
                        data = arrayListOf<Item>().also {
                            app.allCountriesList.map { country ->
                                it.add(Item(country ?: ""))
                            }
                        }
                        showSearchBar = true
                        inputHint = "search"
                        //itemsLayoutAnimator = R.anim.layout_animation_slide_up
                        dialogHeight =
                            (500 * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
                        isChoiceMandatory = true
                        isCancelable = true
                        lifecycleOwner = this
                    }

            picker.show(supportFragmentManager, "MyPickerDialogFragment")
        }

        loadingAnim = findViewById(R.id.loading_anim)

        nextBtn = findViewById(R.id.next_btn)
        nextBtn.setOnClickListener {
            Log.i("testcurrent", "onCreate: ${mediaVp.currentItem}")

            if (mediaVp.currentItem != 2) ViewPagerUtils.onNext(mediaVp)
            else {
                lifecycleScope.launch {
                    val service = NetworkModuleFactory.makeService()
                    val user = TechnicalParam()

                    val tParam = addFlightOne.getGeneralInformation()
                    user.id = tParam.id
                    user.planeId = tParam.planeId
                    user.type = tParam.type
                    user.nameVol = tParam.nameVol
                    user.numberPassenger = tParam.numberPassenger
                    user.pilotId = tParam.pilotId

                    val departParam = addFlightTwo.getDepartInformation()
                    user.dateDep = departParam.dateDep
                    user.zoneDep = departParam.zoneDep

                    val arrivingParam = addFlightThree.getArrivingInformation()
                    user.dateArr = arrivingParam.dateArr
                    user.zoneArr = arrivingParam.zoneArr

                    user.isChecked = false
                    Log.i("nextBtn", "onViewCreated: $user")
                    if (!checkDate(departParam.dateDep, arrivingParam.dateArr)) {
                        val flatDialog = FlatDialog(this@MainActivity)
                        flatDialog.setIcon(R.drawable.crying)
                            .setTitle("Somthing went wrong !")
                            .setTitleColor(Color.parseColor("#FFFFFF"))
                            .setSubtitle("Arriving date is before Departing date")
                            .setSubtitleColor(Color.parseColor("#FFFFFF"))
                            .setBackgroundColor(Color.parseColor("#FF3700B3"))
                            .setFirstButtonColor(Color.parseColor("#FFFFFF"))
                            .setFirstButtonTextColor(Color.parseColor("#000000"))
                            .setFirstButtonText("Try again")
                            .withFirstButtonListner { flatDialog.dismiss() }
                            .show()
                    } else {
                        loadingAnim.visibility = View.VISIBLE
                        delay(2000)
                        loadingAnim.visibility = View.GONE
                        val message: String = service.saveTechnic(user).message.toString()
                        if (message == "ok") {
                            startActivity(Intent(this@MainActivity, ListPlanesActivity::class.java))
                        }
                    }
                }
            }
        }
    }

    fun checkDate(dateDep : String?, dateArr : String?): Boolean {
        val dateDepart = dateDep?.split(" ")?.get(1)
        val dateArrive = dateArr?.split(" ")?.get(1)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val strDate = sdf.parse(dateDepart ?: "01/01/2022")
        val endDate = sdf.parse(dateArrive ?: "01/01/2022")

        Log.i("checjsdzfnejkfze", "checkDate: ${endDate?.after(strDate)}")

        return endDate?.after(strDate) ?: false
    }

    private fun getNameOfDay(year: Int, dayOfMonth: Int): String? {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        val days =
            arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayIndex = calendar[Calendar.DAY_OF_WEEK]
        Log.i("OriginalDate", "getNameOfDay: $dayOfMonth   ${Calendar.DAY_OF_WEEK}")
        return days[dayIndex - 1]
    }

    override fun changeTitle(title: String) {
        titleToolbar.text = title
    }
}