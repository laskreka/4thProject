/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import java.util.Map.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

/**
 *
 * @author lamprinh
 */
@WebServlet(name = "listener", urlPatterns = {"/listener"}, loadOnStartup = 1)
public class listener extends HttpServlet implements LoginListener, RegisterListener, UploadListener {

    static List<String> usersRegistered = null;
    static List<String> usersLogined = null;
    static List<String> photos = null;
    static Map<String, Integer> photos_uploaded;

    public void PhotoUploaded(String username, String photo) {
        Integer i;
        photos.add(photo);
        i = photos_uploaded.get(username);
        if (i == null) {
            i = 1;
        } else {
            i++;
        }
        photos_uploaded.put(username, i);
    }

    public void userRegister(String user) {
        if (usersRegistered == null) {
            usersRegistered = new ArrayList<String>();
        }
        usersRegistered.add(user);
    }

    public void userLogin(String user) {
        if (usersLogined == null) {
            usersLogined = new ArrayList<String>();
        }

        if (usersLogined.contains(user) == false) {
            usersLogined.add(user);
        }
    }

    public void init(ServletConfig config) throws ServletException {
        int i = 0;

        super.init(config);
        photos_uploaded = new HashMap<String, Integer>();
        photos = new ArrayList<String>();

        login.addLoginListener(this);
        register.addRegisterListener(this);
        upload.addUploadListener(this);

        Connection con = null;
        Statement stmt;
        ResultSet rs;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://localhost/photos?"
                    + "user=root&password=laskreka";
            con = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {

            return;
        } catch (ClassNotFoundException e) {
            return;
        }
        try {
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM myphotos");

            while (rs.next()) {
                PhotoUploaded(rs.getString(1), rs.getString(2));
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            throw new ServletException("Servlet Could not display records.", e);
        }
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
        Iterator i;
        int j;
        String job = request.getParameter("job");


        if (job != null && job.equals("overall") == true) {
            try {
                
                /* TODO output your page here. You may use following sample code. */
                out.println("<div id=\"statistics\">");
                out.println("<h4 style=\"font-weight:bold;font-size:20pt; color:burlywood;\"/> Statistics</h4>");
                //out.println("<h1>Servlet listener at " + request.getContextPath() + "</h1>");
                if (usersLogined != null) {
                    out.println("<p>Users who have logged in since startup (" + usersLogined.size() + ")</p>");
                } else {
                    out.println("<p>Users who have logged in since startup (0)</p>");
                }

                if (usersRegistered != null) {
                    out.println("<p>Users which are registered since startup (" + usersRegistered.size() + ")</p>");
                } else {
                    out.println("<p>Users which which are registered since startup (0)</p>");
                }

                out.println("<p>Photos uploaded: " + photos.size() + "</p>");
                
                out.println("<h4 style=\"color: brown\">Last 3 uploaded photos: </h4>");
                out.println("<div id=\"photo_thumbnails\">");
                if (photos.size() >= 3) {
                    for (j = 0; j < 3; j++) {
                        out.println("<img class=\"photo_small\" src=\"homework3/" + photos.get(photos.size() - 3 + j) + "\">");
                    }
                } else {
                    for (j = 0; j < photos.size(); j++) {
                        out.println("<img class=\"photo_small\" src=\"homework3/" + photos.get(j) + "\">");
                    }
                }
                out.println("</div>");
                out.println("</div>");
                out.println("<a style=\"position:relative; right:-1000px; top:-50px;\" href=\"JavaScript:newPopup('http://'+location.host+'/ask_photo/listener?job=all');\">Details</a>");
            } finally {
                out.close();
            }
        } else {
            try {
                out.println("<html>");
                out.println("<header><link href=\"user_main.css\" rel=\"stylesheet\" type=\"text/css\" />");
                out.println("<header></header>");
                out.println("<body>");
                out.println("<div id=\"popup\">");
                //out.println("<h1>Servlet listener at " + request.getContextPath() + "</h1>");
                if (usersLogined != null) {
                    out.println("<p>Users who have logged in since startup (" + usersLogined.size() + ")</p>");
                } else {
                    out.println("<p>Users who have logged in since startup (0)</p>");
                }

                if (usersRegistered != null) {
                    out.println("<p>Users which are registered since restart (" + usersRegistered.size() + ")</p>");
                } else {
                    out.println("<p>Users which which are registered since restart (0)</p>");
                }

                out.println("<p>Photos uploaded: " + photos.size() + "</p>");
                Set s = photos_uploaded.entrySet();
                
                out.println("<h3 style=\"color: brown\">Number of photos uploaded by each user</h3>");
                out.println("<table style=\"color:darksalmon; \" border=1>");
                out.println("<th>Users</th>"
                        + "<th>Images</th>");
                for (i = s.iterator(); i.hasNext();) {
                    Map.Entry<String, Integer> e = (Map.Entry<String, Integer>) i.next();

                    out.println("<tr>");
                    out.println("<td>" + e.getKey() + "</td>");
                    out.println("<td>" + e.getValue() + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");

                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
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
