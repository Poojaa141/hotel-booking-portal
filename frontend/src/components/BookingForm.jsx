import { useEffect, useMemo, useState } from 'react'
import { createBooking, getQuote } from '../api/api.js'
import BookingConfirmation from './BookingConfirmation.jsx'

function getToday() {
  const now = new Date()
  const localDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
  return localDate.toISOString().slice(0, 10)
}

function addOneDay(date) {
  if (!date) return getToday()
  const [year, month, day] = date.split('-').map(Number)
  const nextDate = new Date(year, month - 1, day + 1)
  return `${nextDate.getFullYear()}-${String(nextDate.getMonth() + 1).padStart(2, '0')}-${String(nextDate.getDate()).padStart(2, '0')}`
}

function formatPrice(amount) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount)
}

function getApiMessage(error) {
  if (error.response) {
    return error.response.data?.message || 'Something went wrong. Please try again'
  }
  return 'Cannot reach the server. Please try again'
}

function BookingForm({ selectedRoom, initialCheckIn, initialCheckOut, onDatesChange, onRefreshHotel }) {
  const [checkIn, setCheckIn] = useState(initialCheckIn)
  const [checkOut, setCheckOut] = useState(initialCheckOut)
  const [guestName, setGuestName] = useState('')
  const [guestEmail, setGuestEmail] = useState('')
  const [quote, setQuote] = useState(null)
  const [quoteError, setQuoteError] = useState('')
  const [quoteLoading, setQuoteLoading] = useState(false)
  const [bookingError, setBookingError] = useState('')
  const [bookingLoading, setBookingLoading] = useState(false)
  const [submitted, setSubmitted] = useState(false)
  const [booking, setBooking] = useState(null)

  useEffect(() => {
    setCheckIn(initialCheckIn)
    setCheckOut(initialCheckOut)
  }, [initialCheckIn, initialCheckOut])

  const dateMessage = useMemo(() => {
    if (checkIn && checkIn < getToday()) return 'Check-in date cannot be in the past'
    if (checkIn && checkOut && checkOut <= checkIn) return 'Check-out date must be after check-in date'
    return ''
  }, [checkIn, checkOut])
  const datesValid = Boolean(checkIn && checkOut && !dateMessage)
  const trimmedName = guestName.trim()
  const trimmedEmail = guestEmail.trim()
  const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)
  const formValid = trimmedName.length >= 2 && emailValid && datesValid
  const currentQuote = quote?.roomId === selectedRoom?.roomId
    && quote?.checkIn === checkIn
    && quote?.checkOut === checkOut
    ? quote
    : null

  useEffect(() => {
    if (!datesValid || !selectedRoom) {
      setQuote(null)
      setQuoteError('')
      setQuoteLoading(false)
      return undefined
    }

    let current = true
    setQuote(null)
    setQuoteError('')
    setQuoteLoading(true)

    getQuote({ roomId: selectedRoom.roomId, checkIn, checkOut })
      .then((result) => {
        if (current) setQuote(result)
      })
      .catch((error) => {
        if (current) setQuoteError(getApiMessage(error))
      })
      .finally(() => {
        if (current) setQuoteLoading(false)
      })

    return () => {
      current = false
    }
  }, [checkIn, checkOut, datesValid, selectedRoom?.roomId])

  function updateDates(nextCheckIn, nextCheckOut) {
    setCheckIn(nextCheckIn)
    setCheckOut(nextCheckOut)
    setBookingError('')
    onDatesChange(nextCheckIn, nextCheckOut)
  }

  function handleCheckInChange(event) {
    const nextCheckIn = event.target.value
    const nextCheckOut = checkOut && nextCheckIn && checkOut <= nextCheckIn ? '' : checkOut
    updateDates(nextCheckIn, nextCheckOut)
  }

  function handleCheckOutChange(event) {
    updateDates(checkIn, event.target.value)
  }

  function getFieldErrors() {
    return {
      guestName: !trimmedName ? 'Please enter your name' : trimmedName.length < 2 ? 'Name must be at least 2 characters' : '',
      guestEmail: !trimmedEmail ? 'Please enter your email address' : !emailValid ? 'Please enter a valid email address' : '',
      dates: !checkIn || !checkOut ? 'Please select both check-in and check-out dates' : dateMessage,
    }
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitted(true)
    setBookingError('')

    if (!formValid || !selectedRoom || !selectedRoom.available || !currentQuote?.available || bookingLoading) return

    setBookingLoading(true)
    try {
      const result = await createBooking({
        roomId: selectedRoom.roomId,
        guestName: trimmedName,
        guestEmail: trimmedEmail,
        checkIn,
        checkOut,
      })
      setBooking(result)
    } catch (error) {
      setBookingError(getApiMessage(error))
      if (error.response?.status === 409) onRefreshHotel()
    } finally {
      setBookingLoading(false)
    }
  }

  if (booking) return <BookingConfirmation booking={booking} />

  const fieldErrors = getFieldErrors()
  const soldOut = selectedRoom?.available === false || currentQuote?.available === false
  const canSubmit = formValid && Boolean(selectedRoom?.available) && Boolean(currentQuote?.available) && !soldOut && !quoteLoading && !bookingLoading

  return (
    <form className="booking-card" onSubmit={handleSubmit} noValidate>
      <div className="booking-card-heading">
        <p className="eyebrow">Make it yours</p>
        <h2>Reserve your stay</h2>
        {selectedRoom && <p className="selected-room-name">{selectedRoom.type}</p>}
      </div>

      <div className="booking-fields">
        <label className="booking-field">
          <span>Check-in</span>
          <input type="date" value={checkIn} min={getToday()} onChange={handleCheckInChange} />
          {submitted && fieldErrors.dates && <small className="field-error">{fieldErrors.dates}</small>}
          {!submitted && dateMessage && <small className="field-error">{dateMessage}</small>}
        </label>
        <label className="booking-field">
          <span>Check-out</span>
          <input type="date" value={checkOut} min={addOneDay(checkIn)} onChange={handleCheckOutChange} />
        </label>
        <label className="booking-field">
          <span>Full name</span>
          <input type="text" value={guestName} maxLength={100} autoComplete="name" onChange={(event) => setGuestName(event.target.value)} />
          {submitted && fieldErrors.guestName && <small className="field-error">{fieldErrors.guestName}</small>}
        </label>
        <label className="booking-field">
          <span>Email</span>
          <input type="email" value={guestEmail} maxLength={150} autoComplete="email" onChange={(event) => setGuestEmail(event.target.value)} />
          {submitted && fieldErrors.guestEmail && <small className="field-error">{fieldErrors.guestEmail}</small>}
        </label>
      </div>

      {quoteLoading && <p className="quote-status" role="status">Calculating...</p>}
      {quoteError && <div className="inline-error" role="alert">{quoteError}</div>}
      {bookingError && <div className="inline-error" role="alert">{bookingError}</div>}
      {currentQuote && (
        <div className="quote-summary" aria-live="polite">
          <div><span>{selectedRoom?.type} · {formatPrice(currentQuote.pricePerNight)} x {currentQuote.nights} {currentQuote.nights === 1 ? 'night' : 'nights'}</span><strong>{formatPrice(currentQuote.totalAmount)}</strong></div>
          <div className="quote-total"><span>Total</span><strong>{formatPrice(currentQuote.totalAmount)}</strong></div>
          {!currentQuote.available && <p className="sold-out-message">No rooms available for the selected dates</p>}
        </div>
      )}

      <button className="primary-button booking-submit" type="submit" disabled={!canSubmit}>
        {bookingLoading ? 'Booking...' : 'Confirm booking'}
      </button>
      <p className="secure-note">Your total is confirmed before you book.</p>
    </form>
  )
}

export default BookingForm
