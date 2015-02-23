<%@ taglib uri=http://java.sun.com/jstl/core prefix="c" %>

<html>
    <head>
        <title>Search Results!</title>
    </head>
    <body>
        <ol>
            <c:forEach var="result" items="${results}">
                <li>
                    <p>ItemID: <span>${result.getItemID}</span>
                       Name: <span>${result.getName}</span>
                    </p>
                </li>
            <c:forEach>
        </ol>
    </body>
</html>
