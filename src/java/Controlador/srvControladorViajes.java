package Controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Modelo.DTORuta;
import DAO.DAORutas;
import java.sql.Date;
import java.sql.Time;
import java.util.LinkedList;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

@WebServlet(name = "srvControladorViajes", urlPatterns = {"/srvControladorViajes"})
public class srvControladorViajes extends HttpServlet {
    
    DTORuta ruta = new DTORuta();
    
    public void LeerDatos(HttpServletRequest request, HttpServletResponse response, boolean editar){
        ruta.setIdBus(Integer.parseInt(request.getParameter("slctBus")));
        ruta.setIdChofer(Integer.parseInt(request.getParameter("slctChofer")));
        ruta.setFechaSalida(Date.valueOf(request.getParameter("txtFechaSalida")));
        //Valida si es necesario agregar segundos o no
        String agregarSeg = editar ? "" : ":00";
        //Se modifica para adaptarlo al formato time de sql
        String horaSalida = request.getParameter("txtHoraSalida") + agregarSeg;
        ruta.setHoraSalida(Time.valueOf(horaSalida));
        ruta.setOrigen(Integer.parseInt(request.getParameter("slctOrigen")));
        ruta.setFechaLlegada(Date.valueOf(request.getParameter("txtFechaLlegada")));
        //Se modifica para adaptarlo al formato time de sql
        String horaLlegada = request.getParameter("txtHoraLlegada") + agregarSeg;
        ruta.setHoraLlegada(Time.valueOf(horaLlegada));
        ruta.setDestino(Integer.parseInt(request.getParameter("slctDestino")));
        ruta.setPrecio(Double.parseDouble(request.getParameter("txtPrecio")));
        ruta.setBoletosRestantes(Integer.parseInt(request.getParameter("txtBoletos")));
        if (editar) {
            ruta.setId(Integer.parseInt(request.getParameter("idRuta")));
        } else {
            ruta.setCreador(Integer.parseInt(request.getParameter("id")));
        }
    }  

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        DAORutas dao = new DAORutas();
        
        //Dependiendo del valor de accion que se puso junto al url del servlet sera lo que obtenga
        String action = request.getParameter("accion");
        
        //Obtiene los datos para precargarlos en el form y ser editados
        if (action.equalsIgnoreCase("editar")) {
            int idRuta = Integer.parseInt(request.getParameter("idRuta"));
            ruta = dao.ObtenerRuta(idRuta);
            HttpSession session = request.getSession();
            session.setAttribute("detalleRuta", ruta);
            response.sendRedirect("Vista/EditarViaje.jsp");
        }
        //Realiza la modificacion en la BD
        else if (action.equalsIgnoreCase("actualizar")) {
            LeerDatos(request, response, true);
            dao.EditarRuta(ruta);
            response.sendRedirect("Vista/AdministrarViajes.jsp");
        }
        //Obtiene todos los registros en la tabla
        else if (action.equalsIgnoreCase("listar")) {
            LinkedList<DTORuta> listaRutas = dao.ListarRutas();
            HttpSession session = request.getSession();
            session.setAttribute("listaRutas", listaRutas);
            
            RequestDispatcher vista = request.getRequestDispatcher("Vista/AdministrarViajes.jsp");
            vista.forward(request, response);
        }
        //Realiza la insercion de nuevo registro en la BD
        else if (action.equalsIgnoreCase("agregar")) {
            LeerDatos(request, response, false);
            dao.AgregarRuta(ruta);
        }
        //Cambia el estado para que no figure al obtener todos los registros de la tabla
        else if (action.equalsIgnoreCase("eliminar")) {
            int idRuta = Integer.parseInt(request.getParameter("idRuta"));
            dao.EliminarRuta(idRuta);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
