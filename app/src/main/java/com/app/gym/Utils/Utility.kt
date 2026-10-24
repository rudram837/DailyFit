package com.app.gym.Utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.annotation.RequiresPermission
import java.util.Calendar
import java.util.Locale

class Utility {

    companion object {

        fun showToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun getGreeting(): String {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            return when (hour) {
                in 5..11 -> "Good Morning ☀️"
                in 12..16 -> "Good Afternoon 🌤️"
                in 17..20 -> "Good Evening 🌇"
                else -> "Good Night 🌙"
            }
        }

        fun getTodayDate(): String {
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            val month = calendar.getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT,
                Locale.getDefault()
            )

            val day = calendar.get(Calendar.DAY_OF_MONTH)

            return "$dayOfWeek, $month $day"
        }

        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        private val motivationalQuotes = listOf(
            "Never give up. Great things take time.",
            "Push yourself, because no one else is going to do it for you.",
            "Sweat is just fat crying.",
            "The body achieves what the mind believes.",
            "Small progress is still progress.",
            "Discipline beats motivation every time.",
            "Your only limit is you.",
            "Wake up. Work out. Kick ass.",
            "Strive for progress, not perfection.",
            "The pain you feel today is the strength you feel tomorrow."
        )

        fun getDailyMotivation(): String {
            val dayOfYear = Calendar.getInstance()
                .get(Calendar.DAY_OF_YEAR)
            val quote = motivationalQuotes[dayOfYear % motivationalQuotes.size]
            return "“$quote”"
        }

        fun calculateDistanceKm(
            steps: Int,
            heightCm: Float
        ): Double {
            // Average stride length ≈ 41.5% of height
            val strideLengthMeters = heightCm * 0.415f / 100f

            return (steps * strideLengthMeters) / 1000.0
        }

        fun calculateCalories(
            steps: Int,
            weightKg: Float
        ): Int {
            if (steps <= 0 || weightKg <= 0f) return 0

            // Practical walking estimate
            val calories = steps * weightKg * 0.0005

            return calories.toInt()
        }

        fun calculateProgress(
            current: Int,
            goal: Int
        ): Int {
            if (goal <= 0) return 0

            return ((current.toFloat() / goal) * 100)
                .coerceIn(0f, 100f)
                .toInt()
        }

        fun getRemainingSteps(
            current: Int,
            goal: Int
        ): Int {
            return (goal - current).coerceAtLeast(0)
        }

        fun formatSteps(steps: Int): String {
            return String.format(Locale.getDefault(), "%,d", steps)
        }

        fun getTodayKey(): String {
            val calendar = Calendar.getInstance()

            return String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }
}