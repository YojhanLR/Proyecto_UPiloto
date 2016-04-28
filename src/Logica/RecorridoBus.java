/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

/**
 *
 * @author Yojhan
 */
public class RecorridoBus {
    
    int bus_id;
    int conductor_id;
    int ruta_id;
    long kilometros_recorridos;
    
    RecorridoBus(int bus_id, int conductor_id, int ruta_id, long kilometros_recorridos){
        this.bus_id = bus_id;
        this.conductor_id = conductor_id;
        this.ruta_id = ruta_id;
        this.kilometros_recorridos = kilometros_recorridos;
    }
    
}
