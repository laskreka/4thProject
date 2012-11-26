
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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author laskreka
 */
@WebServlet(name = "Transform", urlPatterns = {"/Transform"})
public class Transform extends HttpServlet {
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

        String imagename = request.getParameter("imagename");

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("user");

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document my_doc = db.parse(new File(this.getServletContext().getRealPath("/") + "/homework3/" + imagename + ".xml"));
            NodeList challenges = my_doc.getElementsByTagName("image");

            Node _ch = challenges.item(0);

            if (_ch.getNodeType() == Node.ELEMENT_NODE) {

                Element ch = (Element) _ch;

                String caption = ch.getElementsByTagName("caption").item(0).getAttributes().item(0).getNodeValue();
                String transform = ch.getElementsByTagName("transform").item(0).getAttributes().item(0).getNodeValue();

                System.out.println(caption + ": " + transform);

                if (transform.equals("nothing")) {
                    WriteXMLFile(imagename, caption, "sepia");
                } else {
                    WriteXMLFile(imagename, caption, "nothing");
                }
                
                user_main.generate_page(username, out, this.getServletContext().getRealPath("/"));
                
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    
    public void WriteXMLFile(String finalimage, String caption, String transform) {

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element image = doc.createElement("image");
            doc.appendChild(image);
            Element _caption = doc.createElement("caption");
            _caption.setAttribute("value", caption);
            Element _transform = doc.createElement("transform");
            _transform.setAttribute("value", transform);
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
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }
}