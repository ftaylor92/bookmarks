<!-- edits or adds link page.  To be used in pop-up window. -->
<html>
	<head>	
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=0.75">
		<title>Edit</title> 
		<link rel="stylesheet" href="./jquery/jquery.mobile.css" />
		<script src="./jquery/jquery.js"></script>
		<script src="./jquery/jquery.mobile.js"></script> <!--
 
		<link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.css" />
		<script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
		<script src="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.js"></script>

		<link rel="stylesheet" href="http://code.jquery.com/mobile/latest/jquery.mobile.min.css" />
		<script src="http://code.jquery.com/jquery-latest.min.js"></script>
		<script src="http://code.jquery.com/mobile/latest/jquery.mobile.js"></script> -->

		<script src="./edit.js"></script>
	</head> 
<body>
<script>
	$(document).ready(function() {
		$.ajaxSetup({ cache: false, contentType: "application/json; charset=utf-8", dataType:"json"});

		//$('#linkName').val(document.userAccounts.current.accounts[0].balance);
	});
</script>

<div style="width: 500px;" data-role="fieldcontain"><label for="linkName">Link Name:</label><input type="text" name="linkName" id="linkName" class="linkName" /></div>
<div style="width: 500px;" data-role="fieldcontain"><label for="linkUrl">Link URL:</label><input type="url" name="linkUrl" id="linkUrl" class="linkUrl" /></div>
<button style="width: 100px;" data-inline="true" onclick="addLink($('input:text[name=linkName]').val(), $('input[name=linkUrl]').val(), 'PUT');">Add</button>
</body>
</html>