<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0//EN">
<!-- login form and description page. -->
<html>
<head>

<head>	
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=0.75">
	<title>Login Page for Bookmarks</title>
	<link rel="shortcut icon" href="./icon.ico"/>

	<link rel="stylesheet" href="./jquery/jquery.mobile.css" />
	<script src="./jquery/jquery.js"></script>
	
	 <!-- <script src="./jquery/jquery.js"></script>
	<script src="./jquery/jquery.mobile.js"></script>

	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.css" />
	<script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.js"></script>

	<link rel="stylesheet" href="http://code.jquery.com/mobile/latest/jquery.mobile.min.css" />
	<script src="http://code.jquery.com/jquery-latest.min.js"></script>
	<script src="http://code.jquery.com/mobile/latest/jquery.mobile.js"></script>

	<script src="./edit.js"></script> -->
</head> 
<body bgcolor="white">
	<!-- form method="GET" action="j_security_check"> -->
	<h2>Simple Bookmarks</h2>
	<form method="GET" action="login">
		<table>
			<tr>
				<th align="right">Username:</th>
				<td align="left"><input type="text" name="j_username"></td>
			</tr>
			<tr>
				<th align="right">Password:</th>
				<td align="left"><input type="password" name="j_password"></td>
			</tr>
			<tr>
				<!-- input type="hidden" name="role_name" value="user" / -->
				<input type="hidden" name="site_name" value="com.fmt.bookmarks" />
				<td align="right"><input type="submit" value="Login"></td>
				<td align="left"><input type="reset"></td>
			</tr>
		</table>
	</form>
	<br/>
	<a href="./create-account.htm">Create Account</a>
	<br/>
	<hr/>
	<br/>
	<ul>
	<h4>The Simplest Bookmarking Site Possible</h4>
<li>Simply Save and Access all Your Bookmarks on Your Web Browser Screen.</li>
<li>Bookmarks Pages can be <a href="http://localhost:8080/bookmarks/plinks?user=ftaylor92&page=j2ee&edit=false&format=straight&private=false">public</a> links or Private only for you.</li>
<li>Organize Multiple Pages of Bookmarks.  Organize Links by blocks of &quot;<a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=software&edit=false&format=straight&private=false">Paragraphs</a>&quot;</li>
<li>Then Display Bookmarks as either <a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=software&edit=false&format=straight&private=false">Straight List</a>, <a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=WS&edit=false&format=trapese&private=false">Trapeze</a> format or as Blocks of Link &quot;<a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=software&edit=false&format=blocks&private=false">Paragraphs</a>&quot;.</li>
<li>Edit, View, <em>Bookmark</em> and Share Your Bookmarks easily from Site.</li>
<li>Always <em>Free</em>.  Always <em>Ad-Free</em>.  Always quickly accessible with one click.</li>
<li>View From any Browser, including Your Mobile Phone</li>
<li>or Try it as a native <a href="http://play.google.com/store/apps/details?id=com.fmt.bookmarks.complete&feature=more_from_developer">Android App <img src="http://www.logoeps.com/wp-content/uploads/2011/06/android-robot-vector.jpg" height="42" width="42" /></a></li>
</ul>
If you have any comments or issues, please email me at <a href="mailto:ftaylor92@bc.edu">ftaylor92@bc.edu</a><br/>

<h5>Block, Straight or Trapeze formats</h5>
<a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=software&edit=false&format=blocks&private=false"><img src="./img/blocks.png" /></a>
<a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=software&edit=false&format=straight&private=false"><img src="./img/straight.png" /></a>
<a href="https://fmt-bookmarks.herokuapp.com/plinks?user=ftaylor92&page=WS&edit=false&format=trapese&private=false"><img src="./img/trapese.png" /></a><br/>

<h5>Multiple Pages</h5>
<img src="./img/pages.png" /><br/>
<h5>Edit Links;  Organize Links by &quot;Paragraphs&quot;</h5>
<img src="./img/editscreen.png" /><br/>

<script>
	$(document).ready(function() {
		$.ajaxSetup({ cache: false, contentType: "application/json; charset=utf-8", dataType:"json"});

		var counterUrl="https://fmt-bookmarks.herokuapp.com/rest/counter?site="+ encodeURIComponent(window.location.href);
		$.get(counterUrl, function(data, txtstatus, xbr) {
			$("#counter").html("<br/><br/><small><em>count: "+ data+"</em></small>");
		});
	});
</script>
<div id='counter'></div>
<div id='cloudbees'>
<hr/>
<a href="http://www.cloudbees.com"><img src="http://cloudbees.prod.acquia-sites.com/sites/default/files/styles/large/public/Button-Built-on-CB-1.png?itok=3Tnkun-C" /></a>
</div>
</body>
</html>
