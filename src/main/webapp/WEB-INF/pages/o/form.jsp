<%@include file="/components/taglibs.jsp" %>
<%@page isELIgnored="false"%>
<head>
    <title>OpenID Tester</title>
</head>
<body>
    <form id="openidForm" method="POST" action="/o/a">
        <label for="identifier">Identifier:</label>
        <input type="text" name="identifier" />
        <input type="submit" value="Verify" />
    </form>
</body>