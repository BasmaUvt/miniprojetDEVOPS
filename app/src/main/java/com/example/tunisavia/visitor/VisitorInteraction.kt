package com.example.tunisavia.visitor

import com.example.tunisavia.entity.TechnicalParam

interface VisitorInteraction {
    fun onClick(plane: TechnicalParam)
    fun removePlane(plane: TechnicalParam)
    fun addVol()
}