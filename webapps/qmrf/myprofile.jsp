<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>


	<c:choose>
	<c:when test="${empty sessionScope['username']}">
			<c:redirect url="protected.jsp"/>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'false'}">
		<c:if test="${(!empty param.user_name) and (sessionScope['username'] != param.user_name)}">
			<c:redirect url="protected.jsp"/>
		</c:if>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'true'}">

	</c:when>
	<c:otherwise>
		<c:if test="${(!empty param.user_name) and (sessionScope['username'] != param.user_name)}">
			<c:redirect url="protected.jsp"/>
		</c:if>
	</c:otherwise>
</c:choose>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Inventory: User' profile"/>
</jsp:include>


<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="profile"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<c:set var="update" value=""/>
<c:if test="${!empty param.user_name}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.title}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.firstname}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.lastname}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.address}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.country}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.webpage}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.affiliation}">
	<c:set var="update" value="ok"/>
</c:if>
<c:if test="${!empty param.keywords}">
	<c:set var="update" value="ok"/>
</c:if>
<c:set var="areviewer" value="0"/>
<c:if test="${param.reviewer eq 'Yes'}">
	<c:set var="update" value="ok"/>
	<c:set var="areviewer" value="1"/>
</c:if>


<c:if test="${!empty update}">

		<c:catch var='exception'>
		<sql:update var="rs" dataSource="jdbc/qmrf_documents">
			update users set title=?,firstname=?,lastname=?,address=?,country=?,affiliation=?,webpage=?,keywords=?,reviewer=?,registration_date=registration_date where user_name=?

		  <sql:param value="${param.title}"/>
		  <sql:param value="${param.firstname}"/>
			<sql:param value="${param.lastname}"/>
			<sql:param value="${param.address}"/>
			<sql:param value="${param.country}"/>
			<sql:param value="${param.affiliation}"/>
			<sql:param value="${param.webpage}"/>
			<sql:param value="${param.keywords}"/>
			<sql:param value="${areviewer}"/>
			<sql:param value="${param.user_name}"/>


		</sql:update>
		</c:catch>
		<c:if test="${!empty exception}">
			<div class="error">${exception}</div>
		</c:if>
		<c:choose>
		<c:when test='${not empty exception}'>
						<div class="error">
									error ${exception}
						</div>
		</c:when>
		<c:otherwise>
		</c:otherwise>
		</c:choose>
</c:if>


<c:catch var="error">
	<c:import var="xml" url="users_xml.jsp"/>
	<c:set var="admin" value="${sessionScope.ismanager && (sessionScope.viewmode eq 'qmrf_manager')}"/>	
	<c:if test="${admin}">	
		<c:import var="xsl" url="/WEB-INF/xslt/users2html.xsl"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${fn:trim(xsl)}">
			<x:param name="header" value="Registered users in QMRF Inventory:"/>
		</x:transform>	
	</c:if>
	<c:import var="xsl" url="/WEB-INF/xslt/users2form.xsl"/>
	<x:transform xml="${fn:trim(xml)}" xslt="${fn:trim(xsl)}">
		<x:param name="admin" value="${admin}"/>
	</x:transform>
</c:catch>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true"/>

	</p>
</div>
</body>
</html>
