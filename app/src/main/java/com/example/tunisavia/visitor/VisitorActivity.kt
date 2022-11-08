package com.example.tunisavia.visitor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.tunisavia.MainActivity
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.base.SocketInstance
import com.example.tunisavia.databinding.ActivityVisitorBinding
import com.example.tunisavia.entity.Pilot
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.signup.SignInActivity
import com.example.tunisavia.update.UpdateVolActivity
import com.example.tunisavia.utils.Toolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hlab.fabrevealmenu.helper.OnFABMenuSelectedListener
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URISyntaxException

@ExperimentalCoroutinesApi
class VisitorActivity : BaseActivity(), OnFABMenuSelectedListener,
    Toolbar.ToolbarInteractionInterface {

    lateinit var binding: ActivityVisitorBinding
    val defineLayoutBinding: () -> View
        get() = {
            binding = ActivityVisitorBinding.inflate(layoutInflater)
            val view = binding.root
            view
        }

    lateinit var volParam: TechnicalParam

    lateinit var number: TextView
    lateinit var type: TextView
    lateinit var nameVolDetails: TextView
    lateinit var pilotName: TextView

    lateinit var timeDepDetails: TextView
    lateinit var dateDepDetails: TextView
    lateinit var zoneDepDetails: TextView

    lateinit var timeArrDetails: TextView
    lateinit var dateArrDetails: TextView
    lateinit var zoneArrDetails: TextView

    var isReadyToShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(defineLayoutBinding())
        app = (this.application as SocketInstance)
        sharedPreference = providePreferencesHelper(this)
        binding.customToolbar.toolbarInteractionInterface = this

        try {
            //mSocket = IO.socket("http://localhost:8000")
            //mSocket = IO.socket("http://192.168.1.137:3000")
            mSocket = IO.socket("https://staravion.herokuapp.com")
        } catch (e: URISyntaxException) {
            Log.i("MainActivityAla", "onCreate: ${e.message}")
        }

        mSocket?.on(Socket.EVENT_CONNECT) {
            Log.d("MainActivityAla", "Socket Connected!")

        }
        mSocket?.connect()

        val isFromTeck = sharedPreference.getBoolean("fromTeck", true)
        binding.logoutBtnList.visibility = if (isFromTeck) {
            binding.materialDesignAndroidFloatingActionMenu.visibility = View.VISIBLE
            View.GONE
        } else {
            binding.materialDesignAndroidFloatingActionMenu.visibility = View.GONE
            View.VISIBLE
        }

        binding.checkBtn.setOnClickListener {
            sharedPreference.putInt("volParamId", volParam.id ?: 0)
            sharedPreference.putString("volParamName", volParam.nameVol.toString())
            startActivity(Intent(this@VisitorActivity, UpdateVolActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            startActivity(Intent(this@VisitorActivity, SignInActivity::class.java))
        }

        binding.logoutBtnList2.setOnClickListener {
            startActivity(Intent(this@VisitorActivity, SignInActivity::class.java))
        }

        binding.logoutBtnList.setOnClickListener {
            startActivity(Intent(this@VisitorActivity, SignInActivity::class.java))
        }

        volParam = Gson().fromJson(intent.extras?.getString("flight", ""), TechnicalParam::class.java)
        Log.i("ListPlanesActivityAladin", "onCreate: $volParam")

        number = findViewById(R.id.number_passenger)
        type = findViewById(R.id.type_plane_txt)
        nameVolDetails = findViewById(R.id.name_vol_details)
        pilotName = findViewById(R.id.pilot_name_txt)

        timeDepDetails = findViewById(R.id.time_dep_details)
        dateDepDetails = findViewById(R.id.date_dep_details)
        zoneDepDetails = findViewById(R.id.zone_dep_details)

        timeArrDetails = findViewById(R.id.time_arr_details)
        dateArrDetails = findViewById(R.id.date_arr_details)
        zoneArrDetails = findViewById(R.id.zone_arr_details)

        number.text = volParam.numberPassenger.toString()
        type.text = volParam.type.toString()
        nameVolDetails.text = volParam.nameVol.toString()

        //depart
        val time = volParam.dateDep?.split(" ")?.get(3)
        val date = volParam.dateDep?.split(" ")?.get(1)
        val dateDep = "${date?.split("/")?.get(0)}.${date?.split("/")?.get(1)}"
        dateDepDetails.text = dateDep
        timeDepDetails.text = time
        zoneDepDetails.text = volParam.zoneDep

        //arrive
        val timeArr = volParam.dateArr?.split(" ")?.get(3)
        val dateArr = volParam.dateArr?.split(" ")?.get(1)
        val dateArrSplited = "${dateArr?.split("/")?.get(0)}.${dateArr?.split("/")?.get(1)}"
        dateArrDetails.text = dateArrSplited
        timeArrDetails.text = timeArr
        zoneArrDetails.text = volParam.zoneArr


        val arrayTutorialType = object : TypeToken<Array<Pilot>>() {}.type
        val listPilot = app.allPilotList
        listPilot.map { p ->
            Log.i("Aladin", "${volParam.pilotId}  list: $p")
        }
        pilotName.text = listPilot.find { it.id == volParam.pilotId }?.name

        runBlocking {
            val service = NetworkModuleFactory.makeService()
            val tech = TechnicalParam()
            tech.id = volParam.id
            val listTech = service.getFlightById(tech).results
            Log.i("lifecycleScope", "onCreate: $listTech")
            if (listTech[0].isChecked == true) {
                findViewById<TextView>(R.id.warning_notice).visibility = View.GONE
                findViewById<TextView>(R.id.not_ready_notice).visibility = View.GONE
                findViewById<TextView>(R.id.success_notice).visibility = View.VISIBLE

                binding.materialDesignAndroidFloatingActionMenu.visibility = View.GONE
                binding.logoutBtnList.visibility = View.VISIBLE
            } else {
                if (isFromTeck) {
                    binding.materialDesignAndroidFloatingActionMenu.visibility = View.VISIBLE
                    binding.logoutBtnList.visibility = View.GONE
                } else {
                    binding.materialDesignAndroidFloatingActionMenu.visibility = View.GONE
                    binding.logoutBtnList.visibility = View.VISIBLE
                }
            }
        }

        binding.toolbarBack.setOnClickListener {
            finish()
        }

        mSocket?.on("check") { args ->
            Log.i("VisitorActivityAla", "onCreate: ${args[0]}")
            if (args[0] != null) {
                val counter = (args[0] as String).split(" ")[0]
                val ids = (args[0] as String).split(" ")[1]
                if (ids.toInt() == volParam.id) {
                    runOnUiThread {
                        when (counter) {
                            "ready" -> {
                                isReadyToShow = false
                                binding.materialDesignAndroidFloatingActionMenu.visibility = View.GONE
                                binding.logoutBtnList.visibility = View.VISIBLE
                                findViewById<TextView>(R.id.warning_notice).visibility = View.GONE
                                findViewById<TextView>(R.id.not_ready_notice).visibility = View.GONE
                                findViewById<TextView>(R.id.success_notice).visibility = View.VISIBLE
                            }
                            "canceled" -> {
                                isReadyToShow = true
                                binding.materialDesignAndroidFloatingActionMenu.visibility = View.VISIBLE
                                binding.logoutBtnList.visibility = View.GONE
                                findViewById<TextView>(R.id.warning_notice).visibility = View.GONE
                                findViewById<TextView>(R.id.not_ready_notice).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.success_notice).visibility = View.GONE
                            }
                            else -> {
                                isReadyToShow = true
                                binding.materialDesignAndroidFloatingActionMenu.visibility = View.VISIBLE
                                binding.logoutBtnList.visibility = View.GONE
                                findViewById<TextView>(R.id.warning_notice).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.not_ready_notice).visibility = View.GONE
                                findViewById<TextView>(R.id.success_notice).visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

    }

    override fun clickQuitHandling() {
        startActivity(Intent(this@VisitorActivity, SignInActivity::class.java))
    }

    override fun onMenuItemSelected(view: View?, id: Int) {
        if (id == R.id.menu_log_out) {
            startActivity(Intent(this@VisitorActivity, SignInActivity::class.java))
        } else if (id == R.id.menu_check) {
            sharedPreference.putInt("volParamId", volParam.id ?: 0)
            sharedPreference.putString("volParamName", volParam.nameVol.toString())
            startActivity(Intent(this@VisitorActivity, UpdateVolActivity::class.java))
        }
    }
}