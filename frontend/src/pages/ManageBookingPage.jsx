import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { cancelBooking, getBooking } from '../api/api.js'
import BookingDetails from '../components/BookingDetails.jsx'

function ManageBookingPage() {
  const [searchParams] = useSearchParams()
  const [reference, setReference] = useState(searchParams.get('ref') || '')
  const [email, setEmail] = useState('')
  const [booking, setBooking] = useState(null)
  const [fieldErrors, setFieldErrors] = useState({})
  const [lookupError, setLookupError] = useState('')
  const [lookupLoading, setLookupLoading] = useState(false)
  const [cancelLoading, setCancelLoading] = useState(false)
  const [cancelMessage, setCancelMessage] = useState('')
  const [cancelError, setCancelError] = useState('')

  useEffect(() => {
    setReference(searchParams.get('ref') || '')
  }, [searchParams])

  async function handleLookup(event) {
    event.preventDefault()
    setBooking(null)
    setLookupError('')
    setCancelMessage('')
    setCancelError('')

    const cleanReference = reference.trim().toUpperCase()
    const cleanEmail = email.trim()
    const errors = {}

    if (!cleanReference) errors.reference = 'Please enter your booking reference'
    if (!cleanEmail) errors.email = 'Please enter your email address'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(cleanEmail)) errors.email = 'Please enter a valid email address'

    setFieldErrors(errors)
    if (Object.keys(errors).length > 0) return

    setLookupLoading(true)
    try {
      const result = await getBooking(cleanReference, cleanEmail)
      setBooking(result)
    } catch (error) {
      if (error.response) {
        setLookupError(error.response.data?.message || 'Something went wrong. Please try again')
      } else {
        setLookupError('Cannot reach the server. Please try again')
      }
    } finally {
      setLookupLoading(false)
    }
  }

  async function handleCancel() {
    setCancelLoading(true)
    setCancelError('')
    setCancelMessage('')

    try {
      const result = await cancelBooking(booking.bookingRef, email.trim())
      setBooking(result)
      setCancelMessage('Your booking has been cancelled')
    } catch (error) {
      if (error.response) {
        setCancelError(error.response.data?.message || 'Something went wrong. Please try again')
      } else {
        setCancelError('Cannot reach the server. Please try again')
      }
    } finally {
      setCancelLoading(false)
    }
  }

  return (
    <main className="manage-page">
      <div className="manage-intro">
        <p className="eyebrow">Stayly reservations</p>
        <h1>Manage your booking</h1>
        <p>Enter the reference and email from your confirmation.</p>
      </div>

      <form className="lookup-card" onSubmit={handleLookup} noValidate>
        <label className="manage-field">
          <span>Booking reference</span>
          <input
            type="text"
            value={reference}
            placeholder="HB-XXXXXXXX"
            autoCapitalize="characters"
            maxLength={20}
            onChange={(event) => setReference(event.target.value)}
            aria-invalid={Boolean(fieldErrors.reference)}
          />
          {fieldErrors.reference && <small className="field-error">{fieldErrors.reference}</small>}
        </label>
        <label className="manage-field">
          <span>Email</span>
          <input
            type="email"
            value={email}
            autoComplete="email"
            onChange={(event) => setEmail(event.target.value)}
            aria-invalid={Boolean(fieldErrors.email)}
          />
          {fieldErrors.email && <small className="field-error">{fieldErrors.email}</small>}
        </label>
        <button className="primary-button lookup-button" type="submit" disabled={lookupLoading}>
          {lookupLoading ? 'Searching...' : 'Find booking'}
        </button>
        {lookupError && <div className="manage-error lookup-error" role="alert">{lookupError}</div>}
      </form>

      {booking && (
        <BookingDetails
          booking={booking}
          onCancel={handleCancel}
          cancelLoading={cancelLoading}
          cancelMessage={cancelMessage}
          cancelError={cancelError}
        />
      )}
    </main>
  )
}

export default ManageBookingPage
