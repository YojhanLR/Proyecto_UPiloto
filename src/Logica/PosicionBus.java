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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yojhan
 */
public class PosicionBus extends Thread {

    private final VentanaPrincipal vp;
    private ArrayList<RecorridoBus> recorridos = new ArrayList<>();
    
    public PosicionBus(VentanaPrincipal vp) {
        this.vp = vp;
        obtenerRecorridos();
    }
    
    private void obtenerRecorridos(){
        try {
            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

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
                conexion.getConexion().close();
            }
        } catch (SQLException e) {
            System.out.println("Error: "+e);

        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
    }

    @Override
    public void run() {
        System.out.println("Inicio ubicación Buses.");
        while (true) {
            try {
                Thread.sleep(2000); //Tiempo que se actualizará la posición de los buses
                System.out.println("Inicio ciclo");
                System.out.println("reccoridos"+recorridos.size());
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
}
