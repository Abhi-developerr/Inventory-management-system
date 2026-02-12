import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useQueryClient } from '@tanstack/react-query';
import config from '../config';
import { useAuth } from '../store/AuthContext';

const deriveWsUrl = () => {
  const base = (config.wsBaseUrl || '').replace(/\/$/, '');
  if (base) return base;
  const apiBase = (config.apiBaseUrl || '').replace(/\/$/, '');
  const apiRoot = apiBase.replace(/\/api$/, '');
  return `${apiRoot}/api/ws`;
};

export const useInventoryUpdates = (enabled = true) => {
  const { token, user } = useAuth();
  const queryClient = useQueryClient();
  const clientRef = useRef(null);

  useEffect(() => {
    if (!enabled || !token || !user?.organizationId) {
      return undefined;
    }

    const destination = `/topic/inventory/${user.organizationId}`;
    const wsUrl = deriveWsUrl();

    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      debug: () => {},
      onConnect: () => {
        client.subscribe(destination, (message) => {
          try {
            const payload = JSON.parse(message.body);
            applyInventoryUpdate(queryClient, payload);
          } catch (err) {
            console.error('Failed to parse inventory update', err);
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame.headers['message'], frame.body);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
        clientRef.current = null;
      }
    };
  }, [enabled, token, user?.organizationId, queryClient]);
};

const applyInventoryUpdate = (queryClient, payload) => {
  if (!payload?.productId) return;

  const updater = (product) => {
    if (!product || product.id !== payload.productId) return product;
    return {
      ...product,
      name: payload.productName ?? product.name,
      sku: payload.sku ?? product.sku,
      price: payload.price ?? product.price,
      stockQuantity: payload.stockQuantity ?? product.stockQuantity,
      lowStockThreshold: payload.lowStockThreshold ?? product.lowStockThreshold,
      isLowStock: payload.lowStock ?? product.isLowStock,
    };
  };

  // Update any paginated product queries
  queryClient.setQueriesData({ queryKey: ['products'] }, (old) => {
    if (!old) return old;
    if (Array.isArray(old)) {
      return old.map(updater);
    }
    if (Array.isArray(old.content)) {
      return {
        ...old,
        content: old.content.map(updater),
      };
    }
    return old;
  });

  // Update low-stock collection
  queryClient.setQueriesData({ queryKey: ['products', 'low-stock'] }, (old) => {
    if (!old) return old;
    if (!Array.isArray(old)) return old;

    const exists = old.some((p) => p.id === payload.productId);

    if (payload.lowStock) {
      if (exists) {
        return old.map(updater);
      }
      return [
        ...old,
        {
          id: payload.productId,
          name: payload.productName,
          sku: payload.sku,
          price: payload.price,
          stockQuantity: payload.stockQuantity,
          lowStockThreshold: payload.lowStockThreshold,
          isLowStock: payload.lowStock,
        },
      ];
    }

    // Remove if now healthy stock
    if (!exists) return old;
    return old.filter((p) => p.id !== payload.productId);
  });
};
