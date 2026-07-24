package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.MlaebiBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.BookingCheckoutScreen
import com.example.ui.screens.BookingHistoryScreen
import com.example.ui.screens.BookingSuccessScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.PitchDetailScreen
import com.example.ui.theme.MlaebiTheme
import com.example.ui.viewmodel.MlaebiViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MlaebiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            MlaebiTheme(darkTheme = isDarkMode) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    MlaebiAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MlaebiAppContent(viewModel: MlaebiViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val pitches by viewModel.filteredPitches.collectAsStateWithLifecycle()
    val selectedSport by viewModel.selectedSport.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val maxPrice by viewModel.maxPriceFilter.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()

    val selectedPitch by viewModel.selectedPitch.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDateString.collectAsStateWithLifecycle()
    val timeSlots by viewModel.timeSlots.collectAsStateWithLifecycle()
    val selectedSlot by viewModel.selectedTimeSlot.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhone by viewModel.userPhone.collectAsStateWithLifecycle()

    val upcomingBookings by viewModel.upcomingBookings.collectAsStateWithLifecycle()
    val pastBookings by viewModel.pastBookings.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val lastConfirmed by viewModel.lastConfirmedBooking.collectAsStateWithLifecycle()

    // Determine bottom bar visibility
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.BookingHistory.route,
        Screen.Notifications.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MlaebiBottomBar(
                    currentRoute = currentRoute,
                    unreadNotificationsCount = unreadCount,
                    onNavigate = { route ->
                        if (route == currentRoute) return@MlaebiBottomBar
                        if (route == Screen.Notifications.route) {
                            viewModel.markNotificationsAsRead()
                        }
                        if (route == Screen.Home.route) {
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        } else {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Screen 1: Home
            composable(Screen.Home.route) {
                HomeScreen(
                    pitches = pitches,
                    selectedSport = selectedSport,
                    searchQuery = searchQuery,
                    selectedCity = selectedCity,
                    cities = viewModel.cities,
                    maxPrice = maxPrice,
                    isDarkMode = isDarkMode,
                    unreadCount = unreadCount,
                    onSportSelected = { viewModel.onSportSelected(it) },
                    onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onCitySelected = { viewModel.onCitySelected(it) },
                    onMaxPriceChanged = { viewModel.onMaxPriceChanged(it) },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onPitchSelected = { pitch ->
                        viewModel.selectPitch(pitch)
                        navController.navigate(Screen.PitchDetail.route)
                    },
                    onNavigateNotifications = {
                        viewModel.markNotificationsAsRead()
                        navController.navigate(Screen.Notifications.route)
                    }
                )
            }

            // Screen 2: Pitch Details
            composable(Screen.PitchDetail.route) {
                PitchDetailScreen(
                    pitch = selectedPitch,
                    selectedDate = selectedDate,
                    availableDates = viewModel.getUpcomingDatesList(),
                    timeSlots = timeSlots,
                    selectedSlot = selectedSlot,
                    onDateSelected = { viewModel.selectDate(it) },
                    onSlotSelected = { viewModel.selectTimeSlot(it) },
                    onConfirmAndPayClicked = {
                        navController.navigate(Screen.Checkout.route)
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }

            // Screen 3: Booking Checkout & Payment
            composable(Screen.Checkout.route) {
                BookingCheckoutScreen(
                    pitch = selectedPitch,
                    selectedDate = selectedDate,
                    selectedSlot = selectedSlot,
                    paymentMethod = paymentMethod,
                    userName = userName,
                    userPhone = userPhone,
                    onPaymentMethodChanged = { viewModel.setPaymentMethod(it) },
                    onUserInfoUpdated = { name, phone -> viewModel.updateUserInfo(name, phone) },
                    onConfirmPaymentClicked = {
                        viewModel.confirmAndPayBooking {
                            navController.navigate(Screen.BookingSuccess.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    },
                    onBackClicked = { navController.popBackStack() }
                )
            }

            // Screen 4: Booking Success Confirmation
            composable(Screen.BookingSuccess.route) {
                BookingSuccessScreen(
                    booking = lastConfirmed,
                    onNavigateToHistory = {
                        navController.navigate(Screen.BookingHistory.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // Screen 5: Booking History
            composable(Screen.BookingHistory.route) {
                BookingHistoryScreen(
                    upcomingBookings = upcomingBookings,
                    pastBookings = pastBookings,
                    onToggleReminder = { id, status -> viewModel.toggleMatchReminder(id, status) },
                    onCancelBooking = { id -> viewModel.cancelBooking(id) },
                    onBookAgainClicked = { pitchId ->
                        val p = pitches.find { it.id == pitchId }
                        if (p != null) {
                            viewModel.selectPitch(p)
                            navController.navigate(Screen.PitchDetail.route)
                        } else {
                            navController.navigate(Screen.Home.route)
                        }
                    }
                )
            }

            // Screen 6: Smart Notifications
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    notifications = notifications,
                    onMarkAllRead = { viewModel.markNotificationsAsRead() }
                )
            }
        }
    }
}
