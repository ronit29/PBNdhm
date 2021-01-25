<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> --%>
<%@ page session="false"%>
<html>
<head>
<!-- <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"> -->
<title>Upload File Request Page</title>
</head>
<body>

	<form method="POST"
		class="com.policybazaar.coreservice.web.controller.DocumentStoreController"
		action="doc/upload" enctype="multipart/form-data">
		<br /> File to upload: <input type="file" name="file">
		<br /> customerId: <input type="text" name="customerId"><br /> 
		<br /> docCategory: <input type="text" name="docCategory"><br />
		<br /> docType: <input type="text" name="docType"><br />
		<br /> leadId: <input type="text" name="leadId"><br />
		<br /> createdBy: <input type="text" name="createdBy"><br />
		<br /> fileName: <input type="text" name="fileName"><br />
		<input type="submit" value="Upload"> Press here to upload the file!

	</form>

</body>
</html>