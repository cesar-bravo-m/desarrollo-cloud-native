export type BigDecimal = string;
export type OffsetDateTime = string;

// OpenAPI spec compatible types
export interface ProductoAPI {
  productoId: number;
  nombre: string;
  descripcion?: string;
  cantidadStock: number;
  registroFecha: string;
  valorCosto: number;
  precio: number;
  imagenUri?: string;
}

export interface CarroAPI {
  carroId: number;
  usuarioId: string;
  productoId: number;
  cantidad: number;
  registroFecha: string;
  vigenciaFlag: number;
}

export interface CarroCreateAPI {
  usuarioId: string;
  estado: string;
  creadoEn: string;
  actualizadoEn?: string | null;
}

export interface CarroResponseAPI {
  carritoId: number;
  usuarioId: string;
  estado: string;
  creadoEn: string;
  actualizadoEn?: string | null;
}

export interface CarroItemAPI {
  carroId: number;
  productoId: number;
  cantidad: number;
  producto?: ProductoAPI;
  subtotal?: number;
}

// Carrito entity
export interface Carrito {
  carritoId: number;
  usuarioId: string;
  estado: string; // 'A' = Activo, 'C' = Completado, 'X' = Abandonado
  creadoEn: OffsetDateTime;
  actualizadoEn?: OffsetDateTime;
}

// Producto entity
export interface Producto {
  productoId: number;
  sku: string;
  nombre: string;
  imagenUri: string;
  descripcion?: string;
  precio: BigDecimal;
  cantidadStock: number;
  creadoEn: OffsetDateTime;
  actualizadoEn?: OffsetDateTime;
}

// CarritoItem entity
export interface CarritoItem {
  carritoItemId: number;
  carritoId: number;
  productoId: number;
  cantidad: number;
  precioUnitario: BigDecimal;
  agregadoEn: OffsetDateTime;
  // Relationships (optional, may be populated depending on API response)
  carrito?: Carrito;
  producto?: Producto;
}

// Venta entity
export interface Venta {
  ventaId: number;
  carritoId: number;
  usuarioId: string;
  montoTotal: BigDecimal;
  urlRecibo: string;
  creadoEn: OffsetDateTime;
  // Relationship (optional, may be populated depending on API response)
  carrito?: Carrito;
}

// SalesMessage for RabbitMQ
export interface SalesMessage {
  carritoId: number;
  usuarioId: string;
}

// SaleItem for SaleEvent
export interface SaleItem {
  productoId: number;
  productoSku: string;
  productoNombre: string;
  cantidad: number;
  precioUnitario: BigDecimal;
  subtotal: BigDecimal;
}

// SaleEvent for event publishing
export interface SaleEvent {
  carritoId: number;
  usuarioId: string;
  montoTotal: BigDecimal;
  urlRecibo: string;
  fechaVenta: OffsetDateTime;
  items: SaleItem[];
  estado: string;
  eventType: string;
}

// DTOs and Request/Response types

// Create Carrito request
export interface CreateCarritoRequest {
  usuarioId: string;
}

// Add item to carrito request
export interface AddCarritoItemRequest {
  productoId: number;
  cantidad: number;
}

// Update carrito item request
export interface UpdateCarritoItemRequest {
  cantidad: number;
}

// Carrito with items (for detailed responses)
export interface CarritoWithItems extends Carrito {
  items: CarritoItem[];
  total?: BigDecimal;
}

// Product with stock status
export interface ProductoWithStock extends Producto {
  inStock: boolean;
  lowStock: boolean;
}

// Sale summary
export interface SaleSummary {
  ventaId: number;
  carritoId: number;
  usuarioId: string;
  montoTotal: BigDecimal;
  fechaVenta: OffsetDateTime;
  itemCount: number;
  estado: string;
}

// API Response wrappers
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Enums and constants
export enum CarritoEstado {
  ACTIVO = 'A',
  COMPLETADO = 'C',
  CANCELADO = 'X'
}

export enum EventType {
  SALE_COMPLETED = 'SALE_COMPLETED',
  SALE_CANCELLED = 'SALE_CANCELLED',
  STOCK_UPDATED = 'STOCK_UPDATED'
}

// Utility types
export type CarritoId = number;
export type ProductoId = number;
export type VentaId = number;
export type UsuarioId = string;

// Type guards
export function isCarritoActivo(carrito: Carrito): boolean {
  return carrito.estado === CarritoEstado.ACTIVO;
}

export function isCarritoCompletado(carrito: Carrito): boolean {
  return carrito.estado === CarritoEstado.COMPLETADO;
}

export function isProductoInStock(producto: Producto): boolean {
  return producto.cantidadStock > 0;
}

export function isProductoLowStock(producto: Producto, threshold: number = 10): boolean {
  return producto.cantidadStock <= threshold && producto.cantidadStock > 0;
}

// Helper functions for calculations
export function calculateCarritoTotal(items: CarritoItem[]): BigDecimal {
  return items.reduce((total, item) => {
    const itemTotal = parseFloat(item.precioUnitario) * item.cantidad;
    return (parseFloat(total) + itemTotal).toFixed(2);
  }, '0.00');
}

export function calculateSaleItemSubtotal(item: SaleItem): BigDecimal {
  return (parseFloat(item.precioUnitario) * item.cantidad).toFixed(2);
}
