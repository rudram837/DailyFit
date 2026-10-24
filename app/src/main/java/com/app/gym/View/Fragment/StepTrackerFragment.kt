package com.app.gym.View.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.gym.Model.StepTracker.StepTrackerUiState
import com.app.gym.R
import com.app.gym.Utils.Utility
import com.app.gym.ViewModel.StepTracker.StepTrackerViewModel
import com.app.gym.ViewModel.StepTracker.StepTrackerViewModelFactory
import com.app.gym.databinding.FragmentStepTrackerBinding
import kotlinx.coroutines.launch
import java.util.Locale

class StepTrackerFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentStepTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private val viewModel: StepTrackerViewModel by viewModels {
        StepTrackerViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager =
            requireContext().getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager

        stepSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTrackerBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        observeStepData()
        checkActivityPermission()

        binding.celebrationOverlay.visibility = View.GONE

        binding.root.postDelayed({
            if (_binding == null) return@postDelayed
            binding.celebrationOverlay.visibility = View.GONE
            showGoalCelebration()
        }, 2000L)
    }

    //    private fun observeStepData() {
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(
//                Lifecycle.State.STARTED
//            ) {
//                viewModel.uiState.collect { state ->
//                    updateUI(state)
//                }
//            }
//        }
//    }
    private fun observeStepData() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                launch {
                    viewModel.uiState.collect { state ->
                        updateUI(state)
                    }
                }

                launch {
                    viewModel.goalCelebration.collect { goal ->

                        if (_binding == null) return@collect

//                        binding.tvCelebrationSub.text =
//                            "${Utility.formatSteps(goal.toInt())} steps completed"

                        showGoalCelebration()
                    }
                }
            }
        }
    }

    private fun showGoalCelebration() {

        val binding = _binding ?: return

        val overlay = binding.celebrationOverlay
        val leftPopper = binding.ivPopperLeft
        val rightPopper = binding.ivPopperRight
        val celebrationText = binding.llCelebrationText
        val confetti = binding.confettiView

        confetti.bringToFront()


        /*
         * Prevent duplicate visual animations.
         */
        if (overlay.visibility == View.VISIBLE) {
            return
        }


        /*
         * Reset animation state.
         */
        leftPopper.animate().cancel()
        rightPopper.animate().cancel()
        celebrationText.animate().cancel()


        leftPopper.alpha = 0f
        leftPopper.scaleX = 0.5f
        leftPopper.scaleY = 0.5f
        leftPopper.rotation = 0f


        rightPopper.alpha = 0f
        rightPopper.scaleX = -0.5f
        rightPopper.scaleY = 0.5f
        rightPopper.rotation = 0f


        celebrationText.alpha = 0f
        celebrationText.scaleX = 0.85f
        celebrationText.scaleY = 0.85f


        /*
         * Clear any old particles.
         */
        confetti.stopAnimation()
        confetti.clearParticles()


        /*
         * Show celebration overlay.
         */
        overlay.visibility = View.VISIBLE

        confetti.visibility = View.VISIBLE
        leftPopper.visibility = View.VISIBLE
        rightPopper.visibility = View.VISIBLE
        celebrationText.visibility = View.VISIBLE


        /*
         * LEFT POPPER
         */
        leftPopper.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(250L)
            .start()


        /*
         * RIGHT POPPER
         *
         * Negative scaleX keeps it mirrored.
         */
        rightPopper.animate()
            .alpha(1f)
            .scaleX(-1f)
            .scaleY(1f)
            .setDuration(250L)
            .start()


        /*
         * CENTER TEXT
         */
        celebrationText.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(150L)
            .setDuration(350L)
            .start()


        /*
         * Fire both poppers after they enter.
         */
        overlay.postDelayed({

            val currentBinding =
                _binding ?: return@postDelayed

            val left =
                currentBinding.ivPopperLeft

            val right =
                currentBinding.ivPopperRight

            val particleView =
                currentBinding.confettiView


            /*
             * Make sure the views have been laid out.
             */
            if (
                left.width <= 0 ||
                right.width <= 0
            ) {
                return@postDelayed
            }


//            /*
//             * Burst origin points.
//             */
//            val leftX =
//                left.x + left.width * 0.92f
//
//            val leftY =
//                left.y + left.height * 0.32f
//
//            val rightX =
//                right.x + right.width * 0.08f
//
//            val rightY =
//                right.y + right.height * 0.32f
//
//
//            /*
//             * Start particle simulation.
//             */
//            particleView.burst(
//                leftX = leftX,
//                leftY = leftY,
//                rightX = rightX,
//                rightY = rightY
//            )
            val leftPoint = getPointInConfettiView(
                source = left,
                target = particleView,
                xFraction = 0.92f,
                yFraction = 0.32f
            )

            val rightPoint = getPointInConfettiView(
                source = right,
                target = particleView,
                xFraction = -0.92f,
                yFraction = 0.32f
            )

            particleView.burst(
                leftX = leftPoint.first,
                leftY = leftPoint.second,
                rightX = rightPoint.first,
                rightY = rightPoint.second
            )


            /*
             * LEFT POPPER RECOIL
             */
            left.animate()
                .rotation(-10f)
                .scaleX(1.12f)
                .scaleY(1.12f)
                .setDuration(120L)
                .withEndAction {

                    left.animate()
                        .rotation(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(180L)
                        .start()
                }
                .start()


            /*
             * RIGHT POPPER RECOIL
             */
            right.animate()
                .rotation(10f)
                .scaleX(-1.12f)
                .scaleY(1.12f)
                .setDuration(120L)
                .withEndAction {

                    right.animate()
                        .rotation(0f)
                        .scaleX(-1f)
                        .scaleY(1f)
                        .setDuration(180L)
                        .start()
                }
                .start()


            /*
             * Hide everything after celebration.
             */
//            currentBinding.celebrationOverlay.postDelayed({
//
//                hideGoalCelebration()
//
//            }, 6000L)
            /*
 * Poppers disappear shortly after firing.
 * Their extracted ribbons/stars continue falling.
 */
            currentBinding.celebrationOverlay.postDelayed({

                if (_binding == null) return@postDelayed

                currentBinding.ivPopperLeft.animate()
                    .alpha(0f)
                    .setDuration(250L)
                    .start()

                currentBinding.ivPopperRight.animate()
                    .alpha(0f)
                    .setDuration(250L)
                    .start()

            }, 550L)


            /*
             * FULL CELEBRATION = 10 SECONDS.
             *
             * The actual ribbon/star pieces continue falling
             * independently for the entire celebration.
             */
            currentBinding.celebrationOverlay.postDelayed({

                hideGoalCelebration()

            }, 6000L)

        }, 250L)
    }

    private fun getPointInConfettiView(
        source: View,
        target: View,
        xFraction: Float,
        yFraction: Float
    ): Pair<Float, Float> {

        val sourceLocation = IntArray(2)
        val targetLocation = IntArray(2)

        source.getLocationOnScreen(sourceLocation)
        target.getLocationOnScreen(targetLocation)

        val screenX =
            sourceLocation[0] +
                    source.width * xFraction

        val screenY =
            sourceLocation[1] +
                    source.height * yFraction

        val targetX =
            screenX - targetLocation[0]

        val targetY =
            screenY - targetLocation[1]

        return targetX to targetY
    }

    private fun hideGoalCelebration() {

        val binding = _binding ?: return

        binding.confettiView.stopAnimation()
        binding.confettiView.clearParticles()


        binding.ivPopperLeft.animate()
            .alpha(0f)
            .setDuration(150L)
            .start()


        binding.ivPopperRight.animate()
            .alpha(0f)
            .setDuration(150L)
            .start()


        binding.llCelebrationText.animate()
            .alpha(0f)
            .setDuration(150L)
            .withEndAction {

                val currentBinding =
                    _binding ?: return@withEndAction


                currentBinding.ivPopperLeft.visibility =
                    View.GONE

                currentBinding.ivPopperRight.visibility =
                    View.GONE

                currentBinding.llCelebrationText.visibility =
                    View.GONE

                currentBinding.confettiView.visibility =
                    View.GONE

                currentBinding.celebrationOverlay.visibility =
                    View.GONE
            }
            .start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(state: StepTrackerUiState) {

        binding.tvSteps.text =
            Utility.formatSteps(state.steps)
        binding.tvStepsToday.text = "Steps Today"
        binding.tvGoal.text = "Goal: ${Utility.formatSteps(state.goal)}"

        binding.tvRemaining.text =
            if (state.remaining > 0) {
                "${Utility.formatSteps(state.remaining)} steps remaining"
            } else {
                "Goal completed 🎉"
            }

        binding.progressSteps.max = 100
        binding.progressSteps.progress = state.progress

        binding.tvDailyGoal.text = "${Utility.formatSteps(state.goal)} steps"

        binding.tvDistance.text =
            String.format(
                Locale.getDefault(),
                "%.1f km",
                state.distanceKm
            )

        binding.tvCalories.text = "${state.calories} kcal"
        binding.tvActiveTime.text = state.activeTime

        if (!state.sensorAvailable) {
            binding.tvSteps.text = "0"
            binding.tvRemaining.text =
                "Step sensor unavailable"
        }
    }

    private fun checkActivityPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ),
                    ACTIVITY_RECOGNITION_REQUEST
                )
                return
            }
        }

        startStepSensor()
    }

    private fun startStepSensor() {

        if (stepSensor == null) {
            viewModel.setSensorUnavailable()
            return
        }

        sensorManager.registerListener(
            this,
            stepSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    private fun stopStepSensor() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return

        if (
            event.sensor.type != Sensor.TYPE_STEP_COUNTER
        ) return

        val currentSensorSteps =
            event.values[0].toLong()

        viewModel.onSensorSteps(
            currentSensorSteps
        )
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // Required by SensorEventListener
    }

    override fun onResume() {
        super.onResume()

        if (hasActivityPermission()) {
            startStepSensor()
        }
    }

    override fun onPause() {
        super.onPause()
        stopStepSensor()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == ACTIVITY_RECOGNITION_REQUEST
        ) {
            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                startStepSensor()
            } else {
                viewModel.setSensorUnavailable()
            }
        }
    }

    private fun hasActivityPermission(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ACTIVITY_RECOGNITION_REQUEST = 1001
    }
}