import { Component, OnInit } from '@angular/core';
import { ProductoService } from '../../services/producto.service';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MsalService } from '@azure/msal-angular';
import { ProductoAPI, CarroResponseAPI, CarroItemAPI, CheckoutResponseAPI, VentaAPI } from '../../../../types';

@Component({
  selector: 'app-carro',
  imports: [ CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './carro.component.html',
  styleUrl: './carro.component.css',
  providers: [ProductoService, HttpClient]
})
export class CarroComponent implements OnInit {

  currentCart: CarroResponseAPI | null = null;
  cartItems: CarroItemAPI[] = [];
  cartTotal: number = 0;
  productos: { [key: number]: ProductoAPI } = {};
  isLoading: boolean = false;
  isUpdating: boolean = false;
  isFetchingTotal: boolean = false;
  isCheckingOut: boolean = false;
  checkoutCompleted: boolean = false;
  receiptUrl: string = '';
  userEmail?: string;

  constructor(
    private router: Router,
    private productoService: ProductoService,
    private msalService: MsalService,
  ) {
    // Check authentication
    const token = localStorage.getItem("token");
    if (!token) {
      console.log("No authentication token found, redirecting to principal");
      this.router.navigate(['/principal']);
      return;
    }

    this.userEmail = localStorage.getItem("username") || undefined;
  }

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    const activeCartId = localStorage.getItem("activeCartId");

    if (!activeCartId) {
      console.log("No active cart found");
      return;
    }

    this.isLoading = true;

    // First validate that the cart exists
    this.productoService.getCartById(parseInt(activeCartId)).subscribe(
      (cart: CarroResponseAPI) => {
        this.currentCart = cart;
        this.loadCartItems(cart.carritoId);
      },
      error => {
        console.log("Error loading cart:", error);
        this.isLoading = false;
        // Cart doesn't exist, clear local storage
        localStorage.removeItem("activeCartId");
      }
    );
  }

  private loadCartItems(cartId: number): void {
    this.productoService.getCartItems(cartId).subscribe(
      (items: CarroItemAPI[]) => {
        this.cartItems = items;
        this.loadProductDetails();
        this.fetchCartTotal(cartId);
        this.isLoading = false;
      },
      error => {
        console.log("Error loading cart items:", error);
        this.isLoading = false;
      }
    );
  }

  private loadProductDetails(): void {
    // Load product details for each cart item
    this.cartItems.forEach(item => {
      if (!this.productos[item.productoId]) {
        this.productoService.getProductoId(item.productoId).subscribe(
          (producto: ProductoAPI) => {
            this.productos[item.productoId] = producto;
          },
          error => {
            console.log("Error loading product:", error);
          }
        );
      }
    });
  }

  private fetchCartTotal(cartId: number): void {
    this.isFetchingTotal = true;
    this.productoService.getCartTotal(cartId).subscribe(
      (total: number) => {
        console.log("Cart total fetched:", total);
        this.cartTotal = total;
        this.isFetchingTotal = false;
      },
      error => {
        console.log("Error fetching cart total:", error);
        this.isFetchingTotal = false;
        // Fallback to local calculation if server fails
        this.calculateLocalTotal();
      }
    );
  }

  private calculateLocalTotal(): void {
    // Fallback calculation in case server total fetch fails
    this.cartTotal = this.cartItems.reduce((sum, item) => {
      const producto = this.productos[item.productoId];
      if (producto) {
        return sum + (producto.precio * item.cantidad);
      }
      return sum;
    }, 0);
  }

  getProductQuantity(productoId: number): number {
    const item = this.cartItems.find(item => item.productoId === productoId);
    return item ? item.cantidad : 0;
  }

  increaseQuantity(productoId: number): void {
    if (!this.currentCart || this.isUpdating) return;

    const currentQuantity = this.getProductQuantity(productoId);
    if (currentQuantity > 0) {
      this.isUpdating = true;
      this.updateProductQuantity(this.currentCart.carritoId, productoId, currentQuantity + 1);
    }
  }

  decreaseQuantity(productoId: number): void {
    if (!this.currentCart || this.isUpdating) return;

    const currentQuantity = this.getProductQuantity(productoId);
    if (currentQuantity > 1) {
      this.isUpdating = true;
      this.updateProductQuantity(this.currentCart.carritoId, productoId, currentQuantity - 1);
    } else if (currentQuantity === 1) {
      this.removeFromCart(productoId);
    }
  }

  private updateProductQuantity(carroId: number, productoId: number, cantidad: number): void {
    this.productoService.updateItemQuantity(carroId, productoId, cantidad).subscribe(
      (response: CarroItemAPI) => {
        console.log("Cantidad actualizada", response);
        this.loadCartItems(carroId); // This will also fetch the new total
        this.isUpdating = false;
      },
      (error: any) => {
        console.log("Error actualizando cantidad:", error);
        this.isUpdating = false;
      }
    );
  }

  removeFromCart(productoId: number): void {
    if (!this.currentCart || this.isUpdating) return;

    this.isUpdating = true;
    this.productoService.removeItemFromCart(this.currentCart.carritoId, productoId).subscribe(
      () => {
        console.log("Item eliminado del carro");
        this.loadCartItems(this.currentCart!.carritoId); // This will also fetch the new total
        this.isUpdating = false;
      },
      (error: any) => {
        console.log("Error eliminando item del carro:", error);
        this.isUpdating = false;
      }
    );
  }

  formatPrice(precio: number): string {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(precio);
  }

  getProductImage(productoId: number): string {
    const producto = this.productos[productoId];
    if (producto && producto.imagenUri) {
      return producto.imagenUri;
    }
    return `producto-img/producto_${productoId}.jpeg`;
  }

  onImageError(event: Event, productoId: number): void {
    const img = event.target as HTMLImageElement;
    img.src = `producto-img/producto_${productoId}.jpeg`;
  }

  goHome(): void {
    console.log("Volviendo a principal");
    this.router.navigate(['/principal']);
  }

  isCartEmpty(): boolean {
    return this.cartItems.length === 0;
  }

  getTotalItems(): number {
    return this.cartItems.reduce((total, item) => total + item.cantidad, 0);
  }

  getCartTotalAmount(): number {
    return this.cartTotal;
  }

  getCartSubtotal(): number {
    // Since API only returns total, calculate subtotal locally
    return this.cartItems.reduce((sum, item) => {
      const producto = this.productos[item.productoId];
      if (producto) {
        return sum + (producto.precio * item.cantidad);
      }
      return sum;
    }, 0);
  }

  getCartTaxes(): number {
    // Since API only returns total, we can't determine taxes separately
    return 0;
  }

  getCartDiscount(): number {
    // Since API only returns total, we can't determine discounts separately
    return 0;
  }

  // Checkout functionality
  checkout(): void {
    if (!this.currentCart || !this.userEmail) {
      console.error('No cart or user email available for checkout');
      return;
    }

    this.isCheckingOut = true;
    this.checkoutCompleted = false;

    // Step 1: Call checkout endpoint
    this.productoService.checkout(this.currentCart.carritoId, this.userEmail).subscribe(
      (response: CheckoutResponseAPI) => {
        console.log('Checkout initiated:', response);
        // Step 2: Start polling for status
        this.pollCartStatus(this.currentCart!.carritoId);
      },
      error => {
        console.error('Checkout failed:', error);
        this.isCheckingOut = false;
        alert('Error al procesar el checkout. Por favor, intenta de nuevo.');
      }
    );
  }

  private pollCartStatus(carritoId: number): void {
    const pollInterval = setInterval(() => {
      this.productoService.getCartStatus(carritoId).subscribe(
        statusResponse => {
          console.log('Cart status:', statusResponse);

          if (statusResponse.status === 'C') {
            // Cart is completed, stop polling
            clearInterval(pollInterval);
            this.handleCheckoutSuccess();
          } else if (statusResponse.status === 'A') {
            // Cart is still active, continue polling
            console.log('Cart still processing, continuing to poll...');
          } else {
            // Unknown status, stop polling and show error
            clearInterval(pollInterval);
            console.error('Unknown cart status:', statusResponse.status);
            this.isCheckingOut = false;
            alert('Error inesperado en el estado del carrito.');
          }
        },
        error => {
          console.error('Error polling cart status:', error);
          clearInterval(pollInterval);
          this.isCheckingOut = false;
          alert('Error al verificar el estado del carrito.');
        }
      );
    }, 2000); // Poll every 2 seconds
  }

  private handleCheckoutSuccess(): void {
    if (!this.userEmail) {
      console.error('No user email available for sales history');
      this.isCheckingOut = false;
      return;
    }

    // Fetch user sales to get the receipt URL
    this.productoService.getUserSales(this.userEmail).subscribe(
      (sales: VentaAPI[]) => {
        console.log('User sales:', sales);

        if (sales.length > 0) {
          // Get the last sale (most recent)
          const lastSale = sales[sales.length - 1];
          this.receiptUrl = lastSale.urlRecibo;
        }

        this.isCheckingOut = false;
        this.checkoutCompleted = true;

        // Clear the active cart from localStorage
        localStorage.removeItem('activeCartId');

        console.log('Checkout completed successfully!');
      },
      error => {
        console.error('Error fetching sales history:', error);
        this.isCheckingOut = false;
        this.checkoutCompleted = true; // Still show success even if receipt fetch fails
        // Clear the active cart from localStorage
        localStorage.removeItem('activeCartId');
      }
    );
  }

  // Navigate back to shopping after successful checkout
  continueShopping(): void {
    this.router.navigate(['/']);
  }
}
