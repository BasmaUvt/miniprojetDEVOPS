package com.example.tunisavia.base

import android.app.Application
import android.util.Log
import com.example.tunisavia.entity.*
import com.example.tunisavia.utils.SSLCertificateHandler
import com.google.gson.Gson
import java.net.URISyntaxException
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class SocketInstance : Application() {
    lateinit var mSocket: Socket
    var cities: MutableList<String> = mutableListOf()
    var allCountriesList: MutableList<String?> = mutableListOf()
    var allCitiesList: MutableMap<String, MutableList<String>> = mutableMapOf()

    //pilot
    var allPilotList: MutableList<Pilot> = mutableListOf()
    //plane
    var allPlanesList: MutableList<Plane> = mutableListOf()
    //places
    var allPlacesList: MutableList<String> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        SSLCertificateHandler.nuke()

        CoroutineScope(Dispatchers.Default).launch {
            generateCountriesList()
        }

        CoroutineScope(Dispatchers.Default).launch {
            generatePilotList()
        }

        CoroutineScope(Dispatchers.Default).launch {
            generatePlaneList()
        }
        CoroutineScope(Dispatchers.Default).launch {
            allPlacesList.add("HME")
            allPlacesList.add("HBBCO")
            allPlacesList.add("AZR")
            allPlacesList.add("MAD")
            allPlacesList.add("ELM10")
            allPlacesList.add("MEL10")
            allPlacesList.add("RKFNO")
            allPlacesList.add("BMS20")
        }
    }

    private fun generatePlaneList() {
        try {
            try {
                val country = Gson().fromJson(loadJSONFromAsset("Plane.json"), AllPlane::class.java)
                allPlanesList = country.planes
            } catch (ex: java.lang.Exception) {
                Log.i("Alaaaaaaaaa", "Exception: ${ex.message}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




    private suspend fun generatePilotList() {
        try {
            try {
                val country = Gson().fromJson(loadJSONFromAsset("Pilot.json"), AllPilot::class.java)
                allPilotList = country.pilots
            } catch (ex: java.lang.Exception) {
                Log.i("Alaaaaaaaaa", "Exception: ${ex.message}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // end list pilot
//
    private fun loadJSONFromAsset(nameFile: String): String {
        var json = ""
        try {
            applicationContext?.assets?.let {
                val inputStream: InputStream = it.open(nameFile)
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                json = String(buffer, Charset.forName("UTF-8"))
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    private suspend fun generateCountriesList() {
        try {
            try {
                val country = Gson().fromJson(loadJSONFromAsset("Test.json"), Countries::class.java)
                country.countries?.map {
                    allCountriesList.add(it.countryName)
                    it.states?.map { state ->
                        cities = state.cities ?: mutableListOf()
                    }
                    allCitiesList.put(it.countryName ?: "", cities)
                }

            } catch (ex: java.lang.Exception) {
                Log.i("Alaaaaaaaaa", "Exception: ${ex.message}")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun setSocket() {
        try {
// "http://10.0.2.2:3000" is the network your Android emulator must use to join the localhost network on your computer
// "http://localhost:3000/" will not work
// If you want to use your physical phone you could use your ip address plus :3000
// This will allow your Android Emulator and physical device at your home to connect to the server
            mSocket = IO.socket("https://staravion.herokuapp.com")
        } catch (e: URISyntaxException) {

        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
}