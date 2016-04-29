/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import Presentacion.VentanaPrincipal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yojhan
 */
public class PosicionBus extends Thread {

    private final VentanaPrincipal vp;
    private final int num = 2; //Numero de buses en recorrido por troncal.
    private ArrayList<RecorridoBus> recorridos = new ArrayList<>();
    ConectarBD conexion = new ConectarBD();
    Statement sentencia;
    
    public PosicionBus(VentanaPrincipal vp) {
        this.vp = vp;
        obtenerRecorridos();
    }
    
    private void obtenerRecorridos(){
        try {
            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from BUS_CONDUCTOR where FECHA_FIN is NULL");
            
            if (!resultado.isBeforeFirst()) {
                System.out.println("No hay recorridos pendientes.");
            }
            else{

                while (resultado.next()) {
                    int bus_id = resultado.getInt("RUTA_ID");
                    int conductor_id = resultado.getInt("CONDUCTOR_ID");
                    int ruta_id = resultado.getInt("RUTA_ID");
                    long kilometros = resultado.getLong("KILOMETROS_RECORRIDOS");


                    RecorridoBus temp = new RecorridoBus(bus_id,conductor_id,ruta_id,kilometros);
                    recorridos.add(temp);
                }

                resultado.close();
                //conexion.getConexion().close();
            }
        } catch (SQLException e) {
            System.out.println("Error: "+e);

        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
    }

    @Override
    public void run() {
        System.out.println(">>>> Inicio ubicación Buses. <<<");
        while (true) {
            try {
                Thread.sleep(2000); //Tiempo que se actualizará la posición de los buses
                System.out.println("---> Inicio ciclo");
                
                if(verificarNumRecorridos(num)){
                    System.out.println("Recorridos insuficientes");
                    agregarRecorridos();
                }
                else{
                    System.out.println("Recorridos suficientes");
                    System.out.println(recorridos.size());
                }
                
                
                if(vp.vRuta != null && vp.vRuta.isVisible()){
                    System.out.println("Esta abierta!");
                }
                else{
                    System.out.println("No lo esta");
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(PosicionBus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean verificarNumRecorridos(int num){
        /*Verifica que el número de buses deseados en estado "recorrido" sea el correcto*/
        /*Por ejemplo se desea que minimo sean 4 buses que esten en recorrido; num=4*/
        
        boolean flag;
        flag = recorridos.size() != (num*3); //3 es el número de rutas (A,B,C).
        /*False: No se necesita agregar más recorridos*/
        /*Verdad: Se necesita agregar más recorridos*/
        return flag;
    }
    
    private void agregarRecorridos(){
        
        int rutaA = 0;
        int rutaB = 0;
        int rutaC = 0;
        
        
        if(recorridos.size() > 0){ //Agrega a las variables auxiliares el número de buses que ya estan en recorridos previos.
            for(RecorridoBus temp : recorridos){
                switch(temp.ruta_id){
                    case 1:
                        rutaA++;
                        break;
                    case 2:
                        rutaB++;
                        break;
                    case 3:
                        rutaC++; 
                        break;
                }
            }
        }
        
            //Ruta A => id:1
            agregarEnRuta(1,rutaA);
            //Ruta B => id:2
            agregarEnRuta(2,rutaB);
            //Ruta C => id:3
            agregarEnRuta(3,rutaC);
            
            //Falta en la BD
        
    }
    
    public void agregarEnRuta(int id, int numbuses){
        
        System.out.println("Ruta ID: "+id);
        
        while(numbuses < num){
            boolean bandera = true;
            int indexConductor = -1;
            int indexBus = -1;
            
            while(bandera){ //Obtiene un bus aleatorio que este libre
                indexBus = (int)(Math.random() * vp.buses.size());
                System.out.println("El número aleatorio Bus es: "+indexBus);
                if(vp.buses.get(indexBus).getEstado().equals("Libre")){
                    bandera = false;
                }
            }
            
            bandera = true;
            
            while(bandera){ //Obtiene un conductor aleatorio que este libre
                indexConductor = (int)(Math.random() * vp.conductores.size());
                System.out.println("El número aleatorio Conductor es: "+indexConductor);
                if(vp.conductores.get(indexConductor).getEstado().equals("Libre")){
                    bandera = false;
                }
            }
            
            Bus bus = vp.buses.get(indexBus);
            bus.setEstado("En recorrido");
            Conductor conductor = vp.conductores.get(indexConductor);
            conductor.setEstado("En recorrido");
            
            RecorridoBus temp = new RecorridoBus(bus.getId(), conductor.getId(), id, 0);
            recorridos.add(temp);
            numbuses++;
        }
    }
}
