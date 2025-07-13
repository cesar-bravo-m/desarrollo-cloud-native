import { formatDate, CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { MsalService } from '@azure/msal-angular';
import { EventMessage, EventType, AccountInfo, AuthenticationResult } from '@azure/msal-browser';
import { loginRequest } from '../../auth-config';

import { ProductoService } from '../../services/producto.service';
import { ProductoAPI, CarroAPI, CarroCreateAPI } from '../../../../types';

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
  usuarioId: number = 0;
  carro_items: number = 0;
  isLoading: boolean = false;
  userEmail?: string;

  constructor(
    private router: Router,
    private productoService: ProductoService,
    private msalService: MsalService,
  ) {
    this.getProductos();
    this.usuarioId = JSON.parse(localStorage.getItem("usuarioId") || "0");
    this.getCarro(localStorage.getItem("username") || "");
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

    console.log("Agregando producto "+productoId);

    const carroData: CarroCreateAPI = {
      usuarioId: username,
      productoId: productoId,
      cantidad: 1
    };

    this.productoService.setCarro(carroData).subscribe(
      (response: CarroAPI) => {
        console.log("Producto agregado", response);
        this.getCarro(username);
      },
      error => {
        console.log("Se ha producido un problema al intentar agregar producto:", error);
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

  formatPrice(valorVenta: number): string {
    return (valorVenta / 100).toFixed(2);
  }

  getProductImage(producto: ProductoAPI): string {
    if (producto.imagenUri) {
      return producto.imagenUri;
    }
    // Fallback to local images based on product ID
    return `producto-img/producto_${producto.productoId}.jpeg`;
  }

  isProductInStock(producto: ProductoAPI): boolean {
    return producto.stockActual > 0;
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
