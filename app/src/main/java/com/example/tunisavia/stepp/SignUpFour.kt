package com.example.tunisavia.stepp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tunisavia.MainActivity
import com.example.tunisavia.MyFirebaseMessagingService
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseFragment
import com.example.tunisavia.base.Constants
import com.example.tunisavia.entity.NotificationData
import com.example.tunisavia.entity.PushNotification
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.entity.User
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.update.UpdateVolActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.util.*

@ExperimentalCoroutinesApi
class SignUpFour : BaseFragment() {
    var basicInfoFragment: FragmentInteraction.BasicInfoFragmentInteraction? = null
    lateinit var confirm: Button
    lateinit var cancel: Button



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
    ): View? = inflater.inflate(R.layout.fragment_signup_four, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pilotId = sharedPreference.getInt("pilotId", 0)
        //Token
        /*MyFirebaseMessagingService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            etToken.setText(it.token)
        }*/


        confirm = view.findViewById(R.id.next_check_four)
        confirm.setOnClickListener {
            val title = "check"
            val messageR = "Cargo and passengers are checked"
            //val recipientToken = etToken.text.toString()
            if(title.isNotEmpty() && messageR.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, messageR),
                    SignUpFive.TOPIC
                ).also {
                    sendNotification(it)
                }
            }
            basicInfoFragment?.onClick()
        }

        cancel = view.findViewById(R.id.cancel_check_four)
        cancel.setOnClickListener {
            /*val title = "check"
            val messageR = "The plane xx is cancled"
            //val recipientToken = etToken.text.toString()
            if(title.isNotEmpty() && messageR.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, messageR),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
            }*/
            val vol = sharedPreference.getString("volParamName", "")
            val volId = sharedPreference.getInt("volParamId", 0)
            mSocket.emit("check", "canceled $volId")
            (activity as UpdateVolActivity).finish()
        }
    }



    override fun onResume() {
        super.onResume()
        (activity as UpdateVolActivity).changeToolbarTitle("Cargo and passengers")
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(basicInfoFragment: FragmentInteraction.BasicInfoFragmentInteraction) =
            SignUpFour().apply {
                this.basicInfoFragment = basicInfoFragment
            }
    }
}