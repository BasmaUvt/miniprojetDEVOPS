package com.example.tunisavia.stepp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.tunisavia.R
import com.example.tunisavia.base.BaseFragment
import com.example.tunisavia.entity.NotificationData
import com.example.tunisavia.entity.PushNotification
import com.example.tunisavia.entity.TeckParam
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.update.UpdateVolActivity
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*
import java.net.URISyntaxException

@ExperimentalCoroutinesApi
class SignUpFive : BaseFragment() {
    var basicInfoFragment: FragmentInteraction.BasicInfoFragmentInteraction? = null

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
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_signup_five, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val confirm: Button = view.findViewById(R.id.next_check_five)
        val volId = sharedPreference.getInt("volParamId", 0)
        val vol = sharedPreference.getString("volParamName", "")
        confirm.setOnClickListener {
            runBlocking {
                val service = NetworkModuleFactory.makeService()
                val tech = TeckParam()
                tech.id = volId
                tech.isready = true
                val listTech = service.updateTechnic(tech).message
            }

            mSocket.emit("check", "ready $volId")

            val title = "check"
            val messageR = "The flight $vol is ready"
            //val recipientToken = etToken.text.toString()
            if(title.isNotEmpty() && messageR.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, messageR),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
            }

            //(activity as UpdateVolActivity).finish()
            (activity as UpdateVolActivity).returnToList()
        }

        val cancel: Button = view.findViewById(R.id.cancel_check_five)
        cancel.setOnClickListener {
            val title = "check"
            val messageR = "The flight $vol is not ready"
            //val recipientToken = etToken.text.toString()
            if(title.isNotEmpty() && messageR.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, messageR),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
            }

            runBlocking {
                val service = NetworkModuleFactory.makeService()
                val tech = TeckParam()
                tech.id = volId
                tech.isready = false
                val listTech = service.updateTechnic(tech).message
            }

            mSocket.emit("check", "canceled $volId")
            (activity as UpdateVolActivity).finish()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as UpdateVolActivity).changeToolbarTitle("Ops")
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        const val TOPIC = "/topics/myTopic2"

        @JvmStatic
        fun newInstance(basicInfoFragment: FragmentInteraction.BasicInfoFragmentInteraction) =
            SignUpFive().apply {
                this.basicInfoFragment = basicInfoFragment
            }
    }
}