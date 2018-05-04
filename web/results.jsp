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
        <title>Gugle</title>
    </head>
    <body>
        <div>
            <h1><img src="img/logo.png" alt="Gugle"></h1>
            <form action="." method="post">
                <input type="text" name="texto_a_buscar" autofocus>
                <input type="submit" name="btn_buscar" value="Buscar">
            </form>
        </div>
        <div>
            <p>
                <c:forEach items="${results}" var="result">
                        ${result}<br>
                </c:forEach> 
            </p>
        </div>
    
    </body>
</html>
