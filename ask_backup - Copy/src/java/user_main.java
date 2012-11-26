

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author laskreka
 */
import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;
import java.util.regex.*;
import javax.servlet.ServletConfig;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.InputSource;
import java.net.URLEncoder;

public class user_main {

    public static void generate_page(String username, PrintWriter out, String path) {
        Connection con=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://localhost/photos?" +
                                   "user=root&password=laskreka";
            con = DriverManager.getConnection(connectionUrl);

            if ( con!=null ) {
               System.out.println("Ola ok me mysql");
            }
        } catch( SQLException e ) {
            out.println("Connection to SQL failed");
            return;
        } catch ( ClassNotFoundException e ) {
            return;
        }
        
        
        out.println("<html>\n"
                + "<header><link href=\"user_main.css\" rel=\"stylesheet\" type=\"text/css\" />\n"
        
                + "</header>\n"
                + "<body>\n"
                
               + "<script>\n"
               + "setInterval('updateStatistics()', 1000);\n"
               + "function updateStatistics(){\n"
               + "var req = new XMLHttpRequest();\n"
               + "req.open('GET', 'http://'+location.host+'/ask_photo/listener?job=overall', false); \n"
               + "req.send();\n"
               + "if (req.status == 200) { \n"
               + "var x = document.getElementById(\"overall\").innerHTML=req.responseText;\n"
               + "  } \n"
               + "}\n"
               + "function newPopup(url) {"
               +"	popupWindow = window.open("
               +" url,'popUpWindow','height=500,width=800,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,"
               +"location=no,directories=no,status=yes')"
               +"}"
               + "</script>\n"
//                + "<div id=\"user_info\">"
                + "<h1 style=\"color: indianred\"> User: " + username + "</h1><a style=\"color: brown\" href=\"index.jsp\">Logout</a>\n"
                + "</div>\n"
              + "<div id=\"overall\">"
              + "</div>"
                + "<div id=\"Upload\">\n"
                + "  <form action=\"upload\" method=\"post\" \n"
                + "   enctype=\"multipart/form-data\" \n"
                + "   name=\"productForm\" id=\"productForm\"><br><br>\n"
                + "  <table width=\"400px\" align=\"center\" border=0 \n"
                + "  style=\"background-color:ffeeff;\">\n"
                + "  <tr>\n"
                + "  <td colspan=2 style=\"\n"
                + "   font-weight:bold;font-size:20pt; color: burlywood\">\n"
                + "   Image Upload</td>\n"
                + "  </tr>\n"
                + "\n"
                + "  <tr>\n"
                + "  <td>\n"
                + "  <input type=\"file\" name=\"file\" id=\"file\">\n"
                + "  <td><input type=\"submit\" name=\"Submit\" value=\"Submit\"></td>\n"
                + "  <td>\n"
                + "  </tr>\n"
                + "\n"
                + "\n"
                + "  <td colspan=\"2\">&nbsp;</td>\n"
                + "  </tr>\n"
                + "\n"
                + "<tr><td>Describe your picture</td></tr>"
                + "<tr><td><input type=\"text\" name=\"caption\"></td></tr>"
                + "  </table>\n"
                + "  </form>\n"
                + "</div>");


        try {
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select * from myphotos where username='" + username + "'");

            while (rs.next()) {
                out.println("<div class=\"image\">");
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();

                    Document my_doc = db.parse(new File(path + "/homework3/" + rs.getString(2) + ".xml"));
                    NodeList challenges = my_doc.getElementsByTagName("image");

                    Node _ch = challenges.item(0);
                    if (_ch.getNodeType() == Node.ELEMENT_NODE) {
                        Element ch = (Element) _ch;

                        String caption = ch.getElementsByTagName("caption").item(0).getAttributes().item(0).getNodeValue();
                        String transform = ch.getElementsByTagName("transform").item(0).getAttributes().item(0).getNodeValue();

                        if (transform.equals("sepia")) {
                            out.println("<img class=\"sepia\" src=\"homework3/" + rs.getString(2) + "\">");
                        } else {
                            out.println("<img src=\"homework3/" + rs.getString(2) + "\">");
                        }



                        out.println("<p><a href='#'>" + caption + "</a></p>");
                        out.println("<form action=\"Transform\" method=\"post\" >");
                        out.println("<input type=\"submit\" value=\"Sepia\" />\n");
                        out.println("<input type=\"hidden\" name=\"imagename\" value=\"" + rs.getString(2) + "\">");
                        out.println("</form>");


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



                out.println("</div>");
            }
        } catch (Exception e) {
        }

        out.println("</ul>");
        out.println("</body>");
        out.println("</html>");
    }
}
