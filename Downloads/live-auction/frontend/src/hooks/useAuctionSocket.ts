import { useEffect, useRef, useState, useCallback } from 'react'
import { Client, IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import type { BidUpdateMessage } from '../types/auction'

const WS_URL = import.meta.env.VITE_WS_URL ?? 'http://localhost:8080/ws'

type ConnectionStatus = 'connecting' | 'connected' | 'disconnected'

/**
 * Manages the STOMP connection for a single auction room.
 *
 * - Auto-reconnects on drop (handled by @stomp/stompjs's reconnectDelay).
 * - Subscribes to /topic/auctions/{auctionId} for live price/bid pushes.
 * - Exposes placeBid() which sends over /app/auctions/{auctionId}/bid.
 *
 * Talking point for interviews: SockJS is a fallback transport, not a
 * requirement -- if raw WebSocket is available it's used directly; SockJS
 * only kicks in on networks/proxies that block WS upgrades.
 */
export function useAuctionSocket(auctionId: number) {
  const [status, setStatus] = useState<ConnectionStatus>('connecting')
  const [updates, setUpdates] = useState<BidUpdateMessage[]>([])
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL) as unknown as WebSocket,
      reconnectDelay: 3000,
      onConnect: () => {
        setStatus('connected')
        client.subscribe(`/topic/auctions/${auctionId}`, (message: IMessage) => {
          const update: BidUpdateMessage = JSON.parse(message.body)
          setUpdates((prev) => [update, ...prev].slice(0, 50))
        })
      },
      onDisconnect: () => setStatus('disconnected'),
      onWebSocketClose: () => setStatus('disconnected'),
    })

    client.activate()
    clientRef.current = client

    return () => {
      client.deactivate()
    }
  }, [auctionId])

  const placeBid = useCallback(
    (bidderName: string, amount: number) => {
      clientRef.current?.publish({
        destination: `/app/auctions/${auctionId}/bid`,
        body: JSON.stringify({ bidderName, amount }),
      })
    },
    [auctionId],
  )

  return { status, updates, placeBid }
}
