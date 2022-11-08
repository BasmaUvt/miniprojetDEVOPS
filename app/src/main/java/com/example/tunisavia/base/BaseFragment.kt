package com.example.tunisavia.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.tunisavia.entity.PushNotification
import com.example.tunisavia.local.db.AppDatabase
import com.example.tunisavia.local.db.SharedPreference
import com.example.tunisavia.remote.NetworkModuleFactory
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.util.*

open class BaseFragment : Fragment() {
    lateinit var mSocket: Socket
    lateinit var app: SocketInstance

    lateinit var sharedPreference: SharedPreference
    lateinit var appDatabase: AppDatabase

    private var randomNumber: Int = 0
    private var integerList: MutableList<Int>? = mutableListOf()

    fun providePreferencesHelper(context: Context): SharedPreference = SharedPreference(context)

    private fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.NAME
        )
            //.fallbackToDestructiveMigration()
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = (activity?.application as SocketInstance)

    }

    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val service = NetworkModuleFactory.makeServiceNotif()
            val response = service.postNotification(notification)
            if(response.isSuccessful) {
                //Log.d("sendNotification", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("sendNotification", "errorBody "+response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e("sendNotification", "Exception "+e.message.toString())
        }
    }

    fun getRandomNumber(): Int {
        val rand = Random()
        randomNumber = (0..100).random()
        if (integerList?.contains(randomNumber) == true) {
            getRandomNumber()
        } else {
            integerList?.add(randomNumber)
        }
        Log.i("qmqmqmqmqmmq", "getRandomNumber: $randomNumber")
        return randomNumber
    }
}