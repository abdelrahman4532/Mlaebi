package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SportType(val titleAr: String, val titleEn: String) {
    ALL("All", "All"),
    FOOTBALL("Football", "Football"),
    TENNIS("Tennis", "Tennis"),
    PADEL("Padel", "Padel")
}

data class Pitch(
    val id: String,
    val name: String,
    val sportType: SportType,
    val city: String,
    val district: String,
    val pricePerHour: Double,
    val rating: Double,
    val reviewCount: Int,
    val drawableResName: String, // e.g. "img_football_pitch_..."
    val surfaceType: String, // e.g. "عشب صناعي ذو جودة عالية"
    val sizeText: String, // e.g. "8 × 8 لاعبين"
    val amenities: List<String>,
    val description: String,
    val ownerName: String,
    val ownerPhone: String,
    val isFeatured: Boolean = false
)

data class TimeSlot(
    val id: String,
    val timeDisplay: String, // "05:00 م - 06:00 م"
    val period: String, // "صباحي", "مسائي"
    val price: Double,
    val isAvailable: Boolean = true,
    val isPeakHour: Boolean = false
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingRef: String, // e.g. "MLB-9482"
    val pitchId: String,
    val pitchName: String,
    val sportType: String, // "FOOTBALL", "TENNIS", "PADEL"
    val pitchDrawable: String,
    val cityAndDistrict: String,
    val bookingDate: String, // e.g. "السبت، 25 يوليو 2026"
    val timeSlotText: String, // "08:00 م - 09:00 م"
    val totalPrice: Double,
    val paymentMethod: String, // "MADA", "CREDIT_CARD", "APPLE_PAY", "CASH"
    val status: String, // "UPCOMING", "COMPLETED", "CANCELLED"
    val createdAt: Long = System.currentTimeMillis(),
    val reminderEnabled: Boolean = true,
    val qrCodePayload: String = ""
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "MATCH_REMINDER" // "MATCH_REMINDER", "BOOKING_CONFIRM", "PROMO"
)

data class VendorStats(
    val totalPitches: Int,
    val totalBookings: Int,
    val monthlyRevenue: Double,
    val occupancyPercentage: Int,
    val activeTodayCount: Int
)
