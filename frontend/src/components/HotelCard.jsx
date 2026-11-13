import { useState } from 'react'
import { Link } from 'react-router-dom'

const imagePalettes = [
  'linear-gradient(135deg, #5e3e22, #8a6240)',
  'linear-gradient(135deg, #666447, #9a9873)',
  'linear-gradient(135deg, #94543f, #c17d63)',
  'linear-gradient(135deg, #a98d68, #d1b894)',
]

function PinIcon() {
  return (
    <svg viewBox="0 0 20 20" aria-hidden="true">
      <path d="M16 8.2c0 4.2-6 9-6 9s-6-4.8-6-9a6 6 0 1 1 12 0Z" />
      <circle cx="10" cy="8" r="2" />
    </svg>
  )
}

function formatPrice(price) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(price)
}

function HotelCard({ hotel, checkIn, checkOut }) {
  const [imageFailed, setImageFailed] = useState(false)
  const paletteIndex = Math.abs(Number(hotel.id) - 1) % imagePalettes.length
  const searchParams = new URLSearchParams()

  if (checkIn) {
    searchParams.set('checkIn', checkIn)
  }
  if (checkOut) {
    searchParams.set('checkOut', checkOut)
  }

  const query = searchParams.toString()
  const detailsPath = `/hotels/${hotel.id}${query ? `?${query}` : ''}`
  const amenities = hotel.amenities || []
  const shownAmenities = amenities.slice(0, 4)
  const remainingAmenities = amenities.length - shownAmenities.length
  const roomTypes = (hotel.rooms || []).map((room) => room.type).join(' · ')
  const roomText = hotel.roomsLeft === 1 ? 'Only 1 left' : `${hotel.roomsLeft} rooms left`

  return (
    <article className="hotel-card">
      <div className="hotel-image" style={{ background: imagePalettes[paletteIndex] }}>
        {hotel.imageUrl && !imageFailed ? (
          <img
            className="hotel-photo"
            src={hotel.imageUrl}
            alt={hotel.name}
            loading="lazy"
            onError={() => setImageFailed(true)}
          />
        ) : (
          <span className="hotel-initial" aria-hidden="true">{hotel.name?.charAt(0) || 'S'}</span>
        )}
        <div className="hotel-image-shade" />
        {hotel.bestDeal && <span className="deal-badge">Best Deal</span>}
        {!hotel.available && (
          <div className="sold-out-overlay">
            <span className="sold-out-label">Sold out</span>
          </div>
        )}
      </div>

      <div className="hotel-card-body">
        <h3 title={hotel.name}>{hotel.name}</h3>
        <p className="hotel-location"><PinIcon />{hotel.location}</p>
        <p className="hotel-description">{hotel.description}</p>

        <div className="amenity-list" aria-label="Amenities">
          {shownAmenities.map((amenity) => <span className="amenity-chip" key={amenity}>{amenity}</span>)}
          {remainingAmenities > 0 && <span className="amenity-more">+{remainingAmenities} more</span>}
        </div>
        {roomTypes && <p className="room-type-list">{roomTypes}</p>}

        <div className="hotel-card-footer">
          <div className="price-line">
            <span className="price">From {formatPrice(hotel.startingPrice)}</span>
            <span className="per-night">/ night</span>
          </div>
          <span className={`rooms-left ${hotel.roomsLeft === 1 ? 'only-one' : ''}`}>
            {roomText}
          </span>
        </div>

        <Link
          className={`view-button ${!hotel.available ? 'is-disabled' : ''}`}
          to={detailsPath}
          aria-disabled={!hotel.available}
          tabIndex={hotel.available ? undefined : -1}
          onClick={(event) => {
            if (!hotel.available) {
              event.preventDefault()
            }
          }}
        >
          View rooms
        </Link>
      </div>
    </article>
  )
}

export default HotelCard
