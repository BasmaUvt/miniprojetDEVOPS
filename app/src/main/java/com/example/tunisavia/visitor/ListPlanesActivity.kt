package com.example.tunisavia.visitor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tunisavia.MainActivity
import com.example.tunisavia.base.BaseActivity
import com.example.tunisavia.databinding.ActivityListPlanesBinding
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.entity.response.VolResponse
import com.example.tunisavia.remote.NetworkModuleFactory
import com.example.tunisavia.signup.SignInActivity
import com.example.tunisavia.visitor.adapter.PlaneAdapter
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class ListPlanesActivity : BaseActivity(), VisitorInteraction, TextView.OnEditorActionListener {

    lateinit var binding: ActivityListPlanesBinding
    val defineLayoutBinding: () -> View
        get() = {
            binding = ActivityListPlanesBinding.inflate(layoutInflater)
            val view = binding.root
            view
        }

    private val planeAdapter by lazy {
        PlaneAdapter(this)
    }

    var listPlanes: MutableList<TechnicalParam> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(defineLayoutBinding())
        sharedPreference = providePreferencesHelper(this)
        val isFromTeck = sharedPreference.getBoolean("fromTeck", true)
        binding.addBtn.visibility = if (isFromTeck) View.VISIBLE else View.GONE

        //search
        binding.searchWidget.searchBoxCollapsed.setOnClickListener {
            binding.searchWidget.searchExpandedBox.visibility = View.VISIBLE
        }

        binding.searchWidget.searchExpandedBackButton.setOnClickListener {
            binding.searchWidget.searchExpandedBox.visibility = View.GONE
        }

        binding.searchWidget.searchExpandedEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isEmpty()) planeAdapter.addList(listPlanes)
                else {
                    val list = listPlanes.filter { it.nameVol?.contains(p0.toString()) == true }.toMutableList()
                    planeAdapter.addList(list)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        binding.searchPart.noPlaneTxt.visibility = View.GONE
        binding.searchPart.logoutBtnList.visibility = View.GONE

        binding.searchPart.loadingAnimList.visibility = View.VISIBLE
        binding.searchPart.searchBarCustom.setOnEditorActionListener(this)

        lifecycleScope.launchWhenStarted {
            try {
                val service = NetworkModuleFactory.makeService()
                val user: VolResponse = service.getAllVol()
                listPlanes = user.results.toMutableList()
            } catch (ex: Exception) {
                Log.i("testtttssfdfzefezf", "exception: ${ex.message}")
            }
            binding.searchPart.loadingAnimList.visibility = View.GONE
            //binding.searchPart.materialDesignAndroidFloatingActionMenu.visibility = View.VISIBLE
            binding.searchPart.planeRv.visibility = if (listPlanes.isEmpty()) {
                binding.searchPart.noPlaneTxt.visibility = View.VISIBLE
                View.GONE
            } else {
                binding.searchPart.noPlaneTxt.visibility = View.GONE
                View.VISIBLE
            }
            planeAdapter.checkVisitor(isFromTeck)
            planeAdapter.addList(listPlanes)
        }

        binding.addBtn.setOnClickListener {
            val i = Intent(this@ListPlanesActivity, MainActivity::class.java)
            startActivity(i)
        }

        binding.logoutBtnList2.setOnClickListener {
            val i = Intent(this@ListPlanesActivity, SignInActivity::class.java)
            startActivity(i)
        }

        binding.searchPart.apply {

            with(planeRv) {
                layoutManager = LinearLayoutManager(this@ListPlanesActivity)
                adapter = planeAdapter
            }

            logoutBtnList.setOnClickListener {
                startActivity(Intent(this@ListPlanesActivity, SignInActivity::class.java))
            }
        }
    }

    override fun onClick(plane: TechnicalParam) {
        val json = Gson().toJson(plane)
        Log.i("aaaaaaaaaaaaaa", "onClick: $json")
        val i = Intent(this@ListPlanesActivity, VisitorActivity::class.java)
        i.putExtra("flight", json)
        startActivity(i)
    }

    override fun removePlane(plane: TechnicalParam) {
        val service = NetworkModuleFactory.makeService()
        val tech = TechnicalParam()
        tech.id = plane.id
        binding.searchPart.loadingAnimList.visibility = View.VISIBLE
        lifecycleScope.launchWhenCreated {
            val message = service.deleteFlightById(plane).message
            Log.i("azertklm", "removePlane: $message")
            if (message == "No documents matched the query. Deleted 0 documents.") {
                if (listPlanes.remove(plane)) {
                    binding.searchPart.loadingAnimList.visibility = View.GONE
                    binding.searchPart.planeRv.visibility = if (listPlanes.isEmpty()) {
                        binding.searchPart.noPlaneTxt.visibility = View.VISIBLE
                        View.GONE
                    } else {
                        binding.searchPart.noPlaneTxt.visibility = View.GONE
                        View.VISIBLE
                    }
                    planeAdapter.addList(listPlanes)
                }
            }
        }
    }

    override fun addVol() {
        val i = Intent(this@ListPlanesActivity, MainActivity::class.java)
        startActivity(i)
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        binding.searchPart.searchBarCustom.startSearching(true)
        return false
    }
}