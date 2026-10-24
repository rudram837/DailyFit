package com.app.gym.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.app.gym.databinding.DialogInternetCheckBinding

class NoInternetDialog(
    context: Context,
    private val onRetry: () -> Unit
) : Dialog(context) {

    private lateinit var binding: DialogInternetCheckBinding

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogInternetCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)

        binding.btnRetry.setOnClickListener {
            onRetry()
        }
    }
}