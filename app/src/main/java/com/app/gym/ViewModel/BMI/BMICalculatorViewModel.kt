package com.app.gym.ViewModel.BMI


import androidx.lifecycle.*
import com.app.gym.Model.*
import com.app.gym.Model.BMI.BMIRequest
import com.app.gym.Model.BMI.BMIResult
import com.app.gym.Model.BMI.Gender
import com.app.gym.Model.BMI.HeightUnit
import com.app.gym.Model.BMI.WeightUnit
import com.app.gym.Repository.BMI.BMIRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BMICalculatorViewModel(
    private val repository: BMIRepository
) : ViewModel() {

    private var gender = Gender.MALE
    private var height = 170f
    private var weight = 65f
    private var age = 18
    private var heightUnit = HeightUnit.CM
    private var weightUnit = WeightUnit.KG
    private val _result = MutableLiveData<BMIResult>()
    val result: LiveData<BMIResult> = _result

    fun onGenderChanged(value: Gender) {
        gender = value
    }

    fun onHeightChanged(value: Float) {
        height = value
    }

    fun onWeightChanged(value: Float) {
        weight = value
    }

    fun onAgeChanged(value: Int) {
        age = value
    }
    fun changeHeightUnit(unit: HeightUnit) {
        heightUnit = unit
    }

    fun changeWeightUnit(unit: WeightUnit) {
        weightUnit = unit
    }

    fun getGender(): Gender = gender

    fun calculateBMI() {

        viewModelScope.launch(Dispatchers.Default) {

            var finalHeight = height
            var finalWeight = weight

            if (heightUnit == HeightUnit.FT) {
                finalHeight *= 30.48f
            }

            if (weightUnit == WeightUnit.LB) {
                finalWeight *= 0.453592f
            }

            val request = BMIRequest(
                gender,
                finalHeight,
                finalWeight,
                age
            )

            val result = repository.calculateBMI(request)
            _result.postValue(result)
        }
    }
}