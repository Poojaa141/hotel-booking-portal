import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export async function searchHotels(location = '', checkIn = '', checkOut = '') {
  const params = {}

  if (location.trim()) {
    params.location = location.trim()
  }
  if (checkIn) {
    params.checkIn = checkIn
  }
  if (checkOut) {
    params.checkOut = checkOut
  }

  const response = await api.get('/hotels', { params })
  return response.data
}
