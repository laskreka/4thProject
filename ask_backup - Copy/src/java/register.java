import javax.servlet.annotation.WebServlet;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.*;

/**
 *
 * @author laskreka
 */
@WebServlet(name = "register", urlPatterns = {"/register"}, loadOnStartup=0)
public class register extends HttpServlet {
    static RegisterListener listener = null;
    
    public static void addRegisterListener(RegisterListener registerListener)
    {
        listener = registerListener;
    }
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Statement stmt;
        ResultSet rs;
        
        
        if ( username == null || password == null ) {
            out.println("<h1>Invalid Register Request</h1>");
            out.println("<a href=\"register.html\">Register</a>");
            
            return;
        }
        
        Connection con;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://localhost/photos?" +
                                   "user=root&password=laskreka";
            con = DriverManager.getConnection(connectionUrl);

            if ( con!=null ) {
               System.out.println("Ola ok me mysql");
            }

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM users WHERE Username='" + username + "'");
             if (rs.next()){
                out.println("<h1>Username exists</h1>");
                out.println("<a href=\"register.html\">Register</a>");

                stmt.close();
                rs.close();
                con.close();
                return;
             }
             stmt.close();
             rs.close();

             
             stmt = con.createStatement();
             
             if ( !stmt.execute("INSERT INTO users VALUES('" + username+"', '" + password + "')") ) {
                 out.println("<h1>You are now registered " + username + "</h1>");
                 out.println("<a href=\"index.jsp\">Login</a>");
                 listener.userRegister(username);
             } else {
                 out.println("<h1>Could not add your username to the db</h1>");
                 out.println("<a href=\"register.html\">Register</a>");
             }
             
             stmt.close();
             rs.close();
             con.close();
        } catch (SQLException e) {
                throw new ServletException("Servlet Could not display records.", e);
        } catch (ClassNotFoundException e ) {
            
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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