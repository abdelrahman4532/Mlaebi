package com.example.data.repository

import com.example.data.local.BookingDao
import com.example.data.local.NotificationDao
import com.example.data.model.BookingEntity
import com.example.data.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class BookingRepository(
    private val bookingDao: BookingDao,
    private val notificationDao: NotificationDao
) {
    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    val upcomingBookings: Flow<List<BookingEntity>> = bookingDao.getBookingsByStatus("UPCOMING")
    val pastBookings: Flow<List<BookingEntity>> = bookingDao.getBookingsByStatus("COMPLETED")
    val notifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    suspend fun seedInitialDataIfEmpty() {
        val existing = allBookings.first()
        if (existing.isEmpty()) {
            val sampleUpcoming = BookingEntity(
                bookingRef = "MLB-8921",
                pitchId = "p1",
                pitchName = "Champions International Stadium - Pitch A",
                sportType = "FOOTBALL",
                pitchDrawable = "img_football_pitch_1784856031671",
                cityAndDistrict = "Riyadh - Al Malqa",
                bookingDate = "Tomorrow - Jul 26, 2026",
                timeSlotText = "08:00 PM - 09:00 PM",
                totalPrice = 253.0, // including 15% VAT
                paymentMethod = "APPLE_PAY",
                status = "UPCOMING",
                reminderEnabled = true,
                qrCodePayload = "MLA-ENTRY-8921-P1"
            )

            val sampleCompleted = BookingEntity(
                bookingRef = "MLB-7312",
                pitchId = "p2",
                pitchName = "Padel Zone Arena",
                sportType = "PADEL",
                pitchDrawable = "img_padel_court_1784856052537",
                cityAndDistrict = "Riyadh - Al Narjis",
                bookingDate = "Last Week - Jul 18, 2026",
                timeSlotText = "06:00 PM - 07:00 PM",
                totalPrice = 207.0,
                paymentMethod = "MADA",
                status = "COMPLETED",
                reminderEnabled = false,
                qrCodePayload = "MLA-ENTRY-7312-P2"
            )

            val sampleCancelled = BookingEntity(
                bookingRef = "MLB-6104",
                pitchId = "p3",
                pitchName = "Kings Tennis Courts",
                sportType = "TENNIS",
                pitchDrawable = "img_tennis_court_1784856042391",
                cityAndDistrict = "Jeddah - Al Shati",
                bookingDate = "Jul 10, 2026",
                timeSlotText = "05:00 PM - 06:00 PM",
                totalPrice = 172.5,
                paymentMethod = "CREDIT_CARD",
                status = "CANCELLED",
                reminderEnabled = false,
                qrCodePayload = "MLA-ENTRY-6104-P3"
            )

            bookingDao.insertBooking(sampleUpcoming)
            bookingDao.insertBooking(sampleCompleted)
            bookingDao.insertBooking(sampleCancelled)

            notificationDao.insertNotification(
                NotificationEntity(
                    title = "Booking Confirmation (#MLB-8921)",
                    body = "Your booking for Champions International Stadium is confirmed for tomorrow at 08:00 PM. Have a great match!",
                    type = "BOOKING_CONFIRM"
                )
            )

            notificationDao.insertNotification(
                NotificationEntity(
                    title = "Smart Match Reminder",
                    body = "Your upcoming match at Padel Zone starts in 2 hours. Don't forget your racket!",
                    type = "MATCH_REMINDER"
                )
            )
        }
    }

    suspend fun createBooking(
        pitchId: String,
        pitchName: String,
        sportType: String,
        pitchDrawable: String,
        cityAndDistrict: String,
        bookingDate: String,
        timeSlotText: String,
        totalPrice: Double,
        paymentMethod: String
    ): BookingEntity {
        val randomNum = (1000..9999).random()
        val ref = "MLB-$randomNum"

        val newBooking = BookingEntity(
            bookingRef = ref,
            pitchId = pitchId,
            pitchName = pitchName,
            sportType = sportType,
            pitchDrawable = pitchDrawable,
            cityAndDistrict = cityAndDistrict,
            bookingDate = bookingDate,
            timeSlotText = timeSlotText,
            totalPrice = totalPrice,
            paymentMethod = paymentMethod,
            status = "UPCOMING",
            reminderEnabled = true,
            qrCodePayload = "MLA-ENTRY-$randomNum-$pitchId"
        )

        val id = bookingDao.insertBooking(newBooking)
        val created = newBooking.copy(id = id)

        // Create notification
        val notif = NotificationEntity(
            title = "Booking Confirmed ($ref)",
            body = "$pitchName has been booked for $bookingDate at $timeSlotText. Smart reminder activated.",
            type = "BOOKING_CONFIRM"
        )
        notificationDao.insertNotification(notif)

        return created
    }

    suspend fun cancelBooking(bookingId: Long) {
        bookingDao.cancelBooking(bookingId)
        val notif = NotificationEntity(
            title = "Booking Cancelled",
            body = "Your booking was successfully cancelled and refunded to your wallet.",
            type = "BOOKING_CONFIRM"
        )
        notificationDao.insertNotification(notif)
    }

    suspend fun toggleReminder(bookingId: Long, currentStatus: Boolean) {
        bookingDao.updateReminderStatus(bookingId, !currentStatus)
    }

    suspend fun markNotificationsRead() {
        notificationDao.markAllAsRead()
    }
}

