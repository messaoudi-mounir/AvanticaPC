<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Init</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">

    
    <script   src="https://code.jquery.com/jquery-3.1.0.min.js" integrity="sha256-cCueBR6CsyA4/9szpPfrX3s49M9vUU5BgtiJj06wt/s=" crossorigin="anonymous"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
	
	<script type="text/javascript" src="js/amq_jquery_adapter.js"></script>
	<script type="text/javascript" src="js/amq.js"></script>
	<script type="text/javascript" src="js/stomp.js"></script>
	
<body>
<div style="margin:20px">
	<form id="sendForm">
		<input type="text" id="inputText" name="inputText" class="form-control" style="display: inline-block;width: 300px;margin: 3px;">
		<button type="submit" class="btn btn-primary">Send</button>
	</form>
	<input type="text" id="inputQueue" class="form-control" style="display: inline-block;width: 300px;margin: 3px;">
	<button class="btn btn-primary" style="width: 59px;">Get</button>
	<table id="QueueTable" class="table table-hover">
		<thead>
			<tr>
				<th>Index</th>
				<th>Value</th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>
	<script>
	$('#sendForm').submit(function(e){
	    e.preventDefault();
	    $.ajax({
	        url:'./send.jsp',
	        type:'post',
	        data:$('#sendForm').serialize(),
	        success:function(){
	        	//alert("Sent");
	            //whatever you wanna do after the form is successfully submitted
	        }
	    });
	});
	</script>
	<script type="text/javascript">
		 var client = Stomp.client( "ws://localhost:61614/stomp", "v11.stomp" );
		 client.connect( "", "",
		  function() {
		      client.subscribe("testQueu2",
		       function( message ) {
		    	  var obj = JSON.parse(message.toString().split("\n")[9]);
		           console.log("Message: "+message.toString().split("\n")[9]);
		           /*$('#QueueTable tr:last').after("<tr><td>"+obj.type+"</td><td>"+obj.value+"</td></tr>");*/
		        // Find a <table> element with id="myTable":
		           var table = document.getElementById("QueueTable");

		           // Create an empty <tr> element and add it to the 1st position of the table:
		           var row = table.insertRow(1);
		           // Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:
		           var cell1 = row.insertCell(0);
		           var cell2 = row.insertCell(1);

		           // Add some text to the new cells:
		           cell1.innerHTML = obj.type;
		           cell2.innerHTML = obj.value;  
		           
		           if(obj.type == 1){
		        	   row.className="success";
		           } else {
		        	   row.className="danger";
		           }
		      }, 
		    { priority: 9 } 
		      );
		   client.send("jms.topic.test", { priority: 9 }, "Pub/Sub over STOMP!");
		  }
		 );
 </script>

</body>
</html>