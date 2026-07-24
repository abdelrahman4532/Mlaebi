package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PitchDetail : Screen("pitch_detail")
    object Checkout : Screen("checkout")
    object BookingSuccess : Screen("booking_success")
    object BookingHistory : Screen("booking_history")
    object Notifications : Screen("notifications")
}
