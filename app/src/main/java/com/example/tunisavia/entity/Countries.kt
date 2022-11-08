package com.example.tunisavia.entity

class Countries {
    var countries: MutableList<AllCountries> ? = null
}

class AllCountries {
    var states: MutableList<States> ? = null
    var countryName: String? = null
    override fun toString(): String {
        return "Countries(states=$states, countryName=$countryName)"
    }
}

class States {
    var cities: MutableList<String>? = null
    var stateName: String? = null
    override fun toString(): String {
        return "States(cities=$cities, stateName=$stateName)"
    }
}