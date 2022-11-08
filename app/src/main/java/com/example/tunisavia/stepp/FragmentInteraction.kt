package com.example.tunisavia.stepp

import com.example.tunisavia.entity.Pilot

object FragmentInteraction {

    interface BasicInfoFragmentInteraction {
        fun onClick(index: Int ?= -1)
    }

    interface PilotInteraction {
        fun onClick(pilot: Pilot, position: Int)
    }

}