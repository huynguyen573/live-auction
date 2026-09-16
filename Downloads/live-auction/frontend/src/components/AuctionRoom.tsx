import { useEffect, useState } from 'react'
import { useAuctionSocket } from '../hooks/useAuctionSocket'
import type { Auction } from '../types/auction'

interface Props {
  auction: Auction
  bidderName: string
}

export default function AuctionRoom({ auction, bidderName }: Props) {
  const { status, updates, placeBid } = useAuctionSocket(auction.id)
  const [bidAmount, setBidAmount] = useState<string>('')
  const [currentPrice, setCurrentPrice] = useState(auction.currentPrice)

  // Reset price when a different auction is selected
  useEffect(() => {
    setCurrentPrice(auction.currentPrice)
  }, [auction.id, auction.currentPrice])

  // Advance price on every accepted bid (updates is newest-first)
  useEffect(() => {
    const latest = updates.find((u) => u.status === 'ACCEPTED')
    if (latest) setCurrentPrice(latest.currentPrice)
  }, [updates])

  // Show the most recent rejection only if it arrived after the last accepted bid
  const lastRejection = (() => {
    for (const u of updates) {
      if (u.status === 'ACCEPTED') return null
      if (u.status !== 'ACCEPTED') return u
    }
    return null
  })()

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    const amount = parseFloat(bidAmount)
    if (!isNaN(amount)) {
      placeBid(bidderName, amount)
      setBidAmount('')
    }
  }

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-xl shadow">
      <div className="flex items-center justify-between mb-2">
        <h2 className="text-xl font-semibold">{auction.title}</h2>
        <span
          className={`text-xs px-2 py-1 rounded-full ${
            status === 'connected' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'
          }`}
        >
          {status}
        </span>
      </div>

      <p className="text-gray-600 text-sm mb-4">{auction.description}</p>

      <div className="text-3xl font-bold mb-4">${currentPrice.toFixed(2)}</div>

      <form onSubmit={handleSubmit} className="flex gap-2 mb-4">
        <input
          type="number"
          step="0.01"
          min={currentPrice + 0.01}
          value={bidAmount}
          onChange={(e) => setBidAmount(e.target.value)}
          placeholder={`> $${currentPrice.toFixed(2)}`}
          className="flex-1 border rounded px-3 py-2"
          required
        />
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Bid
        </button>
      </form>

      {lastRejection && (
        <p className="text-sm text-red-600 mb-2">
          Last bid rejected: {lastRejection.status === 'REJECTED_TOO_LOW' ? 'too low' : 'auction ended'}
        </p>
      )}

      <h3 className="text-sm font-medium text-gray-500 mb-2">Live activity</h3>
      <ul className="space-y-1 max-h-48 overflow-y-auto text-sm">
        {updates.map((u, i) => (
          <li key={i} className="flex justify-between text-gray-700">
            <span>{u.bidderName}</span>
            <span>
              {u.status === 'ACCEPTED' ? `$${u.currentPrice.toFixed(2)}` : `rejected`}
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}
