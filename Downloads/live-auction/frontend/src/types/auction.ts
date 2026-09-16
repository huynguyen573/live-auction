export interface Auction {
  id: number
  title: string
  description: string
  startingPrice: number
  currentPrice: number
  endsAt: string // ISO instant
  version: number
}

export interface BidUpdateMessage {
  auctionId: number
  currentPrice: number
  bidderName: string
  placedAt: string
  status: 'ACCEPTED' | 'REJECTED_TOO_LOW' | 'REJECTED_ENDED'
}

export interface Bid {
  id: number
  bidderName: string
  amount: number
  placedAt: string
}
