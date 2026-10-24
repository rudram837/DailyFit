package com.app.gym.View.Activity.BMI

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.app.gym.Model.BMI.Gender
import com.app.gym.Model.BMI.HeightUnit
import com.app.gym.Model.BMI.WeightUnit
import com.app.gym.R
import com.app.gym.Repository.BMI.BMIRepository
import com.app.gym.ViewModel.BMI.BMICalculatorViewModel
import com.app.gym.ViewModel.BMI.BMICalculatorViewModelFactory
import com.app.gym.databinding.ActivityBmicalculatorBinding

class BMICalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBmicalculatorBinding
    private lateinit var viewModel: BMICalculatorViewModel
    private var currentHeightUnit = HeightUnit.CM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBmicalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = BMIRepository()
        val factory = BMICalculatorViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BMICalculatorViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpClicks()
        observeData()
        restoreUIState()
    }


    @SuppressLint("DefaultLocale")
    private fun setUpClicks() {

        binding.sliderHeight.addOnChangeListener { _, value, _ ->
            val displayText = if (currentHeightUnit == HeightUnit.FT) {
                String.format("%.2f", value)
            } else {
                value.toInt().toString()
            }
            binding.tvHeightValue.text = displayText
            viewModel.onHeightChanged(value)
        }

        binding.sliderWeight.addOnChangeListener { _, value, _ ->
            binding.tvWeightValue.text = String.format("%.1f", value)
            viewModel.onWeightChanged(value)
        }

        binding.btnAgePlus.setOnClickListener {
            val age = binding.tvAgeValue.text.toString().toInt() + 1
            binding.tvAgeValue.text = age.toString()
            viewModel.onAgeChanged(age)
        }


        binding.btnAgeMinus.setOnClickListener {
            val age = binding.tvAgeValue.text.toString().toInt() - 1
            binding.tvAgeValue.text = age.toString()
            viewModel.onAgeChanged(age)
        }

        binding.btnCalculate.setOnClickListener {
//            binding.cardResult.visibility = View.GONE
//            binding.cardSuggestions.visibility = View.GONE
            viewModel.calculateBMI()
        }

        binding.toggleHeight.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.btnCM -> {
                    val cmValue = (binding.sliderHeight.value * 30.48f).coerceIn(100f, 250f)

                    currentHeightUnit = HeightUnit.CM
                    viewModel.changeHeightUnit(HeightUnit.CM)
                    binding.sliderHeight.valueFrom = 100f
                    binding.sliderHeight.valueTo = 250f
                    binding.sliderHeight.value = cmValue
                    binding.tvHeightValue.text = cmValue.toInt().toString()
                    viewModel.onHeightChanged(cmValue)
                }

                R.id.btnFT -> {
                    val ftValue = (binding.sliderHeight.value / 30.48f).coerceIn(3f, 8f)

                    currentHeightUnit = HeightUnit.FT
                    viewModel.changeHeightUnit(HeightUnit.FT)
                    binding.sliderHeight.valueFrom = 3f
                    binding.sliderHeight.valueTo = 8f
                    binding.sliderHeight.value = ftValue
                    binding.tvHeightValue.text = String.format("%.2f", ftValue)
                    viewModel.onHeightChanged(ftValue)
                }
            }
        }

        binding.toggleWeight.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.btnKG -> {
                    val kgValue = (binding.sliderWeight.value * 0.453592f)
                        .coerceIn(30f, 200f)

                    viewModel.changeWeightUnit(WeightUnit.KG)
                    binding.sliderWeight.valueFrom = 30f
                    binding.sliderWeight.valueTo = 200f
                    binding.sliderWeight.value = kgValue
                    binding.tvWeightValue.text = String.format("%.1f", kgValue)
                    viewModel.onWeightChanged(kgValue)
                }

                R.id.btnLB -> {
                    val lbValue = (binding.sliderWeight.value / 0.453592f)
                        .coerceIn(66f, 440f)

                    viewModel.changeWeightUnit(WeightUnit.LB)
                    binding.sliderWeight.valueFrom = 66f
                    binding.sliderWeight.valueTo = 440f
                    binding.sliderWeight.value = lbValue
                    binding.tvWeightValue.text = String.format("%.1f", lbValue)
                    viewModel.onWeightChanged(lbValue)
                }
            }
        }

        binding.toggleGender.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            when (checkedId) {
                R.id.btnMale -> viewModel.onGenderChanged(Gender.MALE)
                R.id.btnFemale -> viewModel.onGenderChanged(Gender.FEMALE)
            }
        }
    }


    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun observeData() {

        viewModel.result.observe(this) { result ->

            binding.cardResult.visibility = View.VISIBLE
            binding.cardSuggestions.visibility = View.VISIBLE

            binding.tvBMIValue.text = String.format("%.1f", result.bmi)
            binding.tvBMIStatus.text = result.status

            val color = when (result.status) {
                "Underweight" -> "#2196F3"
                "Healthy" -> "#4CAF50"
                "Overweight" -> "#FF9800"
                else -> "#F44336" // Obese
            }

            binding.progressBMI.setIndicatorColor(color.toColorInt())
            binding.tvBMIStatus.setTextColor(color.toColorInt())

            val progress = ((result.bmi / 40f) * 100).toInt().coerceIn(0, 100)
            binding.progressBMI.setProgress(progress, true)

            binding.tvIdealWeight.text = "✅ Ideal Weight Range: ${result.idealWeight}"
            binding.tvCalories.text = "🔥 Calories: ~${result.calories} kcal / day"
            binding.tvWorkout.text = "💪 Workout: ${result.workout}"
            binding.tvDiet.text = "🥗 Diet Advice: ${result.diet}"
        }
    }
    private fun restoreUIState() {
        val checkedGenderId = if (viewModel.getGender() == Gender.MALE) R.id.btnMale else R.id.btnFemale
        binding.toggleGender.check(checkedGenderId)
    }

    private fun updateGenderUI(gender: Gender) {

        binding.toggleGender.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.btnMale -> viewModel.onGenderChanged(Gender.MALE)
                R.id.btnFemale -> viewModel.onGenderChanged(Gender.FEMALE)
            }
        }
    }
}