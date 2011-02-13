<%@include file="/components/taglibs.jsp" %>
<%@page isELIgnored="false"%>
<head>
    <title>validate</title>
</head>
<body>
    <form id="openidForm" method="POST" action="/o/a">
        <label for="identifier">Identifier:</label>
        <input type="text" id="identifier" name="identifier" value="${id}" size="50" tabindex="0" />
        <br />
        <input type="submit" id="verify" value="Verify" />
    </form>
    <table id="response">
	    <c:if test="${id != null}">
	        <tr><th>Validated ID: </th> 
	            <td class="openID"><c:out value="${id}" /></td>
	        </tr>
	    </c:if>
	    <c:if test="${attributes != null}">
	        <c:forEach var="entry" items="${attributes}">
	            <tr><th><c:out value="${entry.key}" />: </th>
                    <td><c:out value="${entry.value}" /></td>
                </tr>
	        </c:forEach>
	    </c:if>
    </table>
    <div id="error"><c:out value="${error}" /></div>
</body>