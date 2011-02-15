<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@include file="/components/taglibs.jsp" %>
<html>
<head>
	<title>OpenID Tester | <decorator:title /></title>
	<meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="content-script-type" content="text/javascript">
    <link rel="icon" href="/img/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="/css/main.css"/>
	<decorator:head />
</head>
<body	<decorator:getProperty property="body.id" writeEntireProperty="true"/>
		<decorator:getProperty property="body.class" writeEntireProperty="true"/>>
	<div id="topbar">
		<div id="toplogo">
			<a href="/"><img src="/img/logo_openid2.png" /></a>
		</div>
		<div id="toplinks">
			<a href="/about">about</a>
		</div>
	</div>
	<div id="main">
		<decorator:body />
	</div>
	<div id="bottombar"></div>
</body>
</html>