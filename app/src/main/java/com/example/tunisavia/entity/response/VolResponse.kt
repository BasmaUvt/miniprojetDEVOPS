package com.example.tunisavia.entity.response

import com.example.tunisavia.entity.TechnicalParam

class VolResponse {
    var results: List<TechnicalParam> = mutableListOf()
    override fun toString(): String {
        return "VolResponse(results=$results)"
    }
}
