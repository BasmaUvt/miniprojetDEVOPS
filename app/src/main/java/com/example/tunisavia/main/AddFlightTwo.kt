package com.example.tunisavia.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseFragment
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.update.UpdateVolActivity
import com.example.tunisavia.utils.recyclerpickerdialog.Item
import com.example.tunisavia.utils.recyclerpickerdialog.RecyclerPickerDialogFragment
import com.example.tunisavia.utils.recyclerpickerdialog.SelectionType
import com.example.tunisavia.utils.recyclerpickerdialog.SelectorType
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
class AddFlightTwo : BaseFragment() {

    private lateinit var dateDepImg: TextView
    private lateinit var dateDepIn: TextView
    private lateinit var updateOne: TextView
    private lateinit var updateTwo: TextView
    private lateinit var updateThree: TextView
    private lateinit var timeTestIn: TextView
    var dateDep = ""
    var selectedCountry: String? = "AZR"
    var addInteraction: AddInteraction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            sharedPreference = providePreferencesHelper(it)
        }
        try {
            mSocket = IO.socket("https://staravion.herokuapp.com")
        } catch (e: URISyntaxException) {
            Log.i("MainActivityAla", "onCreate: ${e.message}")
        }

        mSocket.on(Socket.EVENT_CONNECT) {
            Log.d("MainActivityAla", "Socket Connected!")

        }
        mSocket.connect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_two, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateOne = view.findViewById(R.id.update_one_img)
        updateTwo = view.findViewById(R.id.update_two_img)
        updateThree = view.findViewById(R.id.update_three_img)

        dateDepImg = view.findViewById(R.id.date_dep_img)
        dateDepIn = view.findViewById(R.id.date_dep_in)
        updateOne.setOnClickListener {
            val now: Calendar = Calendar.getInstance()
            context?.let {
                val d = DatePickerDialog(
                    it,
                    { view1: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

                        val simpledateformat = SimpleDateFormat("EEEE")
                        val date = Date(year, month, dayOfMonth - 1)
                        val dayOfWeek: String = simpledateformat.format(date)

                        dateDepIn.text = "$dayOfWeek ${dayOfMonth}/${month + 1}/$year"
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
                )
                d.show()
                d.setOnCancelListener {
                    Log.i("DatePickerDialog", "onViewCreated: $it")
                    //t.dismiss()
                }
            }
        }

        timeTestIn = view.findViewById(R.id.time_test_in)
        updateTwo.setOnClickListener {
            val nowTime = Calendar.getInstance()
            context?.let {
                val t = TimePickerDialog(
                    it,
                    { view11: TimePicker?, hour: Int, minute: Int ->
                        Log.d("OriginalTime", "$hour  $minute")
                        timeTestIn.text = "${hour}:$minute"
                    },
                    nowTime[Calendar.HOUR_OF_DAY],
                    nowTime[Calendar.MINUTE],
                    true
                )
                t.show()
            }
        }

        val placeTestIn: TextView = view.findViewById(R.id.place_test_in)
        updateThree.setOnClickListener {
            val picker =
                RecyclerPickerDialogFragment
                    .newInstance(
                        type = SelectionType.SINGLE, //default
                        selector = SelectorType.CHECK_BOX, //default
                        theme = R.style.RecyclerPickerDialogTheme, //default
                        onItemsPicked = { selected ->
                            selectedCountry = selected[0].text
                            placeTestIn.text = selectedCountry
                        }
                    )
                    .apply {
                        /* configure custom fields */
                        title = "Choose Country"
                        data = arrayListOf<Item>().also {
                            app.allPlacesList.map { country ->
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

            picker.show(childFragmentManager, "MyPickerDialogFragment")
        }
    }

    override fun onResume() {
        super.onResume()
        addInteraction?.changeTitle("Date and place for depart")
    }

    fun getDepartInformation(): TechnicalParam {
        val user = TechnicalParam()
        user.dateDep = "${dateDepIn.text} at ${timeTestIn.text}"
        user.zoneDep = selectedCountry
        return user
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(addInteraction: AddInteraction) = AddFlightTwo().apply {
            this.addInteraction = addInteraction
        }
    }
}