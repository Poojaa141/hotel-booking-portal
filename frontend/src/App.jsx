import { Route, Routes } from 'react-router-dom'
import HomePage from './pages/HomePage.jsx'
import HotelDetailsPage from './pages/HotelDetailsPage.jsx'
import './App.css'

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/hotels/:id" element={<HotelDetailsPage />} />
    </Routes>
  )
}

export default App
