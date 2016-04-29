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
 * @author HECTOR
 */
public class Conductor {
    
    private int id;
    private long cedula;
    private String nombre;
    private int edad;
    private long telefono;
    private String dir;
    private String cont;
    private String estado;
    
    
    public Conductor(){
    }
    
    public Conductor(int id, long cedula, String nombre, int edad, long telefono, String dir, String cont, String estado){
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.edad = edad;
        this.telefono = telefono;
        this.dir = dir;
        this.cont = cont;
        this.estado = estado;
    }
    
    public void guardarCond(String a, String b, String c, String d, String e, String f, String g, String h) {

        try {
            ConectarBD conexion = new ConectarBD();

            int id = Integer.parseInt(a);
            String cedula = b;
            String nombre = c;
            String edad = d;
            String telefono = e;
            String direccion = f;
            String contraseña = g;
            String estado = h;

            String instruccion = "insert into CONDUCTOR values (?,?,?,?,?,?,?,?)";
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);
            conexion.sentencia.setInt(1, id);
            conexion.sentencia.setString(2, cedula);
            conexion.sentencia.setString(3, nombre);
            conexion.sentencia.setString(4, edad);
            conexion.sentencia.setString(5, telefono);
            conexion.sentencia.setString(6, direccion);
            conexion.sentencia.setString(7, contraseña);
            conexion.sentencia.setString(8, estado);

            conexion.sentencia.execute();
            conexion.getConexion().close();

            JOptionPane.showMessageDialog(null, "Registro Insertado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException j) {

            JOptionPane.showMessageDialog(null, "Error:" + j, "Información",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    public void actualizarCond(String a, String b, String c, String d, String e, String f, String g, String h) {

        try {

            ConectarBD conexion = new ConectarBD();
            int id = Integer.parseInt(a);
            String cedula = b;
            String nombre = c;
            String edad = d;
            String telefono = e;
            String direccion = f;
            String contraseña = g;
            String estado = h;

            String instruccion = "Update CONDUCTOR set CEDULA=?,NOMBRE=?,EDAD=?,TELEFONO=?,DIRECCION=?,CONTRASEÑA=?,ESTADO=?"
                    + "where CONDUCTOR_ID=" + id;
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);

            conexion.sentencia.setString(1, cedula);
            conexion.sentencia.setString(2, nombre);
            conexion.sentencia.setString(3, edad);
            conexion.sentencia.setString(4, telefono);
            conexion.sentencia.setString(5, direccion);
            conexion.sentencia.setString(6, contraseña);
            conexion.sentencia.setString(7, estado);

            conexion.sentencia.execute();
            conexion.getConexion().close();

            JOptionPane.showMessageDialog(null, "Registro Actualizado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception j) {

            JOptionPane.showMessageDialog(null, "Error:" + j, "Información",
                    JOptionPane.WARNING_MESSAGE);

        }

    }

    public void eliminarCond(String a) {

        try {
            ConectarBD conexion = new ConectarBD();
            int id = Integer.parseInt(a);

            /*String instruccion2= "Delete from MUNICIPIO_PARTIDO "
             + "where PARTIDO_ID='" +id+"'";
             conexion.sentencia=conexion.getConexion().prepareStatement(instruccion2);
             conexion.sentencia.execute(); */
            String instruccion = "Delete from CONDUCTOR "
                    + "where CONDUCTOR_ID='" + id + "'";
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);
            conexion.sentencia.execute();

            conexion.getConexion().close();
            JOptionPane.showMessageDialog(null, "REGISTRO ELIMINADO", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null, "Error:" + e, "Información",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    public int getId() {
        return id;
    }

    public long getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public long getTelefono() {
        return telefono;
    }

    public String getDir() {
        return dir;
    }

    public String getCont() {
        return cont;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    

    
}
