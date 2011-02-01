<%@include file="/components/taglibs.jsp" %>
<%@page isELIgnored="false"%>
<head>
    <title>validate</title>
</head>
<body>
    <form id="openidForm" method="POST" action="/o/a">
        <label for="identifier">Identifier:</label>
        <input type="text" id="identifier" name="identifier" value="${id}" size="50" />
        <br />
        <input type="submit" id="verify" value="Verify" />
    </form>
    <c:if test="${id != null}">
        Validated ID: 
        <span class="openID"><c:out value="${id}" /></span>
    </c:if>
    <c:if test="${attributes != null}">    
        <c:forEach var="entry" items="${attributes.entrySet}">
            <c:out value="${entry.key}" />: <c:out value="${entry.value}" />
        </c:forEach>
    </c:if>
    <div id="error"><c:out value="${error}" /></div>
</body>