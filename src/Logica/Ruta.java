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
public class Ruta {
    
    private int id;
    private float kilometros;
    private String nombre;
    
    public Ruta(int id, float kilometros, String nombre){
        this.id = id;
        this.kilometros = kilometros;
        this.nombre = nombre;
    }
    
    public int getId() {
        return id;
    }

    public float getKilometros() {
        return kilometros;
    }

    public String getNombre() {
        return nombre;
    }
}
