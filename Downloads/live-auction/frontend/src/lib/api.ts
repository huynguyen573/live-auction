import type { Auction } from '../types/auction'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api'

export async function fetchAuctions(): Promise<Auction[]> {
  const res = await fetch(`${API_BASE}/auctions`)
  if (!res.ok) throw new Error('Failed to fetch auctions')
  return res.json()
}

export async function fetchAuction(id: number): Promise<Auction> {
  const res = await fetch(`${API_BASE}/auctions/${id}`)
  if (!res.ok) throw new Error('Failed to fetch auction')
  return res.json()
}
