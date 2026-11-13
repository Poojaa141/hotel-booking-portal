import { useEffect, useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { getHotel } from '../api/api.js'
import BookingForm from '../components/BookingForm.jsx'

function formatPrice(price) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(price)
}

function PinIcon() {
  return <svg viewBox="0 0 20 20" aria-hidden="true"><path d="M16 8.2c0 4.2-6 9-6 9s-6-4.8-6-9a6 6 0 1 1 12 0Z" /><circle cx="10" cy="8" r="2" /></svg>
}

function getToday() {
  const now = new Date()
  const localDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
  return localDate.toISOString().slice(0, 10)
}

function HotelDetailsPage() {
  const { id } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()
  const checkIn = searchParams.get('checkIn') || ''
  const checkOut = searchParams.get('checkOut') || ''
  const [hotel, setHotel] = useState(null)
  const [loading, setLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)
  const [errorMessage, setErrorMessage] = useState('')
  const [imageFailed, setImageFailed] = useState(false)
  const [refreshKey, setRefreshKey] = useState(0)

  useEffect(() => {
    let current = true
    setLoading(true)
    setNotFound(false)
    setErrorMessage('')

    const hasValidDates = Boolean(checkIn && checkOut && checkOut > checkIn && checkIn >= getToday())
    getHotel(id, hasValidDates ? checkIn : '', hasValidDates ? checkOut : '')
      .then((result) => {
        if (current) {
          setHotel(result)
          setImageFailed(false)
        }
      })
      .catch((error) => {
        if (current && error.response?.status === 404) setNotFound(true)
        else if (current) setErrorMessage(error.response?.data?.message || 'Cannot reach the server. Please try again')
      })
      .finally(() => {
        if (current) setLoading(false)
      })

    return () => {
      current = false
    }
  }, [id, checkIn, checkOut, refreshKey])

  function handleDatesChange(nextCheckIn, nextCheckOut) {
    const nextParams = new URLSearchParams()
    if (nextCheckIn) {
      nextParams.set('checkIn', nextCheckIn)
    }
    if (nextCheckIn && nextCheckOut && nextCheckOut > nextCheckIn && nextCheckIn >= getToday()) {
      nextParams.set('checkOut', nextCheckOut)
    }
    setSearchParams(nextParams, { replace: true })
  }

  return (
    <>
      <main className="details-page">
        <Link className="back-link" to="/">Back to results</Link>

        {loading ? (
          <div className="details-skeleton" aria-label="Loading hotel">
            <div className="skeleton-block details-skeleton-photo" />
            <div className="skeleton-block details-skeleton-line" />
            <div className="skeleton-block details-skeleton-line short" />
          </div>
        ) : notFound ? (
          <section className="details-message"><h1>Hotel not found</h1><p>We could not find that stay. Try browsing the hotels again.</p><Link className="primary-button" to="/">Browse hotels</Link></section>
        ) : errorMessage ? (
          <section className="details-message"><h1>Hotel details are unavailable</h1><p role="alert">{errorMessage}</p><Link className="secondary-button" to="/">Back to results</Link></section>
        ) : hotel ? (
          <>
            <section className="details-hero">
              <div className="details-hero-image" style={{ background: 'linear-gradient(135deg, #5e3e22, #8a6240)' }}>
                {hotel.imageUrl && !imageFailed && <img src={hotel.imageUrl} alt={hotel.name} onError={() => setImageFailed(true)} />}
                <div className="details-hero-shade" />
                {!hotel.imageUrl || imageFailed ? <span className="details-hero-initial" aria-hidden="true">{hotel.name?.charAt(0) || 'S'}</span> : null}
              </div>
              <div className="details-title">
                <div>
                  <h1>{hotel.name}</h1>
                  <p><PinIcon />{hotel.location}</p>
                </div>
                {hotel.bestDeal && <span className="deal-badge details-deal">Best Deal</span>}
              </div>
            </section>

            <div className="details-layout">
              <div className="details-copy">
                <section className="details-section">
                  <p className="eyebrow">A place to unwind</p>
                  <h2>About this stay</h2>
                  <p className="details-description">{hotel.description}</p>
                </section>
                <section className="details-section">
                  <p className="eyebrow">Thoughtful comforts</p>
                  <h2>Amenities</h2>
                  <div className="details-amenities">{(hotel.amenities || []).map((amenity) => <span className="amenity-chip" key={amenity}>{amenity}</span>)}</div>
                </section>
                <section className="details-room">
                  <div>
                    <p className="eyebrow">Your room</p>
                    <h2>{hotel.roomType}</h2>
                    <p className="room-category">One room category at this property</p>
                  </div>
                  <div className="room-price">
                    <strong>{formatPrice(hotel.pricePerNight)}</strong>
                    <span>per night</span>
                    {hotel.roomsLeft === 1 ? <em>Only 1 left</em> : hotel.available ? <span>{hotel.roomsLeft} rooms left</span> : <em>Sold out</em>}
                  </div>
                </section>
              </div>

              <aside className="booking-sidebar">
                <BookingForm
                  key={hotel.id}
                  hotel={hotel}
                  initialCheckIn={checkIn}
                  initialCheckOut={checkOut}
                  onDatesChange={handleDatesChange}
                  onRefreshHotel={() => setRefreshKey((value) => value + 1)}
                />
              </aside>
            </div>
          </>
        ) : null}
      </main>
    </>
  )
}

export default HotelDetailsPage
