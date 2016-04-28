/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Yojhan
 */
public class Bus {

    private int id;
    private String estado;
    private String matricula;
    private String ubicacion;
    
    public Bus(){
    }
    
    public Bus(int id, String estado, String matricula, String ubicacion){
         this.id = id;
         this.estado = estado;
         this.matricula = matricula;
         this.ubicacion = ubicacion;
    }
    
    public void guardarBus(int id, String estado, String matricula, String ubicacion) {

        try {
            ConectarBD conexion = new ConectarBD();


            String instruccion = "insert into BUS values (?,?,?,?)";
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);
            conexion.sentencia.setInt(1, id);
            conexion.sentencia.setString(2, estado);
            conexion.sentencia.setString(3, matricula);
            conexion.sentencia.setString(4, ubicacion);

            conexion.sentencia.execute();
            conexion.getConexion().close();

            JOptionPane.showMessageDialog(null, "Registro Insertado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException j) {

            JOptionPane.showMessageDialog(null, "Error:" + j, "Información",
                    JOptionPane.WARNING_MESSAGE);
        }

    }
    
    
    public void actualizarBus(int id, String estado, String matricula, String ubicacion) {

        try {

            ConectarBD conexion = new ConectarBD();

            String instruccion = "Update BUS set ESTADO=?,MATRICULA=?,UBICACION=?"
                    + "where BUS_ID=" + id;
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);

            conexion.sentencia.setString(1, estado);
            conexion.sentencia.setString(2, matricula);
            conexion.sentencia.setString(3, ubicacion);

            conexion.sentencia.execute();
            conexion.getConexion().close();

            JOptionPane.showMessageDialog(null, "Registro Actualizado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException j) {

            JOptionPane.showMessageDialog(null, "Error:" + j, "Información",
                    JOptionPane.WARNING_MESSAGE);

        }

    }


    public void eliminarBus(int id) {

        try {
            ConectarBD conexion = new ConectarBD();

            String instruccion = "Delete from BUS "
                    + "where BUS_ID='" + id + "'";
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);
            conexion.sentencia.execute();

            conexion.getConexion().close();
            JOptionPane.showMessageDialog(null, "Registro Eliminado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error:" + e, "Información",
                    JOptionPane.WARNING_MESSAGE);
        }

    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
