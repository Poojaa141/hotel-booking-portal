import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { searchHotels } from '../api/api.js'
import HotelCard from '../components/HotelCard.jsx'

function getToday() {
  const now = new Date()
  const localDate = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
  return localDate.toISOString().slice(0, 10)
}

function addOneDay(date) {
  if (!date) {
    return getToday()
  }

  const [year, month, day] = date.split('-').map(Number)
  const nextDay = new Date(year, month - 1, day + 1)
  const nextYear = nextDay.getFullYear()
  const nextMonth = String(nextDay.getMonth() + 1).padStart(2, '0')
  const nextDate = String(nextDay.getDate()).padStart(2, '0')
  return `${nextYear}-${nextMonth}-${nextDate}`
}

function CalendarIcon() {
  return (
    <svg viewBox="0 0 20 20" aria-hidden="true">
      <rect x="3" y="5" width="14" height="12" rx="2" />
      <path d="M6.5 3.5v3M13.5 3.5v3M3 8.5h14" />
    </svg>
  )
}

function HotelSkeleton() {
  return (
    <div className="skeleton-card" aria-hidden="true">
      <div className="skeleton-block skeleton-image" />
      <div className="skeleton-block skeleton-line medium" />
      <div className="skeleton-block skeleton-line short" />
      <div className="skeleton-block skeleton-line long" />
      <div className="skeleton-block skeleton-line medium" />
    </div>
  )
}

function getErrorMessage(error) {
  if (error.response) {
    return error.response.data?.message || 'Something went wrong. Please try again'
  }
  return 'Cannot reach the server. Please try again'
}

function HomePage() {
  const [location, setLocation] = useState('')
  const [checkIn, setCheckIn] = useState('')
  const [checkOut, setCheckOut] = useState('')
  const [hotels, setHotels] = useState([])
  const [loading, setLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState('')
  const [validationMessage, setValidationMessage] = useState('')

  useEffect(() => {
    let isCurrent = true

    searchHotels()
      .then((results) => {
        if (isCurrent) {
          setHotels(results)
        }
      })
      .catch((error) => {
        if (isCurrent) {
          setErrorMessage(getErrorMessage(error))
        }
      })
      .finally(() => {
        if (isCurrent) {
          setLoading(false)
        }
      })

    return () => {
      isCurrent = false
    }
  }, [])

  async function loadHotels(searchLocation, arrival, departure) {
    setLoading(true)
    setErrorMessage('')

    try {
      const results = await searchHotels(searchLocation, arrival, departure)
      setHotels(results)
    } catch (error) {
      setErrorMessage(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  async function handleSearch(event) {
    event.preventDefault()
    setValidationMessage('')

    if (!checkIn || !checkOut) {
      setValidationMessage('Please select both check-in and check-out dates')
      return
    }
    if (checkOut <= checkIn) {
      setValidationMessage('Check-out date must be after check-in date')
      return
    }
    if (checkIn < getToday()) {
      setValidationMessage('Check-in date cannot be in the past')
      return
    }

    await loadHotels(location, checkIn, checkOut)
  }

  async function clearSearch() {
    setLocation('')
    setCheckIn('')
    setCheckOut('')
    setValidationMessage('')
    await loadHotels('', '', '')
  }

  function handleCheckInChange(event) {
    const nextCheckIn = event.target.value
    setCheckIn(nextCheckIn)
    if (checkOut && nextCheckIn && checkOut <= nextCheckIn) {
      setCheckOut('')
    }
  }

  return (
    <>
      <section className="hero">
        <div className="hero-inner">
          <h1>Find your perfect stay</h1>
          <p>Book unforgettable experiences</p>
        </div>
      </section>

      <section className="search-wrap" aria-label="Search stays">
        <form className="search-card" onSubmit={handleSearch} noValidate>
          <label className="field">
            <span className="field-label">Location</span>
            <input
              type="text"
              value={location}
              onChange={(event) => setLocation(event.target.value)}
              placeholder="Where would you like to go?"
            />
          </label>

          <label className="field">
            <span className="field-label">
              <CalendarIcon /> Check-in
            </span>
            <input
              type="date"
              value={checkIn}
              min={getToday()}
              onChange={handleCheckInChange}
              required
            />
          </label>

          <label className="field">
            <span className="field-label">
              <CalendarIcon /> Check-out
            </span>
            <input
              type="date"
              value={checkOut}
              min={addOneDay(checkIn)}
              onChange={(event) => setCheckOut(event.target.value)}
              required
            />
          </label>

          <button className="search-button" type="submit">Search</button>
        </form>
        {validationMessage && <p className="validation-message" role="alert">{validationMessage}</p>}
      </section>

      <main className="results-section">
        <div className="results-heading">
          <h2>Top picks for you</h2>
          {!loading && <span className="results-count">{hotels.length} {hotels.length === 1 ? 'stay' : 'stays'} found</span>}
        </div>

        {errorMessage && <div className="error-box" role="alert">{errorMessage}</div>}

        {loading ? (
          <div className="hotel-grid">
            <HotelSkeleton />
            <HotelSkeleton />
            <HotelSkeleton />
          </div>
        ) : hotels.length > 0 ? (
          <div className="hotel-grid">
            {hotels.map((hotel) => (
              <HotelCard key={hotel.id} hotel={hotel} checkIn={checkIn} checkOut={checkOut} />
            ))}
          </div>
        ) : (
          !errorMessage && (
            <div className="empty-state">
              <p>No hotels found for this location</p>
              <button className="clear-button" type="button" onClick={clearSearch}>Clear search</button>
            </div>
          )
        )}
      </main>
    </>
  )
}

export default HomePage
