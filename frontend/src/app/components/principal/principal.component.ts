import { formatDate, CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { MsalService } from '@azure/msal-angular';
import { EventMessage, EventType, AccountInfo, AuthenticationResult } from '@azure/msal-browser';
import { loginRequest } from '../../auth-config';

import { ProductoService } from '../../services/producto.service';
import { ProductoAPI, CarroAPI, CarroCreateAPI, CarroResponseAPI, CarroItemAPI } from '../../../../types';

@Component({
  selector: 'app-principal',
  imports: [ CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './principal.component.html',
  styleUrl: './principal.component.css',
  providers: [ProductoService, HttpClient]
})
export class PrincipalComponent implements OnInit {

  productos: ProductoAPI[] = [];
  carroItems: CarroAPI[] = [];
  currentCart: CarroResponseAPI | null = null;
  cartItems: CarroItemAPI[] = [];
  usuarioId: number = 0;
  carro_items: number = 0;
  isLoading: boolean = false;
  isAddingToCart: boolean = false;
  userEmail?: string;

  constructor(
    private router: Router,
    private productoService: ProductoService,
    private msalService: MsalService,
  ) {
    this.getProductos();
    this.usuarioId = JSON.parse(localStorage.getItem("usuarioId") || "0");
    this.initializeCart();
  }

  ngOnInit(): void {
    this.msalService.initialize().subscribe(() => {
      this.setUserFromAccount(this.msalService.instance.getActiveAccount() || this.msalService.instance.getAllAccounts()[0]);

      this.msalService.instance.addEventCallback((event: EventMessage) => {
        if (event.eventType === EventType.LOGIN_SUCCESS && event.payload && (event.payload as any).account) {
          const account = (event.payload as any).account as AccountInfo;
          this.msalService.instance.setActiveAccount(account);
          this.setUserFromAccount(account);
          this.router.navigate(["/principal"]);
        }

        if (event.eventType === EventType.LOGOUT_END) {
          this.setUserFromAccount(undefined);
          this.router.navigate(["/principal"]);
        }
      });
    });
  }

  private setUserFromAccount(account?: AccountInfo) {
    this.userEmail = account ? account.username : undefined;
    if (this.userEmail) {
      this.initializeCart();
    }
  }

  private initializeCart(): void {
    const username = localStorage.getItem("username");
    if (username) {
      this.loadUserCart(username);
    }
  }

    private loadUserCart(usuarioId: string): void {
    // First, check if we have an active cart ID in local storage
    const activeCartId = localStorage.getItem("activeCartId");
    console.log(`Loading user cart for ${usuarioId}, activeCartId: ${activeCartId}`);

    if (activeCartId) {
      // Try to load the cart by ID first
      console.log(`Found active cart ID in storage: ${activeCartId}`);
      this.loadCartById(parseInt(activeCartId));
    } else {
      // If no active cart ID, check if user has any active carts
      console.log("No active cart ID found, checking for active carts");
              this.productoService.getActiveCart(usuarioId).subscribe(
          (cart: CarroAPI | null) => {
            if (cart) {
              console.log(`Found active cart for user: ${cart.carroId}`);
              // Convert CarroAPI to CarroResponseAPI format for consistency
              const cartResponse: CarroResponseAPI = {
                carritoId: cart.carroId,
                usuarioId: cart.usuarioId,
                estado: cart.vigenciaFlag === 1 ? "A" : "I",
                creadoEn: cart.registroFecha,
                actualizadoEn: null
              };
              this.setActiveCart(cartResponse);
            } else {
              console.log("No active cart found for user");
              this.resetCartState();
            }
          },
        error => {
          console.log("Error loading user cart:", error);
          this.resetCartState();
        }
      );
    }
  }

  private loadCartById(cartId: number): void {
    // First validate that the cart exists
    this.productoService.getCartById(cartId).subscribe(
      (cart: CarroResponseAPI) => {
        // Cart exists, set it as current and load items
        this.currentCart = cart;
        this.loadCartItems(cart.carritoId);
      },
      error => {
        console.log("Error loading cart by ID, cart may not exist:", error);
        // Cart doesn't exist, remove from local storage and reset
        localStorage.removeItem("activeCartId");
        this.resetCartState();
        // Try to find another active cart for this user
        const username = localStorage.getItem("username");
        if (username) {
          this.productoService.getActiveCart(username).subscribe(
            (cart: CarroAPI | null) => {
              if (cart) {
                // Convert CarroAPI to CarroResponseAPI format for consistency
                const cartResponse: CarroResponseAPI = {
                  carritoId: cart.carroId,
                  usuarioId: cart.usuarioId,
                  estado: cart.vigenciaFlag === 1 ? "A" : "I",
                  creadoEn: cart.registroFecha,
                  actualizadoEn: null
                };
                this.setActiveCart(cartResponse);
              } else {
                this.resetCartState();
              }
            },
            error => {
              console.log("Error loading fallback cart:", error);
              this.resetCartState();
            }
          );
        }
      }
    );
  }

  private setActiveCart(cart: CarroResponseAPI): void {
    console.log(`Setting active cart: ${cart.carritoId}`);
    this.currentCart = cart;
    localStorage.setItem("activeCartId", cart.carritoId.toString());
    this.loadCartItems(cart.carritoId);
  }

  private resetCartState(): void {
    console.log("Resetting cart state");
    this.currentCart = null;
    this.cartItems = [];
    this.carro_items = 0;
    localStorage.removeItem("activeCartId");
  }

  private loadCartItems(carroId: number): void {
    this.productoService.getCartItems(carroId).subscribe(
      (items: CarroItemAPI[]) => {
        this.cartItems = items;
        this.carro_items = items.reduce((total, item) => total + item.cantidad, 0);
      },
      error => {
        console.log("Error loading cart items:", error);
      }
    );
  }

  getProductos(): void {
    this.isLoading = true;
    this.productoService.getProducto().subscribe(
      (productos: ProductoAPI[]) => {
        console.log("Recuperando productos", productos);
        this.productos = productos;
        this.isLoading = false;
      },
      error => {
        console.log("Se ha producido un error\nApi Recover error: "+error.message+" / "+error.status);
        this.isLoading = false;
        if (error.message && error.message.includes('No authentication token available')) {
          this.login();
        }
      }
    );
  }

    agregarCarro(productoId: number): void {
    const token = localStorage.getItem("token");
    if (!token) {
      console.log("No authentication token found, redirecting to login");
      this.login();
      return;
    }

    const username = localStorage.getItem("username");
    if (!username) {
      console.log("No username found, redirecting to login");
      this.login();
      return;
    }

    this.isAddingToCart = true;

    // Check if we have an active cart in local storage
    const activeCartId = localStorage.getItem("activeCartId");

    if (activeCartId && this.currentCart) {
      // We have an active cart, check if product is already in it
      const existingItem = this.cartItems.find(item => item.productoId === productoId);

      if (existingItem) {
        // Product exists in cart, update quantity
        const newQuantity = existingItem.cantidad + 1;
        this.updateProductQuantity(this.currentCart.carritoId, productoId, newQuantity);
      } else {
        // Cart exists but product not in cart, add new item
        this.addProductToExistingCart(this.currentCart.carritoId, productoId, 1);
      }
    } else {
      // No active cart, create new cart with product
      this.createNewCartWithProduct(username, productoId, 1);
    }
  }

  private createNewCartWithProduct(usuarioId: string, productoId: number, cantidad: number): void {
    this.productoService.createCartWithProduct(usuarioId, productoId, cantidad).subscribe(
      (response: CarroResponseAPI) => {
        console.log("Nuevo carro creado", response);
        this.setActiveCart(response);
        this.isAddingToCart = false;
      },
      (error: any) => {
        console.log("Error creando nuevo carro:", error);
        this.isAddingToCart = false;
      }
    );
  }

  private addProductToExistingCart(carroId: number, productoId: number, cantidad: number): void {
    this.productoService.addItemToCart(carroId, productoId, cantidad).subscribe(
      (response: CarroItemAPI) => {
        console.log("Producto agregado al carro", response);
        this.loadCartItems(carroId);
        this.isAddingToCart = false;
      },
      (error: any) => {
        console.log("Error agregando producto al carro:", error);
        this.isAddingToCart = false;
      }
    );
  }

  private updateProductQuantity(carroId: number, productoId: number, cantidad: number): void {
    this.productoService.updateItemQuantity(carroId, productoId, cantidad).subscribe(
      (response: CarroItemAPI) => {
        console.log("Cantidad actualizada", response);
        this.loadCartItems(carroId);
        this.isAddingToCart = false;
      },
      (error: any) => {
        console.log("Error actualizando cantidad:", error);
        this.isAddingToCart = false;
      }
    );
  }

  getCarro(usuarioId: string): void {
    console.log("Usuario "+this.usuarioId+" "+usuarioId);

    this.productoService.getCarro(usuarioId).subscribe(
      (carroItems: CarroAPI[]) => {
        console.log("Carro items:", carroItems);
        this.carroItems = carroItems;
        this.carro_items = carroItems.length;
      },
      error => {
        if (error && error.message && error.message.includes('No authentication token available')) {
          this.login();
        } else {
          console.log("Error getting cart: ", error);
        }
      }
    );
  }

  isProductInCart(productoId: number): boolean {
    return this.cartItems.some(item => item.productoId === productoId);
  }

  getProductQuantityInCart(productoId: number): number {
    const item = this.cartItems.find(item => item.productoId === productoId);
    return item ? item.cantidad : 0;
  }

  increaseQuantity(productoId: number): void {
    if (!this.currentCart || this.isAddingToCart) return;

    const currentQuantity = this.getProductQuantityInCart(productoId);
    if (currentQuantity > 0) {
      this.isAddingToCart = true;
      this.updateProductQuantity(this.currentCart.carritoId, productoId, currentQuantity + 1);
    }
  }

  decreaseQuantity(productoId: number): void {
    if (!this.currentCart || this.isAddingToCart) return;

    const currentQuantity = this.getProductQuantityInCart(productoId);
    if (currentQuantity > 1) {
      // Decrease quantity by 1
      this.isAddingToCart = true;
      this.updateProductQuantity(this.currentCart.carritoId, productoId, currentQuantity - 1);
    } else if (currentQuantity === 1) {
      // Remove item completely from cart
      this.removeFromCart(productoId);
    }
  }

  removeFromCart(productoId: number): void {
    if (!this.currentCart || this.isAddingToCart) return;

    this.isAddingToCart = true;
    this.productoService.removeItemFromCart(this.currentCart.carritoId, productoId).subscribe(
      () => {
        console.log("Item eliminado del carro");
        this.loadCartItems(this.currentCart!.carritoId);
        this.isAddingToCart = false;
      },
      (error: any) => {
        console.log("Error eliminando item del carro:", error);
        this.isAddingToCart = false;
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

  getProductImage(producto: ProductoAPI): string {
    if (producto.imagenUri) {
      return producto.imagenUri;
    }
    // Fallback to local images based on product ID
    return `producto-img/producto_${producto.productoId}.jpeg`;
  }

  isProductInStock(producto: ProductoAPI): boolean {
    return producto.cantidadStock > 0;
  }

  onImageError(event: Event, productoId: number): void {
    const img = event.target as HTMLImageElement;
    img.src = `producto-img/producto_${productoId}.jpeg`;
  }

  login(): void {
    this.msalService.loginPopup(loginRequest).subscribe((result: AuthenticationResult) => {
      if (result && result.account) {
        console.log(result);
        this.msalService.instance.setActiveAccount(result.account);
        this.setUserFromAccount(result.account);
        this.router.navigate(["/principal"]);
        localStorage.setItem("token", result.idToken);
        localStorage.setItem("username", result.account.username);
      }
    });
  }

  logout(): void {
    this.msalService.logoutPopup().subscribe(() => {
      this.setUserFromAccount(undefined);
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      this.resetCartState();
    });
  }

  goCarro(): void {
    const token = localStorage.getItem("token");
    if (!token) {
      console.log("No authentication token found, redirecting to login");
      this.login();
      return;
    }

    console.log("Navegando a carro")
    this.router.navigate(['/carro']);
  }
}
