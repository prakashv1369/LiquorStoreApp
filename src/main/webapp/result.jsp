<%@ page import ="java.util.*" %>
<!DOCTYPE html>
<html>
<body>
<center>
<h1>
    Available Brands
</h1>
<%
out.println("FILE ID : " + request.getAttribute("FID"));
//List result= (List) request.getAttribute("brands");
//Iterator it = result.iterator();
out.println("<br>We have <br><br>");

/* while(it.hasNext()){

out.println(it.next()+"<br>");

}
 */
%>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	OpenFile();
});
function OpenFile() { 
    window.open("https://docs.google.com/spreadsheets/d/<%=request.getAttribute("FID")%>", "_blank"); 
} 
</script>
</body>
</html>