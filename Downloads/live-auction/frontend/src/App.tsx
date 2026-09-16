import { useEffect, useState } from 'react'
import { fetchAuctions } from './lib/api'
import type { Auction } from './types/auction'
import AuctionRoom from './components/AuctionRoom'

export default function App() {
  const [auctions, setAuctions] = useState<Auction[]>([])
  const [selected, setSelected] = useState<Auction | null>(null)
  const [bidderName, setBidderName] = useState('Guest' + Math.floor(Math.random() * 1000))
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchAuctions()
      .then((data) => {
        setAuctions(data)
        if (data.length > 0) setSelected(data[0])
      })
      .catch(() => setError('Could not reach the backend. Is it running on :8080?'))
  }, [])

  return (
    <div className="min-h-screen py-10">
      <h1 className="text-2xl font-bold text-center mb-2">Live Auction</h1>
      <p className="text-center text-sm text-gray-500 mb-8">
        Bidding as <span className="font-medium">{bidderName}</span>{' '}
        <button
          className="underline"
          onClick={() => setBidderName('Guest' + Math.floor(Math.random() * 1000))}
        >
          (shuffle name)
        </button>
      </p>

      {error && <p className="text-center text-red-600 mb-4">{error}</p>}

      {auctions.length > 1 && (
        <div className="flex justify-center gap-2 mb-6">
          {auctions.map((a) => (
            <button
              key={a.id}
              onClick={() => setSelected(a)}
              className={`px-3 py-1 rounded ${
                selected?.id === a.id ? 'bg-blue-600 text-white' : 'bg-gray-200'
              }`}
            >
              {a.title}
            </button>
          ))}
        </div>
      )}

      {selected && <AuctionRoom auction={selected} bidderName={bidderName} />}
    </div>
  )
}
