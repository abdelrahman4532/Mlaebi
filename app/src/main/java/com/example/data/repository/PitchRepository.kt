package com.example.data.repository

import com.example.data.model.Pitch
import com.example.data.model.SportType
import com.example.data.model.TimeSlot
import com.example.data.model.VendorStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PitchRepository {

    private val samplePitches = listOf(
        Pitch(
            id = "p1",
            name = "Champions International Stadium - Pitch A",
            sportType = SportType.FOOTBALL,
            city = "Riyadh",
            district = "Al Malqa",
            pricePerHour = 220.0,
            rating = 4.9,
            reviewCount = 142,
            drawableResName = "img_football_pitch_1784856031671",
            surfaceType = "FIFA Approved Premium Turf",
            sizeText = "8 v 8 Players",
            amenities = listOf("LED Floodlights", "Private Parking", "Locker Rooms & Showers", "Misting Cooling", "Sports Cafe", "Free Balls"),
            description = "Professional football pitch equipped with high-quality night lighting and impact-absorbing turf. Referee available on request with parking for up to 50 vehicles.",
            ownerName = "Star Sports Academy",
            ownerPhone = "+966 50 123 4567",
            isFeatured = true
        ),
        Pitch(
            id = "p2",
            name = "Padel Zone Arena",
            sportType = SportType.PADEL,
            city = "Riyadh",
            district = "Al Narjis",
            pricePerHour = 180.0,
            rating = 4.8,
            reviewCount = 98,
            drawableResName = "img_padel_court_1784856052537",
            surfaceType = "Panoramic Glass & Thick Blue Turf",
            sizeText = "Doubles (4 Players)",
            amenities = listOf("Air Conditioned", "Racket & Ball Rental", "Lounge Area", "Scoreboard Display", "Energy Drinks & Water"),
            description = "Fully enclosed climate-controlled indoor padel court built to international standards with panoramic glass walls and glare-free lighting.",
            ownerName = "Capt. Fahad Al-Majed",
            ownerPhone = "+966 55 987 6543",
            isFeatured = true
        ),
        Pitch(
            id = "p3",
            name = "Kings Tennis Courts",
            sportType = SportType.TENNIS,
            city = "Jeddah",
            district = "Al Shati",
            pricePerHour = 150.0,
            rating = 4.7,
            reviewCount = 76,
            drawableResName = "img_tennis_court_1784856042391",
            surfaceType = "Hard Court",
            sizeText = "Singles & Doubles",
            amenities = listOf("Night Lighting", "Personal Coach on Request", "Full Tennis Gear", "Available Parking", "Fresh Juice Bar"),
            description = "Outdoor tennis court with scenic views and excellent evening lighting. Suitable for singles and doubles matches with option to hire a professional coach.",
            ownerName = "Jeddah Golf & Tennis Club",
            ownerPhone = "+966 54 321 0987",
            isFeatured = false
        ),
        Pitch(
            id = "p4",
            name = "Challenge 5v5 Arena",
            sportType = SportType.FOOTBALL,
            city = "Khobar",
            district = "Golden Belt",
            pricePerHour = 160.0,
            rating = 4.6,
            reviewCount = 112,
            drawableResName = "img_football_pitch_1784856031671",
            surfaceType = "Modern Artificial Turf",
            sizeText = "5 v 5 Players",
            amenities = listOf("Floodlights", "Changing Rooms", "Free Cold Water", "Car Parking"),
            description = "Fast-paced 5v5 pitch ready for friendly matches and mini tournaments between friends. Features high safety netting and premium turf.",
            ownerName = "Eastern Pitches Co.",
            ownerPhone = "+966 53 111 2233",
            isFeatured = false
        ),
        Pitch(
            id = "p5",
            name = "Oxygen Padel Academy",
            sportType = SportType.PADEL,
            city = "Jeddah",
            district = "Al Rawdah",
            pricePerHour = 200.0,
            rating = 4.9,
            reviewCount = 165,
            drawableResName = "img_padel_court_1784856052537",
            surfaceType = "Spanish Blue Padel Turf",
            sizeText = "Doubles (4 Players)",
            amenities = listOf("VIP Lounge", "Pro Shop", "Climate Control", "Specialty Coffee Bar", "Electronic Scoring"),
            description = "Luxury padel complex in Jeddah featuring hospitality services and premium lounge areas. World-class court quality certified by the Padel Federation.",
            ownerName = "Oxygen Sports Group",
            ownerPhone = "+966 56 444 5566",
            isFeatured = true
        ),
        Pitch(
            id = "p6",
            name = "Royal Yarmouk Tennis Club",
            sportType = SportType.TENNIS,
            city = "Riyadh",
            district = "Al Yasmin",
            pricePerHour = 175.0,
            rating = 4.8,
            reviewCount = 54,
            drawableResName = "img_tennis_court_1784856042391",
            surfaceType = "Clay Court",
            sizeText = "Singles & Doubles",
            amenities = listOf("Red Clay Surface", "Balanced LED Lights", "Racket Cleaning Station", "Spacious Parking"),
            description = "Experience Roland Garros style clay tennis. Carefully maintained clay court surface that reduces joint impact and fatigue.",
            ownerName = "Yarmouk Academy",
            ownerPhone = "+966 50 888 9900",
            isFeatured = false
        )
    )

    private val _pitchesState = MutableStateFlow(samplePitches)
    val pitches: Flow<List<Pitch>> = _pitchesState.asStateFlow()

    fun getPitchById(id: String): Pitch? {
        return samplePitches.find { it.id == id }
    }

    fun filterPitches(
        sportType: SportType = SportType.ALL,
        searchQuery: String = "",
        selectedCity: String = "All",
        maxPrice: Double = 300.0
    ): List<Pitch> {
        return samplePitches.filter { pitch ->
            val matchSport = (sportType == SportType.ALL || pitch.sportType == sportType)
            val matchCity = (selectedCity == "All" || pitch.city.equals(selectedCity, ignoreCase = true))
            val matchPrice = pitch.pricePerHour <= maxPrice
            val matchQuery = searchQuery.isBlank() ||
                    pitch.name.contains(searchQuery, ignoreCase = true) ||
                    pitch.district.contains(searchQuery, ignoreCase = true) ||
                    pitch.city.contains(searchQuery, ignoreCase = true)

            matchSport && matchCity && matchPrice && matchQuery
        }
    }

    fun getAvailableTimeSlotsForDate(pitchId: String, dateString: String): List<TimeSlot> {
        val pitch = getPitchById(pitchId) ?: return emptyList()
        val basePrice = pitch.pricePerHour

        return listOf(
            TimeSlot("t1", "04:00 AM - 05:00 AM", "Morning", basePrice * 0.8, isAvailable = true),
            TimeSlot("t2", "06:00 AM - 07:00 AM", "Morning", basePrice * 0.8, isAvailable = false),
            TimeSlot("t3", "04:00 PM - 05:00 PM", "Evening", basePrice, isAvailable = true),
            TimeSlot("t4", "05:00 PM - 06:00 PM", "Evening", basePrice, isAvailable = true),
            TimeSlot("t5", "06:00 PM - 07:00 PM", "Evening", basePrice * 1.1, isAvailable = true, isPeakHour = true),
            TimeSlot("t6", "07:00 PM - 08:00 PM", "Evening", basePrice * 1.2, isAvailable = false, isPeakHour = true),
            TimeSlot("t7", "08:00 PM - 09:00 PM", "Evening", basePrice * 1.2, isAvailable = true, isPeakHour = true),
            TimeSlot("t8", "09:00 PM - 10:00 PM", "Evening", basePrice * 1.2, isAvailable = true, isPeakHour = true),
            TimeSlot("t9", "10:00 PM - 11:00 PM", "Evening", basePrice * 1.1, isAvailable = true, isPeakHour = true),
            TimeSlot("t10", "11:00 PM - 12:00 AM", "Evening", basePrice, isAvailable = true)
        )
    }

    fun getVendorStats(): VendorStats {
        return VendorStats(
            totalPitches = 3,
            totalBookings = 48,
            monthlyRevenue = 9840.0,
            occupancyPercentage = 84,
            activeTodayCount = 6
        )
    }

    fun addCustomPitch(pitch: Pitch) {
        val updated = _pitchesState.value + pitch
        _pitchesState.value = updated
    }
}

