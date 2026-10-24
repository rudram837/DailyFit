package com.app.gym.View.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.gym.Adapter.CategoryAdapter
import com.app.gym.Adapter.ExerciseAdapter
import com.app.gym.Dialog.NoInternetDialog
import com.app.gym.R
import com.app.gym.Repository.HomeRepository
import com.app.gym.Utils.Utility
import com.app.gym.Utils.Utility.Companion.showToast
import com.app.gym.View.Activity.BMI.BMICalculatorActivity
import com.app.gym.View.Activity.ExerciseListActivity
import com.app.gym.ViewModel.HomeViewModel
import com.app.gym.ViewModel.HomeViewModelFactory
import com.app.gym.ViewModel.StepTracker.StepTrackerViewModel
import com.app.gym.ViewModel.StepTracker.StepTrackerViewModelFactory
import com.app.gym.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: CategoryAdapter
    private lateinit var exerciseAdapter: ExerciseAdapter

    private var noInternetDialog: NoInternetDialog? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastDate = ""

    //    shmmer
    private var categoryLoaded = false
    private var exerciseLoaded = false

    private val stepTrackerViewModel: StepTrackerViewModel by activityViewModels {
        StepTrackerViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setUpClicks()

        val repository = HomeRepository()
        val factory = HomeViewModelFactory(repository)

        val today = Utility.getTodayDate()
        binding.tvDate.text = today
        lastDate = today
        binding.tvMotivation.text = Utility.getDailyMotivation()

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        observeStepTracker()
        stepTrackerViewModel.loadSavedData()

        binding.swipeRefreshLayout.setOnRefreshListener {

            if (Utility.isInternetAvailable(requireContext())) {
                fetchData()
            } else {
                binding.swipeRefreshLayout.isRefreshing = false
                showNoInternetDialog()
            }
        }
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.gym_primary
        )

        observeData()
        checkInternetAndLoadData()
    }

    override fun onResume() {
        super.onResume()
        stepTrackerViewModel.loadSavedData()
        greetingRunnable.run()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(greetingRunnable)
    }

    private val greetingRunnable = object : Runnable {
        override fun run() {
            _binding?.let {
                it.tvGreeting.text = Utility.getGreeting()
                val currentDate = Utility.getTodayDate()
                if (currentDate != lastDate) {
                    lastDate = currentDate
                    it.tvDate.text = currentDate
                    it.tvMotivation.text = Utility.getDailyMotivation()
                }
            }

            handler.postDelayed(this, 60_000) // Every 1 minute
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun checkInternetAndLoadData() {
        if (Utility.isInternetAvailable(requireContext())) {
            fetchData()
        } else {
            showNoInternetDialog()
        }
    }

    private fun fetchData() {
        categoryLoaded = false
        exerciseLoaded = false

        showShimmer()

        viewModel.getCategories()
        viewModel.getPopularExercises()
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun showNoInternetDialog() {
        if (noInternetDialog?.isShowing == true) return

        noInternetDialog = NoInternetDialog(requireContext()) {
            // Retry button click
            if (Utility.isInternetAvailable(requireContext())) {
                noInternetDialog?.dismiss()
                fetchData()
            } else {
                showToast(requireContext(), "Still no internet connection")
            }
        }
        noInternetDialog?.show()
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(arrayListOf()) { category ->
            // Click Handle -> category.name
        }
        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = adapter

        exerciseAdapter = ExerciseAdapter(arrayListOf()) {
            // exercise click
        }
        binding.rvPopularExercises.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPopularExercises.adapter = exerciseAdapter
    }

    private fun setUpClicks() {
        binding.bmiLayout.setOnClickListener {
            val intent = Intent(requireContext(), BMICalculatorActivity::class.java)
            startActivity(intent)
        }

        binding.tvExercisesHeaderSeeAll.setOnClickListener {
            val intentSee = Intent(requireContext(), ExerciseListActivity::class.java)
            startActivity(intentSee)
        }
    }

    private fun observeData() {
        viewModel.categories.observe(viewLifecycleOwner) {

            adapter.submitList(it.data)

            categoryLoaded = true

            if (categoryLoaded && exerciseLoaded) {
                hideShimmer()
            }

            binding.swipeRefreshLayout.isRefreshing = false
        }

        viewModel.exercises.observe(viewLifecycleOwner) {

            val preview =
                if (it.data.size > 4) it.data.take(4)
                else it.data

            exerciseAdapter.submitList(preview)

            exerciseLoaded = true

            if (categoryLoaded && exerciseLoaded) {
                hideShimmer()
            }

            binding.swipeRefreshLayout.isRefreshing = false
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            hideShimmer()
            binding.swipeRefreshLayout.isRefreshing = false

            if (!errorMsg.isNullOrEmpty()) {
                showToast(requireContext(), errorMsg)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeStepTracker() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                stepTrackerViewModel.uiState.collect { state ->

                    binding.tvHomeSteps.text = Utility.formatSteps(state.steps)
                    binding.tvHomeCalories.text = "${state.calories} kcal"

                    binding.progressHomeCalories.max = 100
//                    binding.progressHomeCalories.progress =
//                        state.calorieProgress

                    binding.progressHomeSteps.max = 100
                    binding.progressHomeSteps.progress =
                        state.progress
                }
            }
        }
    }

    private fun showShimmer() {

        binding.shimmerCategories.visibility = View.VISIBLE
        binding.shimmerExercises.visibility = View.VISIBLE

        binding.shimmerCategories.startShimmer()
        binding.shimmerExercises.startShimmer()

        binding.rvCategories.visibility = View.GONE
        binding.rvPopularExercises.visibility = View.GONE
    }
    private fun hideShimmer() {

        binding.shimmerCategories.stopShimmer()
        binding.shimmerExercises.stopShimmer()

        binding.shimmerCategories.visibility = View.GONE
        binding.shimmerExercises.visibility = View.GONE

        binding.rvCategories.visibility = View.VISIBLE
        binding.rvPopularExercises.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        noInternetDialog?.dismiss()
        handler.removeCallbacks(greetingRunnable)
        noInternetDialog = null
        _binding = null
    }
}