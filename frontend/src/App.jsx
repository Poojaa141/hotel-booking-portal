import { Link, NavLink, Route, Routes } from 'react-router-dom'
import HomePage from './pages/HomePage.jsx'
import HotelDetailsPage from './pages/HotelDetailsPage.jsx'
import ManageBookingPage from './pages/ManageBookingPage.jsx'
import './App.css'

function App() {
  return (
    <>
      <header className="site-header">
        <div className="header-inner">
          <Link className="brand" to="/" aria-label="Stayly home">Stayly</Link>
          <nav className="top-nav" aria-label="Main navigation">
            <NavLink to="/" end className={({ isActive }) => isActive ? 'active' : ''}>Home</NavLink>
            <NavLink to="/manage" className={({ isActive }) => isActive ? 'active' : ''}>Manage booking</NavLink>
          </nav>
        </div>
      </header>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/hotels/:id" element={<HotelDetailsPage />} />
        <Route path="/manage" element={<ManageBookingPage />} />
      </Routes>
    </>
  )
}

export default App
