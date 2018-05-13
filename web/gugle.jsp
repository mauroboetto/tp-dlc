<%-- 
    Document   : results
    Created on : Apr 17, 2018, 12:14:05 PM
    Author     : mauro
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/custom.css" rel="stylesheet" type="text/css" >
        <title>Gugle</title>
    </head>
    <body>  
        <div class="search">
            <h1><img src="img/logo.png" alt="Gugle" class="lineBlock"></h1>
            <form action="." method="post">
                <input type="text" name="search_words" class="lineBlock" value="${search_words}" autofocus>
                <input type="submit" name="btn_buscar" class="lineBlock" value="Buscar">
            </form>
        </div>
        ${test}
        <div class="results">
            <c:choose>
                <c:when test="${results != null && results.size() > 0}">
                    <p>Resultados: ${results.size()}</p>
                    <c:set var="index" value="1" scope="page" />
                    <c:forEach items="${results}" var="result">
                        <div class="result">
                            <br>${index} - <a href="${documents_dir}/${result.filename}">${result.filename}</a>
                            <pre>${result.info}</pre>
                        </div>
                        <c:set var="index" value="${index + 1}" scope="page" />
                    </c:forEach> 
                </c:when>
                <c:otherwise>
                    <div class="result">
                        <p>Sin resultados.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    
    </body>
</html>
