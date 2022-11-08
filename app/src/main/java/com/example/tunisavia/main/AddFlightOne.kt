package com.example.tunisavia.main

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseFragment
import com.example.tunisavia.base.SocketInstance
import com.example.tunisavia.entity.Pilot
import com.example.tunisavia.entity.Plane
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.update.UpdateVolActivity
import com.example.tunisavia.utils.recyclerpickerdialog.Item
import com.example.tunisavia.utils.recyclerpickerdialog.RecyclerPickerDialogFragment
import com.example.tunisavia.utils.recyclerpickerdialog.SelectionType
import com.example.tunisavia.utils.recyclerpickerdialog.SelectorType
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.net.URISyntaxException

@ExperimentalCoroutinesApi
class AddFlightOne : BaseFragment() {

    lateinit var minusBtn: TextView
    lateinit var plusBtn: TextView
    lateinit var numb: EditText
    lateinit var nameVol: EditText
    var count = 5
    private lateinit var choicePilot: TextView
    private lateinit var type: TextView

    private lateinit var pilotBtn: RelativeLayout
    private lateinit var changePilot: ImageView
    private lateinit var imgPilot: ImageView
    private lateinit var notEmptyPilot: LinearLayout
    var pilot: Pilot? = null
    var position = 0
    var list = mutableListOf<Pilot>()
    private var listPlane = mutableListOf<Plane>()
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
        list = app.allPilotList
        listPlane = app.allPlanesList
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_one, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = view.findViewById(R.id.name_in)
        nameVol = view.findViewById(R.id.name_vol)
        choicePilot = view.findViewById(R.id.choice_pilot)
        imgPilot = view.findViewById(R.id.item_img)
        notEmptyPilot = view.findViewById(R.id.not_empty_pilot)
        changePilot = view.findViewById(R.id.change_pilot_img)
        pilotBtn = view.findViewById(R.id.pilot_container)
        changePilot.setOnClickListener {
            changePilot()
        }

        type.setOnClickListener {
            val picker =
                RecyclerPickerDialogFragment
                    .newInstance(
                        type = SelectionType.SINGLE,
                        selector = SelectorType.NONE,
                        withImage = false,
                        theme = R.style.RecyclerPickerDialogTheme,
                        onItemsPicked = { selected ->
                            type.text = selected[0].text
                        }
                    )
                    .apply {
                        title = "Choose Your Plane"
                        data = arrayListOf<Item>().also {
                            listPlane.map { plane ->
                                it.add(Item("${plane.namePlane}  ${plane.typePlane}"))
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


        minusBtn = view.findViewById(R.id.minus_btn)
        plusBtn = view.findViewById(R.id.plus_btn)
        numb = view.findViewById(R.id.numb)
        count = numb.text.toString().toInt()

        minusBtn.setOnClickListener {
            Log.i("minusBtn", "onViewCreated: $count  ${numb.text.toString().toInt()}")
            count -= 1
            if (count == 0) {
                minusBtn.visibility = View.INVISIBLE
                return@setOnClickListener
            }
            minusBtn.visibility = View.VISIBLE
            numb.setText(count.toString())
        }

        plusBtn.setOnClickListener {
            minusBtn.visibility = View.VISIBLE
            count += 1
            numb.setText(count.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        addInteraction?.changeTitle("General information")
    }

    fun getGeneralInformation(): TechnicalParam {
        val planeId = app.allPlanesList.find { it.namePlane == type.text.toString().split("  ")[0] }?.idPlane
        Log.i("nextBtn", "${type.text.toString().split("  ")[0]}  onViewCreated: $planeId")

        val user = TechnicalParam()

        user.id = getRandomNumber()
        user.planeId = planeId ?: 0
        user.type = type.text.toString()
        user.nameVol = nameVol.text.toString()
        user.numberPassenger = numb.text.toString().toInt()
        val pilot = list.find { pilot -> pilot.name == choicePilot.text }
        if (pilot != null) user.pilotId = pilot.id ?: 0

        return user
    }

    fun changePilot() {
        val picker =
            RecyclerPickerDialogFragment
                .newInstance(
                    type = SelectionType.SINGLE,
                    selector = SelectorType.CHECK_BOX,
                    withImage = true,
                    theme = R.style.RecyclerPickerDialogTheme,
                    onItemsPicked = { selected ->
                        imgPilot.setImageResource(R.drawable.empty_user)
                        choicePilot.text = selected[0].text
                    }
                )
                .apply {
                    title = "Choose Your Pilot"
                    data = arrayListOf<Item>().also {
                        list.map { pilot ->
                            it.add(Item(pilot.name ?: ""))
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

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(addInteraction: AddInteraction) = AddFlightOne().apply {
            this.addInteraction = addInteraction
        }
    }
}