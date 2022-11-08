/*
 * Copyright 2020 Ayomide Falobi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tunisavia

import androidx.lifecycle.ViewModel
import com.example.tunisavia.entity.StepperSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@ExperimentalCoroutinesApi
class StepperViewModel : ViewModel() {

    private val _stepperSettings = MutableStateFlow(StepperSettings())

    val stepperSettings: StateFlow<StepperSettings> get() = _stepperSettings

    fun updateStepper(newStepperSettings: StepperSettings) {
        _stepperSettings.value = newStepperSettings
    }
}
