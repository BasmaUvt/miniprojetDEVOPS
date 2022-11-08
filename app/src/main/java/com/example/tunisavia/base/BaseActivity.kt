package com.example.tunisavia.base

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.tunisavia.R
import com.example.tunisavia.local.db.AppDatabase
import com.example.tunisavia.local.db.SharedPreference
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.*

open class BaseActivity : AppCompatActivity() {

    lateinit var sharedPreference: SharedPreference
    lateinit var appDatabase: AppDatabase
    var mSocket: Socket? = null
    lateinit var app: SocketInstance

    private var randomNumber: Int = 0
    private var integerList: MutableList<Int>? = mutableListOf()

    fun getRandomNumber(): Int {
        val rand = Random()
        randomNumber = (0..100).random()
        if (integerList?.contains(randomNumber) == true) {
            getRandomNumber()
        } else {
            integerList?.add(randomNumber)
        }
        return randomNumber
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        app = (this.application as SocketInstance)
        sharedPreference = providePreferencesHelper(this)
        appDatabase = provideAppDatabase(this)
    }

    fun initVisibilityPassword(inputFullName: EditText) {
        inputFullName.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.ic_baseline_visibility_off_24,
            0
        )
        inputFullName.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    fun providePreferencesHelper(context: Context): SharedPreference = SharedPreference(context)

    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.NAME
        )
            //.fallbackToDestructiveMigration()
            .build()
    }
}