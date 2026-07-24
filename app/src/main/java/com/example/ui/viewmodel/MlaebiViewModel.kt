package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.Pitch
import com.example.data.model.SportType
import com.example.data.model.TimeSlot
import com.example.data.model.VendorStats
import com.example.data.repository.BookingRepository
import com.example.data.repository.PitchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MlaebiViewModel(application: Application) : AndroidViewModel(application) {

    private val pitchRepo = PitchRepository()
    private val bookingRepo: BookingRepository

    init {
        val db = AppDatabase.getDatabase(application)
        bookingRepo = BookingRepository(db.bookingDao(), db.notificationDao())
        viewModelScope.launch {
            bookingRepo.seedInitialDataIfEmpty()
        }
    }

    // App Preferences / Mode
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isVendorMode = MutableStateFlow(false) // Toggle between Bookings user & Pitch Owner/Vendor
    val isVendorMode: StateFlow<Boolean> = _isVendorMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setVendorMode(enabled: Boolean) {
        _isVendorMode.value = enabled
    }

    // Filters
    private val _selectedSport = MutableStateFlow(SportType.ALL)
    val selectedSport: StateFlow<SportType> = _selectedSport.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCity = MutableStateFlow("All")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow(300.0)
    val maxPriceFilter: StateFlow<Double> = _maxPriceFilter.asStateFlow()

    val cities = listOf("All", "Riyadh", "Jeddah", "Khobar", "Dammam")

    // Filtered Pitches State
    val filteredPitches: StateFlow<List<Pitch>> = combine(
        _selectedSport,
        _searchQuery,
        _selectedCity,
        _maxPriceFilter
    ) { sport, query, city, maxPrice ->
        pitchRepo.filterPitches(sport, query, city, maxPrice)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSportSelected(sport: SportType) {
        _selectedSport.value = sport
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCitySelected(city: String) {
        _selectedCity.value = city
    }

    fun onMaxPriceChanged(price: Double) {
        _maxPriceFilter.value = price
    }

    // Selected Pitch for Details & Booking
    private val _selectedPitch = MutableStateFlow<Pitch?>(null)
    val selectedPitch: StateFlow<Pitch?> = _selectedPitch.asStateFlow()

    // Booking Date selection
    private val _selectedDateString = MutableStateFlow(getFormattedTodayDate())
    val selectedDateString: StateFlow<String> = _selectedDateString.asStateFlow()

    // Available Time Slots for selected pitch & date
    private val _timeSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    val timeSlots: StateFlow<List<TimeSlot>> = _timeSlots.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow<TimeSlot?>(null)
    val selectedTimeSlot: StateFlow<TimeSlot?> = _selectedTimeSlot.asStateFlow()

    // Payment Selection
    private val _selectedPaymentMethod = MutableStateFlow("APPLE_PAY") // "MADA", "CREDIT_CARD", "APPLE_PAY", "CASH"
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    private val _userName = MutableStateFlow("Abdulrahman Al-Otaibi")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("+966 55 123 4567")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _lastConfirmedBooking = MutableStateFlow<BookingEntity?>(null)
    val lastConfirmedBooking: StateFlow<BookingEntity?> = _lastConfirmedBooking.asStateFlow()

    fun selectPitch(pitch: Pitch) {
        _selectedPitch.value = pitch
        _selectedTimeSlot.value = null
        loadSlotsForPitchAndDate(pitch.id, _selectedDateString.value)
    }

    fun selectDate(dateString: String) {
        _selectedDateString.value = dateString
        _selectedTimeSlot.value = null
        _selectedPitch.value?.let { pitch ->
            loadSlotsForPitchAndDate(pitch.id, dateString)
        }
    }

    private fun loadSlotsForPitchAndDate(pitchId: String, dateString: String) {
        val slots = pitchRepo.getAvailableTimeSlotsForDate(pitchId, dateString)
        _timeSlots.value = slots
    }

    fun selectTimeSlot(slot: TimeSlot) {
        if (slot.isAvailable) {
            _selectedTimeSlot.value = slot
        }
    }

    fun setPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun updateUserInfo(name: String, phone: String) {
        _userName.value = name
        _userPhone.value = phone
    }

    // Confirm Booking Action
    fun confirmAndPayBooking(onSuccess: () -> Unit) {
        val pitch = _selectedPitch.value ?: return
        val slot = _selectedTimeSlot.value ?: return

        val basePrice = slot.price
        val vat = basePrice * 0.15
        val totalPrice = basePrice + vat

        viewModelScope.launch {
            val confirmed = bookingRepo.createBooking(
                pitchId = pitch.id,
                pitchName = pitch.name,
                sportType = pitch.sportType.name,
                pitchDrawable = pitch.drawableResName,
                cityAndDistrict = "${pitch.city} - ${pitch.district}",
                bookingDate = _selectedDateString.value,
                timeSlotText = slot.timeDisplay,
                totalPrice = totalPrice,
                paymentMethod = _selectedPaymentMethod.value
            )
            _lastConfirmedBooking.value = confirmed
            onSuccess()
        }
    }

    // Bookings History Flows
    val allBookings: StateFlow<List<BookingEntity>> = bookingRepo.allBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val upcomingBookings: StateFlow<List<BookingEntity>> = bookingRepo.upcomingBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pastBookings: StateFlow<List<BookingEntity>> = bookingRepo.pastBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch {
            bookingRepo.cancelBooking(bookingId)
        }
    }

    fun toggleMatchReminder(bookingId: Long, currentEnabled: Boolean) {
        viewModelScope.launch {
            bookingRepo.toggleReminder(bookingId, currentEnabled)
        }
    }

    // Smart Notifications
    val notifications: StateFlow<List<NotificationEntity>> = bookingRepo.notifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val unreadCount: StateFlow<Int> = bookingRepo.unreadNotificationsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            bookingRepo.markNotificationsRead()
        }
    }

    // Court Owner / Vendor Dashboard Stats
    fun getVendorStats(): VendorStats = pitchRepo.getVendorStats()

    fun addNewPitchByOwner(
        name: String,
        sportType: SportType,
        city: String,
        district: String,
        price: Double,
        surface: String,
        description: String
    ) {
        val newPitch = Pitch(
            id = "p_${System.currentTimeMillis()}",
            name = name,
            sportType = sportType,
            city = city,
            district = district,
            pricePerHour = price,
            rating = 5.0,
            reviewCount = 1,
            drawableResName = when(sportType) {
                SportType.FOOTBALL -> "img_football_pitch_1784856031671"
                SportType.PADEL -> "img_padel_court_1784856052537"
                SportType.TENNIS -> "img_tennis_court_1784856042391"
                else -> "img_football_pitch_1784856031671"
            },
            surfaceType = surface,
            sizeText = "Standard",
            amenities = listOf("LED Floodlights", "Parking", "Locker Rooms"),
            description = description,
            ownerName = _userName.value,
            ownerPhone = _userPhone.value,
            isFeatured = true
        )
        pitchRepo.addCustomPitch(newPitch)
    }

    // Date Utilities
    fun getUpcomingDatesList(): List<String> {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("EEE, MMM d, yyyy", Locale.US)

        list.add("Today - " + SimpleDateFormat("MMM d", Locale.US).format(cal.time))
        cal.add(Calendar.DAY_OF_YEAR, 1)
        list.add("Tomorrow - " + SimpleDateFormat("MMM d", Locale.US).format(cal.time))

        for (i in 2..6) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            list.add(sdf.format(cal.time))
        }
        return list
    }

    private fun getFormattedTodayDate(): String {
        val cal = Calendar.getInstance()
        return "Today - " + SimpleDateFormat("MMM d", Locale.US).format(cal.time)
    }
}

