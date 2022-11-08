package com.example.tunisavia.entity

import com.google.gson.annotations.SerializedName

class TechnicalParam {

    var id: Int? = -1
    var type: String? = "undefined"

    @SerializedName("name_vol")
    var nameVol: String? = "undefined"

    @SerializedName("number_passenger")
    var numberPassenger: Int? = 0

    @SerializedName("date_dep")
    var dateDep: String? = "undefined"

    @SerializedName("zone_dep")
    var zoneDep: String? = "undefined"

    @SerializedName("date_arr")
    var dateArr: String? = "undefined"

    @SerializedName("zone_arr")
    var zoneArr: String? = "undefined"

    @SerializedName("is_checked")
    var isChecked: Boolean? = true

    @SerializedName("pilot_id")
    var pilotId: Int? = 0

    @SerializedName("plane_id")
    var planeId: Int? = 0

    override fun toString(): String {
        return "TechnicalParam(id=$id, type=$type, nameVol=$nameVol, numberPassenger=$numberPassenger, dateDep=$dateDep, zoneDep=$zoneDep, dateArr=$dateArr, zoneArr=$zoneArr, isChecked=$isChecked, pilotId=$pilotId, planeId=$planeId)"
    }
}