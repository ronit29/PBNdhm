<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
<body>
<!-- <input type = button onclick="http://localhost:8080/coreservice/socialAuth/google">Sign In with Google</button> 
<button type = "button " onclick="http://localhost:8080/coreservice/socialAuth/facebook">Sign In with Facebook</button> 
 <a href="http://localhost:8080/coreservice/socialAuth/google"><img src="C:/Users/Kanupriya/Downloads/facebook.ico"></img></a> -->

<!-- <a href="http://localhost:8080/coreservice/gPluslogin?provider=google">Sign In with Google</a> -->

<!-- <a href="http://localhost:8080/coreservice/fbLogin?provider=facebook">Sign In with Facebook</a> -->


<a href="<%=request.getContextPath()%>/socialAuth/google"><input type="button" value='Sign In with Google'></a> &nbsp;&nbsp;&nbsp;&nbsp;

<img src="C:/Users/Kanupriya/Downloads/facebook.ico"/>
<a href="<%=request.getContextPath()%>/socialAuth/facebook"><input type="button" value='Sign In with Facebook'></a>
</body>
</html>
