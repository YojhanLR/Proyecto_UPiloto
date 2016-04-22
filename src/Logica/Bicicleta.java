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
public class Bicicleta {

    public void guardarBici(String a, String b) {

        try {
            ConectarBD conexion = new ConectarBD();

            int id = Integer.parseInt(a);
            String estado = b;

            String instruccion = "insert into BICICLETA values (?,?)";
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);
            conexion.sentencia.setInt(1, id);
            conexion.sentencia.setString(2, estado);

            conexion.sentencia.execute();
            conexion.getConexion().close();

            JOptionPane.showMessageDialog(null, "Registro Insertado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException j) {

            JOptionPane.showMessageDialog(null, "Error:" + j, "Información",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    public void actualizarBici(String a, String b) {

        try {

            ConectarBD conexion = new ConectarBD();
            int id = Integer.parseInt(a);
            String estado = b;

            String instruccion = "Update BICICLETA set ESTADO=?"
                    + "where BICICLETA_ID=" + id;
            conexion.sentencia = conexion.getConexion().prepareStatement(instruccion);

            conexion.sentencia.setString(1, estado);

            conexion.sentencia.execute();
            conexion.getConexion().close();

            JOptionPane.showMessageDialog(null, "Registro Actualizado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception j) {

            JOptionPane.showMessageDialog(null, "Error:" + j, "Información",
                    JOptionPane.WARNING_MESSAGE);

        }

    }

    public void eliminarBici(String a) {

        try {
            ConectarBD conexion = new ConectarBD();
            int id = Integer.parseInt(a);

            String instruccion = "Delete from BICICLETA "
                    + "where BICICLETA_ID='" + id + "'";
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

}
