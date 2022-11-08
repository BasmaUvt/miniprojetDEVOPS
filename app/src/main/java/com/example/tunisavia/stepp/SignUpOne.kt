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
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.stepp.SignUpFive.Companion.TOPIC
import com.example.tunisavia.update.UpdateVolActivity
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.net.URISyntaxException

@ExperimentalCoroutinesApi
class SignUpOne : BaseFragment() {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_signup_one, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonNext: Button = view.findViewById(R.id.next_check_one)
        buttonNext.setOnClickListener {
            val title = "check"
            val messageR = "Check is done"
            //val recipientToken = etToken.text.toString()
            if(title.isNotEmpty() && messageR.isNotEmpty()) {
                PushNotification(
                    NotificationData(title, messageR),
                    TOPIC
                ).also {
                    sendNotification(it)
                }
            }
            basicInfoFragment?.onClick()
        }

        val buttonCancel: Button = view.findViewById(R.id.cancel_check_one)
        buttonCancel.setOnClickListener {
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
        (activity as UpdateVolActivity).changeToolbarTitle("Checking")
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(basicInfoFragment: FragmentInteraction.BasicInfoFragmentInteraction) =
            SignUpOne().apply {
                this.basicInfoFragment = basicInfoFragment
            }
    }
}