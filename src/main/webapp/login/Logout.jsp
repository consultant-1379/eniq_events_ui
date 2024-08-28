<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  	<% request.getSession(true).invalidate(); %>
<script language="JavaScript">
function Load() {
	window.location = "<%= application.getContextPath() %>";
}
</script>
<body onload='Load()'>
</html>