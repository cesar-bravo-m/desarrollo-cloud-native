import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, from, of } from 'rxjs';
import { MsalService } from '@azure/msal-angular';
import { switchMap, catchError } from 'rxjs/operators';
import { ProductoAPI, CarroAPI, CarroCreateAPI, CarroResponseAPI, CarroItemAPI } from '../../../types';

@Injectable({
  providedIn: 'root'
})

export class ProductoService {

  private url = "http://localhost:8080";

  constructor(
    private http: HttpClient,
    private msalService: MsalService
  ) { }

  private getHeaders(): Observable<HttpHeaders> {
    return from(this.msalService.initialize()).pipe(
      switchMap(() => {
        const token = localStorage.getItem("token");
        if (!token) {
          console.error('No authentication token available');
          throw new Error('No authentication token available');
        }
        return of(new HttpHeaders({
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }));
      }),
      catchError(error => {
        console.error('Error getting auth headers:', error);
        throw error;
      })
    );
  }

  getProducto(): Observable<ProductoAPI[]> {
    console.log("Accediendo endpoint: "+this.url);
    return this.getHeaders().pipe(
      switchMap(headers => this.http.get<ProductoAPI[]>(this.url+"/producto", { headers }))
    );
  }

  getProductoId(productoId: number): Observable<ProductoAPI> {
    console.log("Accediendo endpoint: "+this.url+"/producto/"+productoId);
    return this.getHeaders().pipe(
      switchMap(
        headers =>
          this.http.get<ProductoAPI>(this.url+"/producto/"+productoId, { headers })
      )
    );
  }

  getCarro(usuarioId: string): Observable<CarroAPI[]> {
    console.log("recuperando carro: "+this.url+"\n"+usuarioId);
    var uri = this.url+"/carritos/usuario/"+usuarioId;
    console.log("Uri "+uri);
    return this.getHeaders().pipe(
      switchMap(
        headers => this.http.get<CarroAPI[]>(uri, { headers }))
    );
  }

  // Get cart by ID
  getCartById(cartId: number): Observable<CarroResponseAPI> {
    console.log(`Obteniendo carro por ID: ${cartId}`);
    const uri = `${this.url}/carritos/${cartId}`;
    return this.getHeaders().pipe(
      switchMap(headers => this.http.get<CarroResponseAPI>(uri, { headers }))
    );
  }

  // Get active cart for user (returns the first active cart)
  getActiveCart(usuarioId: string): Observable<CarroAPI | null> {
    return this.getCarro(usuarioId).pipe(
      switchMap(carts => {
        const activeCarts = carts.filter(cart => cart.vigenciaFlag === 1);
        return of(activeCarts.length > 0 ? activeCarts[0] : null);
      })
    );
  }

  // Create new empty cart
  createEmptyCart(usuarioId: string): Observable<CarroResponseAPI> {
    const currentDate = new Date().toISOString();
    const carroData: CarroCreateAPI = {
      usuarioId: usuarioId,
      estado: "A",
      creadoEn: currentDate,
      actualizadoEn: null
    };

    console.log("Creando nuevo carro vacío:", carroData);
    return this.getHeaders().pipe(
      switchMap(headers => {
        console.log("Sending POST request to:", `${this.url}/carritos`);
        return this.http.post<CarroResponseAPI>(`${this.url}/carritos`, carroData, { headers });
      })
    );
  }

  // Create cart with product (two-step process: create cart, then add product)
  createCartWithProduct(usuarioId: string, productoId: number, cantidad: number): Observable<CarroResponseAPI> {
    return this.createEmptyCart(usuarioId).pipe(
      switchMap((cart: CarroResponseAPI) => {
        console.log("Cart created successfully:", cart);
        // Now add the product to the newly created cart
        return this.addItemToCart(cart.carritoId, productoId, cantidad).pipe(
          switchMap(() => {
            // Return the cart info, not the item info
            return of(cart);
          })
        );
      })
    );
  }

  // Add item to existing cart
  addItemToCart(carroId: number, productoId: number, cantidad: number): Observable<CarroItemAPI> {
    console.log(`Agregando item al carro ${carroId}: producto ${productoId}, cantidad ${cantidad}`);
    const uri = `${this.url}/carritos/${carroId}/items?productoId=${productoId}&cantidad=${cantidad}`;
    return this.getHeaders().pipe(
      switchMap(headers => this.http.post<CarroItemAPI>(uri, null, { headers }))
    );
  }

  // Update item quantity in cart
  updateItemQuantity(carroId: number, productoId: number, cantidad: number): Observable<CarroItemAPI> {
    console.log(`Actualizando cantidad del item en carro ${carroId}: producto ${productoId}, nueva cantidad ${cantidad}`);
    const uri = `${this.url}/carritos/${carroId}/items/${productoId}?cantidad=${cantidad}`;
    return this.getHeaders().pipe(
      switchMap(headers => this.http.put<CarroItemAPI>(uri, null, { headers }))
    );
  }

  // Get cart items for a specific cart
  getCartItems(carroId: number): Observable<CarroItemAPI[]> {
    console.log(`Obteniendo items del carro ${carroId}`);
    const uri = `${this.url}/carritos/${carroId}/items`;
    return this.getHeaders().pipe(
      switchMap(headers => this.http.get<CarroItemAPI[]>(uri, { headers }))
    );
  }

  // Remove item from cart
  removeItemFromCart(carroId: number, productoId: number): Observable<void> {
    console.log(`Eliminando item del carro ${carroId}: producto ${productoId}`);
    const uri = `${this.url}/carritos/${carroId}/items/${productoId}`;
    return this.getHeaders().pipe(
      switchMap(headers => this.http.delete<void>(uri, { headers }))
    );
  }

  // Legacy method - kept for backwards compatibility
  setCarro(carro: CarroCreateAPI): Observable<CarroAPI> {
    console.log("guardando seleccion: "+this.url+"\n"+carro);
    return this.getHeaders().pipe(
      switchMap(headers => this.http.post<CarroAPI>(this.url+"/carritos", carro, { headers }))
    );
  }

  unsetCarro(carroId: number): Observable<any> {
    return this.getHeaders().pipe(
      switchMap(headers => this.http.delete(this.url+"/carritos/"+carroId, { headers }))
    );
  }

  getTicketId(): Observable<any> {
    return this.getHeaders().pipe(
      switchMap(headers => this.http.get(this.url+"/ticket/gen", { headers }))
    );
  }

  getHtmlContent(url: string): Observable<string> {
    return this.http.get(url, { responseType: 'text' });
  }
}
