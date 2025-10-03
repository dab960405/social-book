import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment'; // importar environment

@Injectable({
  providedIn: 'root',
})
export class ApiConfiguration {
  rootUrl: string = environment.apiUrl;  // ahora usa environment
}