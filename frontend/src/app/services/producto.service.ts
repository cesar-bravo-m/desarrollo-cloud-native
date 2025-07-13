import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, from, of } from 'rxjs';
import { MsalService } from '@azure/msal-angular';
import { switchMap, catchError } from 'rxjs/operators';
import { ProductoAPI, CarroAPI, CarroCreateAPI } from '../../../types';

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
