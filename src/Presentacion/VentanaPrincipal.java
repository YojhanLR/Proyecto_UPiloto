/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. HEX COLOR 0a7e00
 */
package Presentacion;

import Logica.Bicicleta;
import Logica.Bus;
import Logica.Conductor;
import Logica.ConectarBD;
import Logica.PosicionBus;
import Logica.Ruta;
import Logica.Transfer;
import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Yojhan
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    Conductor objCond = new Conductor();
    Bicicleta objBici = new Bicicleta();
    Bus objBus = new Bus();
    Transfer objTra = new Transfer();
    PosicionBus posBus;
    public VentanaRuta vRuta;

    public ArrayList<Bus> buses = new ArrayList<>();
    public ArrayList<Ruta> rutas = new ArrayList<>();
    public ArrayList<Conductor> conductores = new ArrayList<>();

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
        this.setLocationRelativeTo(null); //Ubica ventana en la mitad de la pantalla
        initThreads();

        try {
            servidor = new DatagramSocket(5000);
        } catch (SocketException errorSocket) {
            JOptionPane.showMessageDialog(null, "Error en el Socket" + errorSocket,
                    "Información", JOptionPane.PLAIN_MESSAGE);
            System.exit(1);
        }
    }

    private void initThreads() {
        //Por recomendaciones un Hilo no se debe iniciar en un constructor.
        obtenerRutas();
        actualizarListaBuses();
        actualizarListaConductores();

        posBus = new PosicionBus(this);
        posBus.start();

    }

    public void actualizarListaBuses() {
        try {
            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from BUS");

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                int id = resultado.getInt("BUS_ID");
                String estado = resultado.getString("ESTADO");
                String matricula = resultado.getString("MATRICULA");
                String ubicacion = resultado.getString("UBICACION");

                Bus temp = new Bus(id, estado, matricula, ubicacion);
                buses.add(temp);
            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarListaConductores() {
        try {
            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from CONDUCTOR");

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                int id = resultado.getInt("CONDUCTOR_ID");
                long cedula = resultado.getLong("CEDULA");
                String nombre = resultado.getString("NOMBRE");
                int edad = resultado.getInt("EDAD");
                long telefono = resultado.getLong("TELEFONO");
                String direccion = resultado.getString("DIRECCION");
                String cont = resultado.getString("CONTRASEÑA");
                String estado = resultado.getString("ESTADO");

                Conductor temp = new Conductor(id, cedula, nombre, edad, telefono, direccion, cont, estado);
                conductores.add(temp);
            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void obtenerRutas() {
        try {
            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from RUTA");

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                int id = resultado.getInt("RUTA_ID");
                float kilometros = resultado.getFloat("KILOMETROS");
                String nombre = resultado.getString("NOMBRE");

                Ruta temp = new Ruta(id, kilometros, nombre);
                rutas.add(temp);
            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // SOCKETS 
    private DatagramSocket servidor;
    String Consulta, Consulta2;

    public void esperarPaquetes() {
        while (true) {
            try {
                byte datos1[] = new byte[10000];
                DatagramPacket recibirPaquete1 = new DatagramPacket(datos1, datos1.length);
                servidor.receive(recibirPaquete1);
                Consulta = new String(recibirPaquete1.getData(), 0, recibirPaquete1.getLength());

                byte datos2[] = new byte[10000];
                DatagramPacket recibirPaquete2 = new DatagramPacket(datos2, datos2.length);
                servidor.receive(recibirPaquete2);
                Consulta2 = new String(recibirPaquete2.getData(), 0, recibirPaquete2.getLength());
                System.out.println(Consulta2);

                byte datos3[] = new byte[1000];
                DatagramPacket recibirPaquete3 = new DatagramPacket(datos3, datos3.length);
                servidor.receive(recibirPaquete3);

                enviarPaqueteACliente(recibirPaquete3);

            } catch (IOException errorio) {
                System.out.print(errorio.toString() + "\n");
            }
        }
    }

    private void enviarPaqueteACliente(DatagramPacket recibirPaquete3) throws IOException {

        try {

            String Bandera = new String(recibirPaquete3.getData(), 0, recibirPaquete3.getLength());

            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            sentencia.executeQuery(Consulta);
            sentencia.clearBatch();

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado2 = sentencia.executeQuery(Consulta2);
            sentencia.clearBatch();

            conexion.getConexion().close();

            String mensaje = "  ";
            System.out.printf(mensaje);

            byte datos[] = mensaje.getBytes();

            DatagramPacket enviarPaquete = new DatagramPacket(datos, datos.length, 5000);
            servidor.send(enviarPaquete);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }

        System.out.print("Paquete enviado\n");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog_Bici = new javax.swing.JDialog();
        btn_verBicis = new javax.swing.JButton();
        jDialog_Conductores = new javax.swing.JDialog();
        btn_verConductores = new javax.swing.JButton();
        btn_OpcionesConductor = new javax.swing.JButton();
        jDialog_Buses = new javax.swing.JDialog();
        btn_verBuses = new javax.swing.JButton();
        btn_verRutaA = new javax.swing.JButton();
        btn_verRutaB = new javax.swing.JButton();
        btn_verRutaC = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jDialog_Transfers = new javax.swing.JDialog();
        btn_verTransfers = new javax.swing.JButton();
        jDialog_Clientes = new javax.swing.JDialog();
        btn_verClientes = new javax.swing.JButton();
        JDOpcionesConductores = new javax.swing.JDialog();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnRegistrarConductor = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnEditarConductor = new javax.swing.JButton();
        btnEliminarConductor = new javax.swing.JButton();
        btnAtrasConductor = new javax.swing.JButton();
        txtIdConductor = new javax.swing.JTextField();
        txtCedulaConductor = new javax.swing.JTextField();
        txtNombreConductor = new javax.swing.JTextField();
        txtEdadConductor = new javax.swing.JTextField();
        txtTelefonoConductor = new javax.swing.JTextField();
        txtDireccionConductor = new javax.swing.JTextField();
        txtContrasenaConductor = new javax.swing.JTextField();
        btnBuscarConductor = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        txtEstadoConductor = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        JDListadoConductores = new javax.swing.JDialog();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ListaDeConductores = new javax.swing.JTable();
        btnSalirListadoDeConductores = new javax.swing.JButton();
        JDListadoClientes = new javax.swing.JDialog();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ListaDeClientes = new javax.swing.JTable();
        btnSalirListadoDeClientes = new javax.swing.JButton();
        JDListadoBicicletas = new javax.swing.JDialog();
        jLabel25 = new javax.swing.JLabel();
        btnVerBicicletas = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        ListaBicicletas = new javax.swing.JTable();
        btnRegistrarBicicleta = new javax.swing.JButton();
        btnEditarBicicleta = new javax.swing.JButton();
        btnEliminarBicicleta = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cmbEstadoBicicleta = new javax.swing.JComboBox();
        txtIdBicicleta = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        cmbNombreEstacionBicicletas = new javax.swing.JComboBox();
        jLabel38 = new javax.swing.JLabel();
        btnCargarBicicletas = new javax.swing.JLabel();
        btn_volverAtrasBici = new javax.swing.JButton();
        JDListadoTransfers = new javax.swing.JDialog();
        jLabel48 = new javax.swing.JLabel();
        btnVerTransfers = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        ListaTransfers = new javax.swing.JTable();
        btnRegistrarTransfer = new javax.swing.JButton();
        btnEditarTransfer = new javax.swing.JButton();
        btnEliminarTransfer = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        cmbEstadoTransfer = new javax.swing.JComboBox();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        txtMatriculaTransfer = new javax.swing.JTextField();
        txtIdTransfer = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        txtUbicacionTransfer = new javax.swing.JTextField();
        btnCargarTransfer = new javax.swing.JLabel();
        btn_volverAtrasTransfer = new javax.swing.JButton();
        JDListadoBuses = new javax.swing.JDialog();
        jLabel43 = new javax.swing.JLabel();
        btnVerBuses = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        ListaBuses = new javax.swing.JTable();
        btnRegistrarBus = new javax.swing.JButton();
        btnEditarBus = new javax.swing.JButton();
        btnEliminarBus = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        cmbEstadoBus = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txtMatriculaBus = new javax.swing.JTextField();
        txtIdBus = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        txtUbicacion = new javax.swing.JTextField();
        btnCargarBus = new javax.swing.JLabel();
        btn_volverAtrasBus = new javax.swing.JButton();
        panel_logo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        panel_datos = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        label_nombre = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        label_correo = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();
        panel_buses = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        panel_transfers = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        panel_biciagil = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panel_clientes = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        panel_conductores = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();

        jDialog_Bici.setTitle("Bici-ágil");
        jDialog_Bici.setMinimumSize(new java.awt.Dimension(300, 126));
        jDialog_Bici.setResizable(false);

        btn_verBicis.setText("Ver bicicletas");
        btn_verBicis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verBicisActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog_BiciLayout = new javax.swing.GroupLayout(jDialog_Bici.getContentPane());
        jDialog_Bici.getContentPane().setLayout(jDialog_BiciLayout);
        jDialog_BiciLayout.setHorizontalGroup(
            jDialog_BiciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_BiciLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_verBicis, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialog_BiciLayout.setVerticalGroup(
            jDialog_BiciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog_BiciLayout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(btn_verBicis, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        jDialog_Conductores.setTitle("Conductores");
        jDialog_Conductores.setIconImage(null);
        jDialog_Conductores.setMinimumSize(new java.awt.Dimension(300, 126));
        jDialog_Conductores.setResizable(false);

        btn_verConductores.setText("Ver lista de conductores");
        btn_verConductores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verConductoresActionPerformed(evt);
            }
        });

        btn_OpcionesConductor.setText("Opciones de conductor");
        btn_OpcionesConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_OpcionesConductorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog_ConductoresLayout = new javax.swing.GroupLayout(jDialog_Conductores.getContentPane());
        jDialog_Conductores.getContentPane().setLayout(jDialog_ConductoresLayout);
        jDialog_ConductoresLayout.setHorizontalGroup(
            jDialog_ConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_ConductoresLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog_ConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_verConductores, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(btn_OpcionesConductor, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                .addContainerGap())
        );
        jDialog_ConductoresLayout.setVerticalGroup(
            jDialog_ConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_ConductoresLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_verConductores, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_OpcionesConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDialog_Buses.setTitle("Buses");
        jDialog_Buses.setMinimumSize(new java.awt.Dimension(300, 156));
        jDialog_Buses.setResizable(false);

        btn_verBuses.setText("Ver lista de buses");
        btn_verBuses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verBusesActionPerformed(evt);
            }
        });

        btn_verRutaA.setText("Ruta A");
        btn_verRutaA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verRutaAActionPerformed(evt);
            }
        });

        btn_verRutaB.setText("Ruta B");
        btn_verRutaB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verRutaBActionPerformed(evt);
            }
        });

        btn_verRutaC.setText("Ruta C");
        btn_verRutaC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verRutaCActionPerformed(evt);
            }
        });

        jLabel21.setText("Ver buses en tiempo real");

        javax.swing.GroupLayout jDialog_BusesLayout = new javax.swing.GroupLayout(jDialog_Buses.getContentPane());
        jDialog_Buses.getContentPane().setLayout(jDialog_BusesLayout);
        jDialog_BusesLayout.setHorizontalGroup(
            jDialog_BusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_BusesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog_BusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_verBuses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jDialog_BusesLayout.createSequentialGroup()
                        .addComponent(btn_verRutaA, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addComponent(btn_verRutaB, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btn_verRutaC, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog_BusesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addGap(89, 89, 89))
        );
        jDialog_BusesLayout.setVerticalGroup(
            jDialog_BusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_BusesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_verBuses, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog_BusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_verRutaA, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_verRutaB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_verRutaC, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jDialog_Transfers.setTitle("Transfers");
        jDialog_Transfers.setMinimumSize(new java.awt.Dimension(300, 130));
        jDialog_Transfers.setResizable(false);

        btn_verTransfers.setText("Ver lista de transfers");
        btn_verTransfers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verTransfersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog_TransfersLayout = new javax.swing.GroupLayout(jDialog_Transfers.getContentPane());
        jDialog_Transfers.getContentPane().setLayout(jDialog_TransfersLayout);
        jDialog_TransfersLayout.setHorizontalGroup(
            jDialog_TransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_TransfersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_verTransfers, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialog_TransfersLayout.setVerticalGroup(
            jDialog_TransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog_TransfersLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(btn_verTransfers, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        jDialog_Clientes.setTitle("Clientes");
        jDialog_Clientes.setMinimumSize(new java.awt.Dimension(300, 83));
        jDialog_Clientes.setResizable(false);

        btn_verClientes.setText("Ver lista de clientes");
        btn_verClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_verClientesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog_ClientesLayout = new javax.swing.GroupLayout(jDialog_Clientes.getContentPane());
        jDialog_Clientes.getContentPane().setLayout(jDialog_ClientesLayout);
        jDialog_ClientesLayout.setHorizontalGroup(
            jDialog_ClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_ClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_verClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );
        jDialog_ClientesLayout.setVerticalGroup(
            jDialog_ClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_ClientesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_verClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JDOpcionesConductores.setTitle("Opciones de conductores");
        JDOpcionesConductores.setMinimumSize(new java.awt.Dimension(530, 580));
        JDOpcionesConductores.setResizable(false);

        jLabel8.setText("Opciones de conductores");

        jLabel9.setText("Id:");

        jLabel10.setText("Cédula:");

        btnRegistrarConductor.setText("Registrar");
        btnRegistrarConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarConductorActionPerformed(evt);
            }
        });

        jLabel16.setText("Nombre:");

        jLabel17.setText("Edad:");

        jLabel18.setText("Teléfono:");

        jLabel19.setText("Dirección:");

        jLabel20.setText("Contraseña:");

        btnEditarConductor.setText("Editar");
        btnEditarConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarConductorActionPerformed(evt);
            }
        });

        btnEliminarConductor.setText("Eliminar");
        btnEliminarConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarConductorActionPerformed(evt);
            }
        });

        btnAtrasConductor.setText("Atras");
        btnAtrasConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasConductorActionPerformed(evt);
            }
        });

        txtEdadConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEdadConductorActionPerformed(evt);
            }
        });

        btnBuscarConductor.setText("Buscar");
        btnBuscarConductor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarConductorActionPerformed(evt);
            }
        });

        jLabel22.setText("Estado:");

        javax.swing.GroupLayout JDOpcionesConductoresLayout = new javax.swing.GroupLayout(JDOpcionesConductores.getContentPane());
        JDOpcionesConductores.getContentPane().setLayout(JDOpcionesConductoresLayout);
        JDOpcionesConductoresLayout.setHorizontalGroup(
            JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDOpcionesConductoresLayout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, JDOpcionesConductoresLayout.createSequentialGroup()
                        .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel16))
                        .addGap(37, 37, 37)
                        .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(JDOpcionesConductoresLayout.createSequentialGroup()
                                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtIdConductor, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(txtCedulaConductor))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscarConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(JDOpcionesConductoresLayout.createSequentialGroup()
                        .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, JDOpcionesConductoresLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18))
                                .addGap(32, 32, 32)
                                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDireccionConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTelefonoConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEdadConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, JDOpcionesConductoresLayout.createSequentialGroup()
                                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel22))
                                .addGap(19, 19, 19)
                                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtContrasenaConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEstadoConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(161, 161, 161))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, JDOpcionesConductoresLayout.createSequentialGroup()
                        .addComponent(btnRegistrarConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnEditarConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminarConductor, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAtrasConductor)))
                .addContainerGap(41, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDOpcionesConductoresLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172))
        );
        JDOpcionesConductoresLayout.setVerticalGroup(
            JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDOpcionesConductoresLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtIdConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarConductor))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtCedulaConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtNombreConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtEdadConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtTelefonoConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtDireccionConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtContrasenaConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtEstadoConductor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(JDOpcionesConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrarConductor)
                    .addComponent(btnEditarConductor)
                    .addComponent(btnEliminarConductor)
                    .addComponent(btnAtrasConductor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(126, 126, 126))
        );

        JDListadoConductores.setTitle("Lista de conductores");
        JDListadoConductores.setMinimumSize(new java.awt.Dimension(900, 450));

        jLabel23.setText("Lista de conductores ");

        ListaDeConductores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(ListaDeConductores);

        btnSalirListadoDeConductores.setText("Volver atrás");
        btnSalirListadoDeConductores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirListadoDeConductoresActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JDListadoConductoresLayout = new javax.swing.GroupLayout(JDListadoConductores.getContentPane());
        JDListadoConductores.getContentPane().setLayout(JDListadoConductoresLayout);
        JDListadoConductoresLayout.setHorizontalGroup(
            JDListadoConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoConductoresLayout.createSequentialGroup()
                .addGap(0, 23, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoConductoresLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JDListadoConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoConductoresLayout.createSequentialGroup()
                        .addComponent(btnSalirListadoDeConductores, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(327, 327, 327))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoConductoresLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(358, 358, 358))))
        );
        JDListadoConductoresLayout.setVerticalGroup(
            JDListadoConductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoConductoresLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(btnSalirListadoDeConductores)
                .addGap(23, 23, 23))
        );

        JDListadoClientes.setTitle("Lista de clientes");
        JDListadoClientes.setMinimumSize(new java.awt.Dimension(900, 400));
        JDListadoClientes.setModal(true);

        jLabel24.setText("Lista de clientes");

        ListaDeClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(ListaDeClientes);

        btnSalirListadoDeClientes.setText("Volver atrás");
        btnSalirListadoDeClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirListadoDeClientesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JDListadoClientesLayout = new javax.swing.GroupLayout(JDListadoClientes.getContentPane());
        JDListadoClientes.getContentPane().setLayout(JDListadoClientesLayout);
        JDListadoClientesLayout.setHorizontalGroup(
            JDListadoClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoClientesLayout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoClientesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JDListadoClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoClientesLayout.createSequentialGroup()
                        .addComponent(btnSalirListadoDeClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(327, 327, 327))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoClientesLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(373, 373, 373))))
        );
        JDListadoClientesLayout.setVerticalGroup(
            JDListadoClientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoClientesLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalirListadoDeClientes)
                .addGap(23, 23, 23))
        );

        JDListadoBicicletas.setTitle("Opciones de bici-ágil");
        JDListadoBicicletas.setMinimumSize(new java.awt.Dimension(740, 530));
        JDListadoBicicletas.setResizable(false);

        jLabel25.setText("Opciones de bici-ágil");

        btnVerBicicletas.setText("Ver Bicicletas");
        btnVerBicicletas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerBicicletasActionPerformed(evt);
            }
        });

        ListaBicicletas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(ListaBicicletas);

        btnRegistrarBicicleta.setText("Registrar");
        btnRegistrarBicicleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarBicicletaActionPerformed(evt);
            }
        });

        btnEditarBicicleta.setText("Editar");
        btnEditarBicicleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarBicicletaActionPerformed(evt);
            }
        });

        btnEliminarBicicleta.setText("Eliminar");
        btnEliminarBicicleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarBicicletaActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbEstadoBicicleta.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Libre", "Reservada" }));
        cmbEstadoBicicleta.setToolTipText("");

        jLabel27.setText("Estado:");

        jLabel26.setText("Id:");

        jLabel38.setText("Estación:");

        btnCargarBicicletas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/owi-opensearch.png"))); // NOI18N
        btnCargarBicicletas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCargarBicicletasMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel26)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmbEstadoBicicleta, javax.swing.GroupLayout.Alignment.LEADING, 0, 115, Short.MAX_VALUE)
                    .addComponent(txtIdBicicleta, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbNombreEstacionBicicletas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCargarBicicletas)
                .addGap(5, 5, 5))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26)
                        .addComponent(txtIdBicicleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCargarBicicletas, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(cmbEstadoBicicleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbNombreEstacionBicicletas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        btn_volverAtrasBici.setText("Volver atrás");
        btn_volverAtrasBici.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_volverAtrasBiciActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JDListadoBicicletasLayout = new javax.swing.GroupLayout(JDListadoBicicletas.getContentPane());
        JDListadoBicicletas.getContentPane().setLayout(JDListadoBicicletasLayout);
        JDListadoBicicletasLayout.setHorizontalGroup(
            JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoBicicletasLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(JDListadoBicicletasLayout.createSequentialGroup()
                        .addComponent(btnRegistrarBicicleta, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(JDListadoBicicletasLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(btnEditarBicicleta, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(JDListadoBicicletasLayout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addComponent(btnEliminarBicicleta, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBicicletasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBicicletasLayout.createSequentialGroup()
                        .addComponent(btnVerBicicletas, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBicicletasLayout.createSequentialGroup()
                        .addComponent(btn_volverAtrasBici)
                        .addGap(138, 138, 138))))
            .addGroup(JDListadoBicicletasLayout.createSequentialGroup()
                .addGap(297, 297, 297)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JDListadoBicicletasLayout.setVerticalGroup(
            JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoBicicletasLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(btnVerBicicletas)
                .addGap(18, 18, 18)
                .addGroup(JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBicicletasLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(JDListadoBicicletasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegistrarBicicleta)
                            .addComponent(btnEditarBicicleta)
                            .addComponent(btnEliminarBicicleta)))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btn_volverAtrasBici)
                .addGap(28, 28, 28))
        );

        JDListadoTransfers.setTitle("Opciones de transfers");
        JDListadoTransfers.setMinimumSize(new java.awt.Dimension(740, 530));
        JDListadoTransfers.setResizable(false);

        jLabel48.setText("Opciones de Transfers");

        btnVerTransfers.setText("Ver Transfers");
        btnVerTransfers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerTransfersActionPerformed(evt);
            }
        });

        ListaTransfers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(ListaTransfers);

        btnRegistrarTransfer.setText("Registrar");
        btnRegistrarTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarTransferActionPerformed(evt);
            }
        });

        btnEditarTransfer.setText("Editar");
        btnEditarTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarTransferActionPerformed(evt);
            }
        });

        btnEliminarTransfer.setText("Eliminar");
        btnEliminarTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarTransferActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbEstadoTransfer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Libre", "Reservado" }));
        cmbEstadoTransfer.setToolTipText("");

        jLabel49.setText("Matricula: ");

        jLabel50.setText("Id:");

        jLabel51.setText("Estado:");

        jLabel52.setText("Ubicación");

        btnCargarTransfer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/owi-opensearch.png"))); // NOI18N
        btnCargarTransfer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCargarTransferMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel50)
                        .addGap(54, 54, 54))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addComponent(jLabel49)
                            .addComponent(jLabel52))
                        .addGap(18, 18, 18)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMatriculaTransfer, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdTransfer)
                    .addComponent(cmbEstadoTransfer, javax.swing.GroupLayout.Alignment.LEADING, 0, 114, Short.MAX_VALUE)
                    .addComponent(txtUbicacionTransfer))
                .addGap(18, 18, 18)
                .addComponent(btnCargarTransfer)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel50)
                        .addComponent(txtIdTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCargarTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(txtMatriculaTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(cmbEstadoTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(txtUbicacionTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        btn_volverAtrasTransfer.setText("Volver atrás");
        btn_volverAtrasTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_volverAtrasTransferActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JDListadoTransfersLayout = new javax.swing.GroupLayout(JDListadoTransfers.getContentPane());
        JDListadoTransfers.getContentPane().setLayout(JDListadoTransfersLayout);
        JDListadoTransfersLayout.setHorizontalGroup(
            JDListadoTransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoTransfersLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(JDListadoTransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JDListadoTransfersLayout.createSequentialGroup()
                        .addComponent(btnRegistrarTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditarTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnEliminarTransfer, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoTransfersLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnVerTransfers, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoTransfersLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_volverAtrasTransfer)
                .addGap(145, 145, 145))
            .addGroup(JDListadoTransfersLayout.createSequentialGroup()
                .addGap(302, 302, 302)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JDListadoTransfersLayout.setVerticalGroup(
            JDListadoTransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoTransfersLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(btnVerTransfers)
                .addGap(18, 18, 18)
                .addGroup(JDListadoTransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JDListadoTransfersLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(JDListadoTransfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegistrarTransfer)
                            .addComponent(btnEditarTransfer)
                            .addComponent(btnEliminarTransfer)))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_volverAtrasTransfer)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        JDListadoBuses.setTitle("Opciones de buses");
        JDListadoBuses.setMinimumSize(new java.awt.Dimension(740, 530));
        JDListadoBuses.setResizable(false);

        jLabel43.setText("Opciones de Buses");

        btnVerBuses.setText("Ver Buses");
        btnVerBuses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerBusesActionPerformed(evt);
            }
        });

        ListaBuses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(ListaBuses);

        btnRegistrarBus.setText("Registrar");
        btnRegistrarBus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarBusActionPerformed(evt);
            }
        });

        btnEditarBus.setText("Editar");
        btnEditarBus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarBusActionPerformed(evt);
            }
        });

        btnEliminarBus.setText("Eliminar");
        btnEliminarBus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarBusActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cmbEstadoBus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Libre", "En recorrido" }));
        cmbEstadoBus.setToolTipText("");

        jLabel47.setText("Matricula: ");

        jLabel44.setText("Id:");

        jLabel45.setText("Estado:");

        jLabel46.setText("Ubicación");

        btnCargarBus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/owi-opensearch.png"))); // NOI18N
        btnCargarBus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCargarBusMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel45)
                    .addComponent(jLabel46)
                    .addComponent(jLabel47)
                    .addComponent(jLabel44))
                .addGap(18, 49, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtMatriculaBus)
                    .addComponent(txtIdBus)
                    .addComponent(cmbEstadoBus, 0, 118, Short.MAX_VALUE)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCargarBus)
                .addGap(9, 9, 9))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel44)
                        .addComponent(txtIdBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnCargarBus, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMatriculaBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(cmbEstadoBus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addGap(19, 19, 19))
        );

        btn_volverAtrasBus.setText("Volver atrás");
        btn_volverAtrasBus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_volverAtrasBusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JDListadoBusesLayout = new javax.swing.GroupLayout(JDListadoBuses.getContentPane());
        JDListadoBuses.getContentPane().setLayout(JDListadoBusesLayout);
        JDListadoBusesLayout.setHorizontalGroup(
            JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBusesLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBusesLayout.createSequentialGroup()
                        .addComponent(btnVerBuses, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(95, 95, 95))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBusesLayout.createSequentialGroup()
                        .addGroup(JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(JDListadoBusesLayout.createSequentialGroup()
                                .addComponent(btnEditarBus, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnEliminarBus, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JDListadoBusesLayout.createSequentialGroup()
                        .addComponent(btn_volverAtrasBus)
                        .addGap(150, 150, 150))))
            .addGroup(JDListadoBusesLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnRegistrarBus, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        JDListadoBusesLayout.setVerticalGroup(
            JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JDListadoBusesLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(btnVerBuses)
                .addGap(18, 18, 18)
                .addGroup(JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JDListadoBusesLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addGroup(JDListadoBusesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegistrarBus)
                            .addComponent(btnEditarBus)
                            .addComponent(btnEliminarBus)))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btn_volverAtrasBus)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Home - Módulo Administrador");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        panel_logo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_logo.setAlignmentX(0.2F);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/logo.png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 153, 0));
        jLabel2.setText("Sistema de Transporte Público Integrado");

        javax.swing.GroupLayout panel_logoLayout = new javax.swing.GroupLayout(panel_logo);
        panel_logo.setLayout(panel_logoLayout);
        panel_logoLayout.setHorizontalGroup(
            panel_logoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_logoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
            .addGroup(panel_logoLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_logoLayout.setVerticalGroup(
            panel_logoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_logoLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Bienvenido(a):  ");

        label_nombre.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        label_nombre.setText("Nombre administrador ");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Mis datos");

        jLabel5.setText("Correo: ");

        jLabel6.setText("Nombre de usuario:");

        label_correo.setText("correo electronico");

        label_username.setText("nombre de usuario");

        javax.swing.GroupLayout panel_datosLayout = new javax.swing.GroupLayout(panel_datos);
        panel_datos.setLayout(panel_datosLayout);
        panel_datosLayout.setHorizontalGroup(
            panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_datosLayout.createSequentialGroup()
                .addGroup(panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_datosLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_nombre))
                    .addGroup(panel_datosLayout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_correo)
                            .addComponent(label_username)))
                    .addGroup(panel_datosLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel4)))
                .addContainerGap(89, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        panel_datosLayout.setVerticalGroup(
            panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_datosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(label_nombre))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(label_correo))
                .addGap(18, 18, 18)
                .addGroup(panel_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(label_username))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        panel_buses.setBackground(new java.awt.Color(234, 234, 234));
        panel_buses.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_buses.setMaximumSize(new java.awt.Dimension(111, 104));
        panel_buses.setMinimumSize(new java.awt.Dimension(111, 104));
        panel_buses.setPreferredSize(new java.awt.Dimension(111, 104));
        panel_buses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_busesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_busesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_busesMouseExited(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 102, 0));
        jLabel11.setText("Buses");

        jLabel39.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/map-icon-bus-station.png"))); // NOI18N

        javax.swing.GroupLayout panel_busesLayout = new javax.swing.GroupLayout(panel_buses);
        panel_buses.setLayout(panel_busesLayout);
        panel_busesLayout.setHorizontalGroup(
            panel_busesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_busesLayout.createSequentialGroup()
                .addGroup(panel_busesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_busesLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel11))
                    .addGroup(panel_busesLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel39)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        panel_busesLayout.setVerticalGroup(
            panel_busesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_busesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11))
        );

        panel_transfers.setBackground(new java.awt.Color(234, 234, 234));
        panel_transfers.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_transfers.setMaximumSize(new java.awt.Dimension(111, 104));
        panel_transfers.setMinimumSize(new java.awt.Dimension(111, 104));
        panel_transfers.setPreferredSize(new java.awt.Dimension(111, 104));
        panel_transfers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_transfersMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_transfersMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_transfersMouseExited(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 102, 0));
        jLabel12.setText("Transfer");

        jLabel40.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fa-taxi.png"))); // NOI18N

        javax.swing.GroupLayout panel_transfersLayout = new javax.swing.GroupLayout(panel_transfers);
        panel_transfers.setLayout(panel_transfersLayout);
        panel_transfersLayout.setHorizontalGroup(
            panel_transfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_transfersLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(panel_transfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_transfersLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_transfersLayout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))))
        );
        panel_transfersLayout.setVerticalGroup(
            panel_transfersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_transfersLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12))
        );

        panel_biciagil.setBackground(new java.awt.Color(234, 234, 234));
        panel_biciagil.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_biciagil.setMaximumSize(new java.awt.Dimension(111, 104));
        panel_biciagil.setMinimumSize(new java.awt.Dimension(111, 104));
        panel_biciagil.setPreferredSize(new java.awt.Dimension(111, 104));
        panel_biciagil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_biciagilMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_biciagilMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_biciagilMouseExited(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 102, 0));
        jLabel13.setText("Bici-ágil");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ion-android-bicycle.png"))); // NOI18N

        javax.swing.GroupLayout panel_biciagilLayout = new javax.swing.GroupLayout(panel_biciagil);
        panel_biciagil.setLayout(panel_biciagilLayout);
        panel_biciagilLayout.setHorizontalGroup(
            panel_biciagilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_biciagilLayout.createSequentialGroup()
                .addGroup(panel_biciagilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_biciagilLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel13))
                    .addGroup(panel_biciagilLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel7)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        panel_biciagilLayout.setVerticalGroup(
            panel_biciagilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_biciagilLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13))
        );

        panel_clientes.setBackground(new java.awt.Color(234, 234, 234));
        panel_clientes.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_clientes.setMaximumSize(new java.awt.Dimension(111, 104));
        panel_clientes.setMinimumSize(new java.awt.Dimension(111, 104));
        panel_clientes.setPreferredSize(new java.awt.Dimension(111, 104));
        panel_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_clientesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_clientesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_clientesMouseExited(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 102, 0));
        jLabel14.setText("Clientes");

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fa-users.png"))); // NOI18N

        javax.swing.GroupLayout panel_clientesLayout = new javax.swing.GroupLayout(panel_clientes);
        panel_clientes.setLayout(panel_clientesLayout);
        panel_clientesLayout.setHorizontalGroup(
            panel_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_clientesLayout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(28, 28, 28))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_clientesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panel_clientesLayout.setVerticalGroup(
            panel_clientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_clientesLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14))
        );

        panel_conductores.setBackground(new java.awt.Color(234, 234, 234));
        panel_conductores.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_conductores.setMaximumSize(new java.awt.Dimension(111, 104));
        panel_conductores.setMinimumSize(new java.awt.Dimension(111, 104));
        panel_conductores.setPreferredSize(new java.awt.Dimension(111, 104));
        panel_conductores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_conductoresMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel_conductoresMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel_conductoresMouseExited(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 102, 0));
        jLabel15.setText("Conductores");

        jLabel41.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fa-tachometer.png"))); // NOI18N

        javax.swing.GroupLayout panel_conductoresLayout = new javax.swing.GroupLayout(panel_conductores);
        panel_conductores.setLayout(panel_conductoresLayout);
        panel_conductoresLayout.setHorizontalGroup(
            panel_conductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_conductoresLayout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(20, 20, 20))
            .addGroup(panel_conductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panel_conductoresLayout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jLabel41)
                    .addContainerGap(26, Short.MAX_VALUE)))
        );
        panel_conductoresLayout.setVerticalGroup(
            panel_conductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_conductoresLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel15))
            .addGroup(panel_conductoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panel_conductoresLayout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jLabel41)
                    .addContainerGap(23, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel_logo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(panel_datos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panel_conductores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_buses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_transfers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_biciagil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel_datos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel_logo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panel_buses, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_transfers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_biciagil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_clientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel_conductores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panel_biciagilMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_biciagilMouseClicked
        cerrarDialogs();
        this.jDialog_Bici.setVisible(true);
        jDialog_Bici.setLocationRelativeTo(null); //Ubica ventana en la mitad de la pantalla
    }//GEN-LAST:event_panel_biciagilMouseClicked

    private void panel_conductoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_conductoresMouseClicked
        cerrarDialogs();
        this.jDialog_Conductores.setVisible(true);
        jDialog_Conductores.setLocationRelativeTo(null);
    }//GEN-LAST:event_panel_conductoresMouseClicked

    private void panel_busesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_busesMouseClicked
        cerrarDialogs();
        this.jDialog_Buses.setVisible(true);
        jDialog_Buses.setLocationRelativeTo(null);
    }//GEN-LAST:event_panel_busesMouseClicked

    private void panel_transfersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_transfersMouseClicked
        cerrarDialogs();
        this.jDialog_Transfers.setVisible(true);
        jDialog_Transfers.setLocationRelativeTo(null);
    }//GEN-LAST:event_panel_transfersMouseClicked

    private void panel_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_clientesMouseClicked
        cerrarDialogs();
        this.jDialog_Clientes.setVisible(true);
        jDialog_Clientes.setLocationRelativeTo(null);
    }//GEN-LAST:event_panel_clientesMouseClicked

    private void panel_biciagilMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_biciagilMouseEntered
        panel_biciagil.setBackground(new Color(248, 248, 248));
    }//GEN-LAST:event_panel_biciagilMouseEntered

    private void panel_biciagilMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_biciagilMouseExited
        panel_biciagil.setBackground(new Color(234, 234, 234));
    }//GEN-LAST:event_panel_biciagilMouseExited

    private void panel_conductoresMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_conductoresMouseEntered
        panel_conductores.setBackground(new Color(248, 248, 248));
    }//GEN-LAST:event_panel_conductoresMouseEntered

    private void panel_conductoresMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_conductoresMouseExited
        panel_conductores.setBackground(new Color(234, 234, 234));
    }//GEN-LAST:event_panel_conductoresMouseExited

    private void panel_busesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_busesMouseEntered
        panel_buses.setBackground(new Color(248, 248, 248));
    }//GEN-LAST:event_panel_busesMouseEntered

    private void panel_busesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_busesMouseExited
        panel_buses.setBackground(new Color(234, 234, 234));
    }//GEN-LAST:event_panel_busesMouseExited

    private void panel_transfersMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_transfersMouseEntered
        panel_transfers.setBackground(new Color(248, 248, 248));
    }//GEN-LAST:event_panel_transfersMouseEntered

    private void panel_transfersMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_transfersMouseExited
        panel_transfers.setBackground(new Color(234, 234, 234));
    }//GEN-LAST:event_panel_transfersMouseExited

    private void panel_clientesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_clientesMouseEntered
        panel_clientes.setBackground(new Color(248, 248, 248));
    }//GEN-LAST:event_panel_clientesMouseEntered

    private void panel_clientesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_clientesMouseExited
        panel_clientes.setBackground(new Color(234, 234, 234));
    }//GEN-LAST:event_panel_clientesMouseExited

    private void btn_verBicisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verBicisActionPerformed
        this.JDListadoBicicletas.setVisible(true);
        this.jDialog_Bici.setVisible(false);
    }//GEN-LAST:event_btn_verBicisActionPerformed

    private void txtEdadConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEdadConductorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEdadConductorActionPerformed

    private void btnBuscarConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarConductorActionPerformed
        try {

            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from CONDUCTOR where CONDUCTOR_ID ="
                    + Integer.parseInt(this.txtIdConductor.getText()));

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(JDOpcionesConductores, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                this.txtCedulaConductor.setText("" + resultado.getString("CEDULA"));
                this.txtNombreConductor.setText("" + resultado.getString("NOMBRE"));
                this.txtEdadConductor.setText("" + resultado.getString("EDAD"));
                this.txtTelefonoConductor.setText("" + resultado.getString("TELEFONO"));
                this.txtDireccionConductor.setText("" + resultado.getString("DIRECCION"));
                this.txtContrasenaConductor.setText("" + resultado.getString("CONTRASEÑA"));
                this.txtEstadoConductor.setText("" + resultado.getString("ESTADO"));
            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarConductorActionPerformed

    private void btnAtrasConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasConductorActionPerformed
        this.JDOpcionesConductores.setVisible(false);
        this.jDialog_Conductores.setVisible(true);
    }//GEN-LAST:event_btnAtrasConductorActionPerformed

    private void btn_verConductoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verConductoresActionPerformed
        this.jDialog_Conductores.setVisible(false);
        this.JDListadoConductores.setVisible(true);

        DefaultTableModel modelo = new DefaultTableModel();

        ConectarBD conexion = new ConectarBD();
        Statement sentencia;

        try {
            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM CONDUCTOR ");
            ResultSetMetaData campos = resultado.getMetaData();
            int cantidadColumnas = campos.getColumnCount();
            for (int i = 1; i <= cantidadColumnas; i++) {
                modelo.addColumn(campos.getColumnLabel(i));
            }
            while (resultado.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i] = resultado.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
            resultado.close();
            conexion.getConexion().close();
            this.ListaDeConductores.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDListadoConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDListadoConductores, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }


    }//GEN-LAST:event_btn_verConductoresActionPerformed

    private void btn_verBusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verBusesActionPerformed
        this.JDListadoBuses.setVisible(true);
        this.jDialog_Buses.setVisible(false);
    }//GEN-LAST:event_btn_verBusesActionPerformed

    private void btn_verRutaAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verRutaAActionPerformed
        jDialog_Buses.setVisible(false);
        vRuta = new VentanaRuta("A");
        vRuta.setVisible(true);
    }//GEN-LAST:event_btn_verRutaAActionPerformed

    private void btn_verRutaBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verRutaBActionPerformed
        jDialog_Buses.setVisible(false);
        vRuta = new VentanaRuta("B");
        vRuta.setVisible(true);
    }//GEN-LAST:event_btn_verRutaBActionPerformed

    private void btn_verRutaCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verRutaCActionPerformed
        jDialog_Buses.setVisible(false);
        vRuta = new VentanaRuta("C");
        vRuta.setVisible(true);
    }//GEN-LAST:event_btn_verRutaCActionPerformed

    private void btn_verClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verClientesActionPerformed
        this.jDialog_Clientes.setVisible(false);

        DefaultTableModel modelo = new DefaultTableModel();

        this.ListaDeClientes.setModel(modelo);
        ConectarBD conexion = new ConectarBD();
        Statement sentencia;

        try {
            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM USUARIO ");
            ResultSetMetaData campos = resultado.getMetaData();
            int cantidadColumnas = campos.getColumnCount();
            for (int i = 1; i <= cantidadColumnas; i++) {
                modelo.addColumn(campos.getColumnLabel(i));
            }
            while (resultado.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i] = resultado.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDListadoClientes, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDListadoClientes, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }

        this.JDListadoClientes.setVisible(true);
    }//GEN-LAST:event_btn_verClientesActionPerformed

    private void btn_OpcionesConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_OpcionesConductorActionPerformed
        // TODO add your handling code here:
        this.JDOpcionesConductores.setVisible(true);
        this.jDialog_Conductores.setVisible(false);

        this.txtIdConductor.setText("");
        this.txtCedulaConductor.setText("");
        this.txtNombreConductor.setText("");
        this.txtEdadConductor.setText("");
        this.txtTelefonoConductor.setText("");
        this.txtDireccionConductor.setText("");
        this.txtContrasenaConductor.setText("");
        this.txtEstadoConductor.setText("");

    }//GEN-LAST:event_btn_OpcionesConductorActionPerformed

    private void btnRegistrarConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarConductorActionPerformed
        // TODO add your handling code here:

        try {
            objCond.guardarCond(this.txtIdConductor.getText(), this.txtCedulaConductor.getText(), this.txtNombreConductor.getText(),
                    this.txtEdadConductor.getText(), this.txtTelefonoConductor.getText(), this.txtDireccionConductor.getText(),
                    this.txtContrasenaConductor.getText(), this.txtEstadoConductor.getText());

            this.txtIdConductor.setText("");
            this.txtCedulaConductor.setText("");
            this.txtNombreConductor.setText("");
            this.txtEdadConductor.setText("");
            this.txtTelefonoConductor.setText("");
            this.txtDireccionConductor.setText("");
            this.txtContrasenaConductor.setText("");
            this.txtEstadoConductor.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_btnRegistrarConductorActionPerformed

    private void btnEditarConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarConductorActionPerformed
        // TODO add your handling code here:

        try {
            objCond.actualizarCond(this.txtIdConductor.getText(), this.txtCedulaConductor.getText(), this.txtNombreConductor.getText(),
                    this.txtEdadConductor.getText(), this.txtTelefonoConductor.getText(), this.txtDireccionConductor.getText(),
                    this.txtContrasenaConductor.getText(), this.txtEstadoConductor.getText());

            this.txtIdConductor.setText("");
            this.txtCedulaConductor.setText("");
            this.txtNombreConductor.setText("");
            this.txtEdadConductor.setText("");
            this.txtTelefonoConductor.setText("");
            this.txtDireccionConductor.setText("");
            this.txtContrasenaConductor.setText("");
            this.txtEstadoConductor.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_btnEditarConductorActionPerformed

    private void btnEliminarConductorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarConductorActionPerformed
        // TODO add your handling code here:

        int seleccion = JOptionPane.showOptionDialog(JDOpcionesConductores, "¿Desea eliminar este registro(Si/No)", "Seleccione una opción",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if ((seleccion + 1) == 1) {
            try {

                objCond.eliminarCond(this.txtIdConductor.getText());

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(JDOpcionesConductores, "Error" + e, "Informacion",
                        JOptionPane.WARNING_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "El registro no ha sido eliminado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        this.txtIdConductor.setText("");
        this.txtCedulaConductor.setText("");
        this.txtNombreConductor.setText("");
        this.txtEdadConductor.setText("");
        this.txtTelefonoConductor.setText("");
        this.txtDireccionConductor.setText("");
        this.txtContrasenaConductor.setText("");
        this.txtEstadoConductor.setText("");
    }//GEN-LAST:event_btnEliminarConductorActionPerformed

    private void btnSalirListadoDeConductoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirListadoDeConductoresActionPerformed
        this.JDListadoConductores.setVisible(false);
        this.jDialog_Conductores.setVisible(true);
    }//GEN-LAST:event_btnSalirListadoDeConductoresActionPerformed

    private void btnSalirListadoDeClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirListadoDeClientesActionPerformed
        this.JDListadoClientes.setVisible(false);
        this.jDialog_Clientes.setVisible(true);
    }//GEN-LAST:event_btnSalirListadoDeClientesActionPerformed

    private void btnVerBicicletasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerBicicletasActionPerformed

        DefaultTableModel modelo = new DefaultTableModel();

        this.ListaBicicletas.setModel(modelo);
        ConectarBD conexion = new ConectarBD();
        Statement sentencia;

        try {
            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM BICICLETA ");
            ResultSetMetaData campos = resultado.getMetaData();
            int cantidadColumnas = campos.getColumnCount();
            for (int i = 1; i <= cantidadColumnas; i++) {
                modelo.addColumn(campos.getColumnLabel(i));
            }
            while (resultado.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i] = resultado.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDListadoBicicletas, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDListadoBicicletas, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_btnVerBicicletasActionPerformed

    private void btnRegistrarBicicletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarBicicletaActionPerformed
        try {
            objBici.guardarBici(this.txtIdBicicleta.getText(), this.cmbEstadoBicicleta.getSelectedItem().toString());

            this.txtIdBicicleta.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDListadoBicicletas, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnRegistrarBicicletaActionPerformed

    private void btnEditarBicicletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarBicicletaActionPerformed
        try {
            objBici.actualizarBici(this.txtIdBicicleta.getText(), this.cmbEstadoBicicleta.getSelectedItem().toString());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDListadoBicicletas, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }
        this.txtIdBicicleta.setText("");
    }//GEN-LAST:event_btnEditarBicicletaActionPerformed

    private void btnEliminarBicicletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarBicicletaActionPerformed
        // TODO add your handling code here:

        int seleccion = JOptionPane.showOptionDialog(JDListadoBicicletas, "¿Esta seguro de eliminar este registro?(Si/No)", "Seleccione una opción",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if ((seleccion + 1) == 1) {
            try {

                objBici.eliminarBici(this.txtIdBicicleta.getText());

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(JDListadoBicicletas, "Error" + e, "Informacion",
                        JOptionPane.WARNING_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(JDListadoBicicletas, "El registro no ha sido eliminado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        this.txtIdBicicleta.setText("");


    }//GEN-LAST:event_btnEliminarBicicletaActionPerformed

    private void btnEliminarBusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarBusActionPerformed
        int seleccion = JOptionPane.showOptionDialog(JDListadoBuses, "¿Esta seguro de eliminar este registro?(Si/No)", "Seleccione una opción",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if ((seleccion + 1) == 1) {
            try {
                objBus.eliminarBus(Integer.parseInt(this.txtIdBus.getText()));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(JDListadoBuses, "Error" + e, "Informacion",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(JDListadoBuses, "El registro no ha sido eliminado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        this.txtIdBus.setText("");
    }//GEN-LAST:event_btnEliminarBusActionPerformed

    private void btnEditarBusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarBusActionPerformed
        try {
            objBus.actualizarBus(Integer.parseInt(this.txtIdBus.getText()), this.cmbEstadoBus.getSelectedItem().toString(), this.txtMatriculaBus.getText(), this.txtUbicacion.getText());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDListadoBuses, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }
        this.txtIdBicicleta.setText("");
    }//GEN-LAST:event_btnEditarBusActionPerformed

    private void btnRegistrarBusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarBusActionPerformed
        try {
            objBus.guardarBus(Integer.parseInt(this.txtIdBus.getText()), this.cmbEstadoBus.getSelectedItem().toString(), this.txtMatriculaBus.getText(), this.txtUbicacion.getText());

            this.txtIdBus.setText("");
            this.txtMatriculaBus.setText("");
            this.txtUbicacion.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDListadoBuses, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnRegistrarBusActionPerformed

    private void btnVerBusesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerBusesActionPerformed

        DefaultTableModel modelo = new DefaultTableModel();

        this.ListaBuses.setModel(modelo);
        ConectarBD conexion = new ConectarBD();
        Statement sentencia;

        try {
            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM BUS");
            ResultSetMetaData campos = resultado.getMetaData();
            int cantidadColumnas = campos.getColumnCount();
            for (int i = 1; i <= cantidadColumnas; i++) {
                modelo.addColumn(campos.getColumnLabel(i));
            }
            while (resultado.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i] = resultado.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this.JDListadoBuses, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.JDListadoBuses, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnVerBusesActionPerformed

    private void btn_volverAtrasBusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_volverAtrasBusActionPerformed
        this.JDListadoBuses.setVisible(false);
        this.jDialog_Buses.setVisible(true);
    }//GEN-LAST:event_btn_volverAtrasBusActionPerformed

    private void btn_volverAtrasBiciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_volverAtrasBiciActionPerformed
        this.jDialog_Bici.setVisible(true);
        this.JDListadoBicicletas.setVisible(false);
    }//GEN-LAST:event_btn_volverAtrasBiciActionPerformed

    private void btnVerTransfersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerTransfersActionPerformed
        DefaultTableModel modelo = new DefaultTableModel();

        ConectarBD conexion = new ConectarBD();
        Statement sentencia;

        try {
            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM TRANSFER");
            ResultSetMetaData campos = resultado.getMetaData();
            int cantidadColumnas = campos.getColumnCount();
            for (int i = 1; i <= cantidadColumnas; i++) {
                modelo.addColumn(campos.getColumnLabel(i));
            }
            while (resultado.next()) {
                Object[] fila = new Object[cantidadColumnas];
                for (int i = 0; i < cantidadColumnas; i++) {
                    fila[i] = resultado.getObject(i + 1);
                }
                modelo.addRow(fila);
            }
            this.ListaTransfers.setModel(modelo);
            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this.JDListadoTransfers, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this.JDListadoTransfers, "Error:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnVerTransfersActionPerformed

    private void btnRegistrarTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarTransferActionPerformed
        try {
            objTra.guardarTransfer(Integer.parseInt(this.txtIdTransfer.getText()), this.cmbEstadoTransfer.getSelectedItem().toString(), this.txtMatriculaTransfer.getText(), this.txtUbicacionTransfer.getText());

            this.txtIdTransfer.setText("");
            this.txtMatriculaTransfer.setText("");
            this.txtUbicacionTransfer.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDListadoTransfers, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnRegistrarTransferActionPerformed

    private void btnEditarTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarTransferActionPerformed
        try {
            objTra.actualizarTransfer(Integer.parseInt(this.txtIdTransfer.getText()), this.cmbEstadoTransfer.getSelectedItem().toString(), this.txtMatriculaTransfer.getText(), this.txtUbicacionTransfer.getText());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(JDListadoTransfers, "Error" + e, "Informacion",
                    JOptionPane.WARNING_MESSAGE);
        }
        this.txtIdTransfer.setText("");
    }//GEN-LAST:event_btnEditarTransferActionPerformed

    private void btnEliminarTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTransferActionPerformed
        int seleccion = JOptionPane.showOptionDialog(JDListadoTransfers, "¿Esta seguro de eliminar este registro?(Si/No)", "Seleccione una opción",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
        if ((seleccion + 1) == 1) {
            try {
                objBus.eliminarBus(Integer.parseInt(this.txtIdTransfer.getText()));

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(JDListadoTransfers, "Error" + e, "Informacion",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(JDListadoTransfers, "El registro no ha sido eliminado", "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        this.txtIdTransfer.setText("");
    }//GEN-LAST:event_btnEliminarTransferActionPerformed

    private void btn_volverAtrasTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_volverAtrasTransferActionPerformed
        this.jDialog_Transfers.setVisible(true);
        this.JDListadoTransfers.setVisible(false);
    }//GEN-LAST:event_btn_volverAtrasTransferActionPerformed

    private void btn_verTransfersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_verTransfersActionPerformed
        this.jDialog_Transfers.setVisible(false);
        this.JDListadoTransfers.setVisible(true);
    }//GEN-LAST:event_btn_verTransfersActionPerformed

    private void btnCargarBusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCargarBusMouseClicked
        try {

            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from BUS where BUS_ID ="
                    + Integer.parseInt(this.txtIdBus.getText()));

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(txtIdBus, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                this.txtMatriculaBus.setText("" + resultado.getString("MATRICULA"));
                this.txtUbicacion.setText("" + resultado.getString("UBICACION"));

                if (resultado.getString("ESTADO").equals("Libre")) {
                    cmbEstadoBus.setSelectedItem("Libre");
                } else {

                    cmbEstadoBus.setSelectedItem("En recorrido");
                }

            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error: Ingrese un id válido e intente de nuevo.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnCargarBusMouseClicked

    private void btnCargarTransferMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCargarTransferMouseClicked
        try {

            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from TRANSFER where TRANSFER_ID ="
                    + Integer.parseInt(this.txtIdTransfer.getText()));

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(txtIdTransfer, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                this.txtMatriculaTransfer.setText("" + resultado.getString("MATRICULA"));
                this.txtUbicacionTransfer.setText("" + resultado.getString("UBICACION"));

                if (resultado.getString("ESTADO").equals("Libre")) {
                    cmbEstadoTransfer.setSelectedItem("Libre");
                } else {
                    cmbEstadoTransfer.setSelectedItem("Reservado");
                }

            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error: Ingrese un id válido e intente de nuevo.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnCargarTransferMouseClicked

    private void btnCargarBicicletasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCargarBicicletasMouseClicked
        try {

            ConectarBD conexion = new ConectarBD();
            Statement sentencia;

            sentencia = conexion.getConexion().createStatement();
            ResultSet resultado = sentencia.executeQuery("select * from BICICLETA where BICICLETA_ID ="
                    + Integer.parseInt(this.txtIdBicicleta.getText()));

            if (!resultado.isBeforeFirst()) {
                JOptionPane.showMessageDialog(txtIdBicicleta, "No se encontraron registros.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

            while (resultado.next()) {
                if (resultado.getString("ESTADO").equals("Libre")) {
                    cmbEstadoBicicleta.setSelectedItem("Libre");
                } else {
                    cmbEstadoBicicleta.setSelectedItem("Reservada");
                }

//                if (resultado.getString("ESTADO").equals("Libre")) {
//                    cmbEstadoBicicleta.setSelectedItem("Libre");
//                } else {
//                    cmbEstadoBicicleta.setSelectedItem("Reservada");
//                }
            }

            resultado.close();
            conexion.getConexion().close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error SQL:" + e, "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(JDOpcionesConductores, "Error: Ingrese un id válido e intente de nuevo.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnCargarBicicletasMouseClicked

    private void cerrarDialogs() {
        this.jDialog_Bici.setVisible(false);
        this.jDialog_Buses.setVisible(false);
        this.jDialog_Clientes.setVisible(false);
        this.jDialog_Conductores.setVisible(false);
        this.jDialog_Transfers.setVisible(false);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog JDListadoBicicletas;
    private javax.swing.JDialog JDListadoBuses;
    private javax.swing.JDialog JDListadoClientes;
    private javax.swing.JDialog JDListadoConductores;
    private javax.swing.JDialog JDListadoTransfers;
    private javax.swing.JDialog JDOpcionesConductores;
    private javax.swing.JTable ListaBicicletas;
    private javax.swing.JTable ListaBuses;
    private javax.swing.JTable ListaDeClientes;
    private javax.swing.JTable ListaDeConductores;
    private javax.swing.JTable ListaTransfers;
    private javax.swing.JButton btnAtrasConductor;
    private javax.swing.JButton btnBuscarConductor;
    private javax.swing.JLabel btnCargarBicicletas;
    private javax.swing.JLabel btnCargarBus;
    private javax.swing.JLabel btnCargarTransfer;
    private javax.swing.JButton btnEditarBicicleta;
    private javax.swing.JButton btnEditarBus;
    private javax.swing.JButton btnEditarConductor;
    private javax.swing.JButton btnEditarTransfer;
    private javax.swing.JButton btnEliminarBicicleta;
    private javax.swing.JButton btnEliminarBus;
    private javax.swing.JButton btnEliminarConductor;
    private javax.swing.JButton btnEliminarTransfer;
    private javax.swing.JButton btnRegistrarBicicleta;
    private javax.swing.JButton btnRegistrarBus;
    private javax.swing.JButton btnRegistrarConductor;
    private javax.swing.JButton btnRegistrarTransfer;
    private javax.swing.JButton btnSalirListadoDeClientes;
    private javax.swing.JButton btnSalirListadoDeConductores;
    private javax.swing.JButton btnVerBicicletas;
    private javax.swing.JButton btnVerBuses;
    private javax.swing.JButton btnVerTransfers;
    private javax.swing.JButton btn_OpcionesConductor;
    private javax.swing.JButton btn_verBicis;
    private javax.swing.JButton btn_verBuses;
    private javax.swing.JButton btn_verClientes;
    private javax.swing.JButton btn_verConductores;
    private javax.swing.JButton btn_verRutaA;
    private javax.swing.JButton btn_verRutaB;
    private javax.swing.JButton btn_verRutaC;
    private javax.swing.JButton btn_verTransfers;
    private javax.swing.JButton btn_volverAtrasBici;
    private javax.swing.JButton btn_volverAtrasBus;
    private javax.swing.JButton btn_volverAtrasTransfer;
    private javax.swing.JComboBox cmbEstadoBicicleta;
    private javax.swing.JComboBox cmbEstadoBus;
    private javax.swing.JComboBox cmbEstadoTransfer;
    private javax.swing.JComboBox cmbNombreEstacionBicicletas;
    private javax.swing.JDialog jDialog_Bici;
    private javax.swing.JDialog jDialog_Buses;
    private javax.swing.JDialog jDialog_Clientes;
    private javax.swing.JDialog jDialog_Conductores;
    private javax.swing.JDialog jDialog_Transfers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel label_correo;
    private javax.swing.JLabel label_nombre;
    private javax.swing.JLabel label_username;
    private javax.swing.JPanel panel_biciagil;
    private javax.swing.JPanel panel_buses;
    private javax.swing.JPanel panel_clientes;
    private javax.swing.JPanel panel_conductores;
    private javax.swing.JPanel panel_datos;
    private javax.swing.JPanel panel_logo;
    private javax.swing.JPanel panel_transfers;
    private javax.swing.JTextField txtCedulaConductor;
    private javax.swing.JTextField txtContrasenaConductor;
    private javax.swing.JTextField txtDireccionConductor;
    private javax.swing.JTextField txtEdadConductor;
    private javax.swing.JTextField txtEstadoConductor;
    private javax.swing.JTextField txtIdBicicleta;
    private javax.swing.JTextField txtIdBus;
    private javax.swing.JTextField txtIdConductor;
    private javax.swing.JTextField txtIdTransfer;
    private javax.swing.JTextField txtMatriculaBus;
    private javax.swing.JTextField txtMatriculaTransfer;
    private javax.swing.JTextField txtNombreConductor;
    private javax.swing.JTextField txtTelefonoConductor;
    private javax.swing.JTextField txtUbicacion;
    private javax.swing.JTextField txtUbicacionTransfer;
    // End of variables declaration//GEN-END:variables
}
