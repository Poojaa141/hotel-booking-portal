export function formatDate(isoString) {
  if (!isoString) return ''

  const [year, month, day] = isoString.slice(0, 10).split('-').map(Number)
  const date = new Date(Date.UTC(year, month - 1, day))

  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(date)
}
