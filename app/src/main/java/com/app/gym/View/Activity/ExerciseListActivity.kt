package com.app.gym.View.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.gym.Adapter.ExerciseAdapter
import com.app.gym.Adapter.ExerciseListAdapter
import com.app.gym.R
import com.app.gym.Repository.HomeRepository
import com.app.gym.ViewModel.HomeViewModel
import com.app.gym.ViewModel.HomeViewModelFactory
import com.app.gym.databinding.ActivityExerciseListBinding

class ExerciseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseListBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var exerciseAdapter: ExerciseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val repository = HomeRepository()
        val factory = HomeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupRecyclerView()
        observeExercises()
    }

    private fun setupRecyclerView() {

        exerciseAdapter = ExerciseListAdapter(arrayListOf()) { item ->
            // Item Click
        }
        binding.rvExercises.layoutManager =
            LinearLayoutManager(this)
        binding.rvExercises.adapter = exerciseAdapter
    }
    private fun observeExercises() {

        viewModel.getPopularExercises()

        viewModel.exercises.observe(this) { response ->
            exerciseAdapter.submitList(response.data)
        }
    }
}