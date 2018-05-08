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
                <input type="text" name="search_words" class="lineBlock" autofocus>
                <input type="submit" name="btn_buscar" class="lineBlock" value="Buscar">
            </form>
        </div>
        <div class="result">
            <p>
                <c:forEach items="${results}" var="result">
                        ${result}<br>
                </c:forEach> 
            </p>
        </div>
    
    </body>
</html>
