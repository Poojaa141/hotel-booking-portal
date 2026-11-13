import { useState } from 'react'
import { formatDate } from '../utils/format.js'

function formatPrice(amount) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount)
}

function BookingDetails({ booking, onCancel, cancelLoading, cancelMessage, cancelError }) {
  const [confirming, setConfirming] = useState(false)

  function keepBooking() {
    setConfirming(false)
  }

  return (
    <article className="manage-details-card">
      <div className="manage-details-heading">
        <div>
          <p className="eyebrow">Your reservation</p>
          <h2>{booking.hotelName}</h2>
          <p className="manage-location">{booking.location} · {booking.roomType}</p>
        </div>
        <span className={`status-chip ${booking.status === 'CONFIRMED' ? 'confirmed' : 'cancelled'}`}>
          {booking.status}
        </span>
      </div>

      <dl className="manage-booking-data">
        <div><dt>Booking reference</dt><dd>{booking.bookingRef}</dd></div>
        <div><dt>Guest name</dt><dd>{booking.guestName}</dd></div>
        <div><dt>Email</dt><dd>{booking.guestEmail}</dd></div>
        <div><dt>Check-in</dt><dd>{formatDate(booking.checkIn)}</dd></div>
        <div><dt>Check-out</dt><dd>{formatDate(booking.checkOut)}</dd></div>
        <div><dt>Nights</dt><dd>{booking.nights}</dd></div>
        <div><dt>Total amount</dt><dd className="manage-total">{formatPrice(booking.totalAmount)}</dd></div>
        {booking.createdAt && <div><dt>Booked on</dt><dd>{formatDate(booking.createdAt)}</dd></div>}
      </dl>

      {cancelMessage && <div className="manage-success" role="status">{cancelMessage}</div>}
      {cancelError && <div className="manage-error" role="alert">{cancelError}</div>}

      {booking.status === 'CANCELLED' ? (
        <p className="cancelled-note">This booking has been cancelled. The room has been released.</p>
      ) : confirming ? (
        <div className="cancel-confirmation">
          <p>Are you sure you want to cancel this booking?</p>
          <div>
            <button className="danger-button" type="button" onClick={onCancel} disabled={cancelLoading}>
              {cancelLoading ? 'Cancelling...' : 'Yes, cancel booking'}
            </button>
            <button className="secondary-button" type="button" onClick={keepBooking} disabled={cancelLoading}>
              Keep booking
            </button>
          </div>
        </div>
      ) : (
        <button className="danger-outline-button" type="button" onClick={() => setConfirming(true)}>
          Cancel booking
        </button>
      )}
    </article>
  )
}

export default BookingDetails
