import { useState } from 'react'
import { Link } from 'react-router-dom'

function formatPrice(amount) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount)
}

function BookingConfirmation({ booking }) {
  const [copied, setCopied] = useState(false)

  async function copyReference() {
    try {
      await navigator.clipboard.writeText(booking.bookingRef)
      setCopied(true)
    } catch {
      setCopied(false)
    }
  }

  return (
    <section className="confirmation-card" aria-live="polite">
      <div className="confirmation-icon" aria-hidden="true">
        <svg viewBox="0 0 24 24"><path d="m5 12 4 4L19 6" /></svg>
      </div>
      <p className="confirmation-eyebrow">Your stay is reserved</p>
      <h2>Booking confirmed</h2>
      <div className="reference-block">
        <span>Booking reference</span>
        <strong>{booking.bookingRef}</strong>
        <button type="button" className="copy-button" onClick={copyReference}>
          {copied ? 'Copied' : 'Copy reference'}
        </button>
      </div>
      <dl className="confirmation-details">
        <div><dt>Hotel</dt><dd>{booking.hotelName}</dd></div>
        <div><dt>Dates</dt><dd>{booking.checkIn} to {booking.checkOut}</dd></div>
        <div><dt>Nights</dt><dd>{booking.nights}</dd></div>
        <div><dt>Total amount</dt><dd>{formatPrice(booking.totalAmount)}</dd></div>
        <div><dt>Guest</dt><dd>{booking.guestName}</dd></div>
        <div><dt>Email</dt><dd>{booking.guestEmail}</dd></div>
        <div><dt>Status</dt><dd>{booking.status}</dd></div>
      </dl>
      <p className="reference-note">Keep your reference and email. You will need both to view or cancel your booking.</p>
      <div className="confirmation-actions">
        <Link className="primary-button" to="/">Back to home</Link>
        <Link className="secondary-button" to="/manage">Manage a booking</Link>
      </div>
    </section>
  )
}

export default BookingConfirmation
