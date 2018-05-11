/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import parsers.WordReader;
import document_indexing.VectorialIndexingManager;
import document_indexing.SearchResult;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author mauro
 */
//@WebServlet(name = "SearchResults", urlPatterns = {"/searchresults"})
public class SearchResults extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        //VectorialIndexingManager im = new VectorialIndexingManager();
        VectorialIndexingManager im = new VectorialIndexingManager(
                "/home/mauro/datos/utn/dlc/DLC-Buscador/web/test_fns.bin", 
                "/home/mauro/datos/utn/dlc/DLC-Buscador/web/test_voc.bin", 
                "/home/mauro/datos/utn/dlc/DLC-Buscador/web/test_post.bin");
        
        Iterable<SearchResult> results;
        String txt = request.getParameter("search_words");
        if (txt != null && !txt.trim().isEmpty()) {
            HashSet<String> words = new HashSet(WordReader.parseLine(txt));
            results = im.getResults(words);
            
        } else {
            results = null;
        }
        
          
        
        request.setAttribute("results", results);
        request.setAttribute("search_words", txt);
        ServletContext app = this.getServletContext();
        RequestDispatcher disp = app.getRequestDispatcher("/results.jsp");
        disp.forward(request, response);
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
