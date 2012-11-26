
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

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import javax.servlet.http.*;

import java.sql.*;

import java.io.File;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

/**
 *
 * @author laskreka
 */
@WebServlet(name = "upload", urlPatterns = {"/upload"}, loadOnStartup=0)
public class upload extends HttpServlet {
    public static UploadListener listener = null;
    
    public static void addUploadListener(UploadListener ul) {
        listener = ul;
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
        String finalimage = "";
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        processRequest(request, response);
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");
        String caption = "";
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        System.out.println("request: " + request);
        if (!isMultipart) {
            System.out.println("File Not Uploaded");
        } else {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload uploadimage = new ServletFileUpload(factory);
            List items = null;

            try {
                items = uploadimage.parseRequest(request);
                System.out.println("items: " + items);
            } catch (FileUploadException e) {
                e.printStackTrace();
            }



            Iterator itr = items.iterator();

            while (itr.hasNext()) {
                FileItem item = (FileItem) itr.next();
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    System.out.println("name: " + name);
                    String value = item.getString();
                    System.out.println("value: " + value);
                    if (name.equals("caption")) {
                        caption = value;
                    }
                } else {
                    try {
                        String itemName = item.getName();
                        Random generator = new Random();
                        int r = Math.abs(generator.nextInt());

                        String reg = "[.*]";
                        String replacingtext = "";
                        System.out.println("Text before replacing is:-" + itemName);
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(itemName);
                        StringBuffer buffer = new StringBuffer();

                        while (matcher.find()) {
                            matcher.appendReplacement(buffer, replacingtext);
                        }
                        int IndexOf = itemName.indexOf(".");
                        if (IndexOf == -1) {
                            user_main.generate_page(username, out, this.getServletContext().getRealPath(""));
                            return;
                        }
                        String domainName = itemName.substring(IndexOf);

                        System.out.println("domainName: " + domainName);

                        finalimage = buffer.toString() + "_" + r + domainName;
                        System.out.println("Final Image=" + finalimage);


                        File savedFile = new File(this.getServletContext().getRealPath("") + "/homework3/" + finalimage);
                        item.write(savedFile);

                        System.out.println(this.getServletContext().getRealPath("") + "/homework3/" + finalimage);

                        String strQuery = null;

                        Connection con = null;
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            String connectionUrl = "jdbc:mysql://localhost/photos?"
                                    + "user=root&password=laskreka";
                            con = DriverManager.getConnection(connectionUrl);

                            if (con != null) {
                                System.out.println("Ola ok me mysql");
                            }
                        } catch (SQLException e) {
                            out.println("Connection to SQL failed");
                            return;
                        } catch (ClassNotFoundException e) {
                            return;
                        }
                        try {
                            System.out.println("itemName::::: " + itemName);
                            Statement st = con.createStatement();
                            strQuery = ("INSERT INTO myphotos VALUES('" + username + "', '" + finalimage + "')");
                            int rs = st.executeUpdate(strQuery);
                            System.out.println("Query Executed Successfully++++++++++++++");
                            System.out.println("image inserted successfully");
                            if ( listener != null ) { 
                                listener.PhotoUploaded(username, finalimage);
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }



            try {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();

                Element image = doc.createElement("image");
                doc.appendChild(image);
                Element _caption = doc.createElement("caption");
                _caption.setAttribute("value", caption);
                Element _transform = doc.createElement("transform");
                _transform.setAttribute("value", "nothing");
                image.appendChild(_caption);
                image.appendChild(_transform);

                // Prepare the DOM document for writing
                Source source = new DOMSource(doc);
                // Prepare the output file
                File file = new File(this.getServletContext().getRealPath("/") + "/homework3/" + finalimage + ".xml");
                Result result = new StreamResult(file);

                // Write the DOM document to the file
                Transformer xformer = TransformerFactory.newInstance().newTransformer();
                xformer.transform(source, result);

                user_main.generate_page(username, out, this.getServletContext().getRealPath("/"));
            } catch (Exception e) {
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}