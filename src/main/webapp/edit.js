//globals
//var BASE_URL= "http://fmtmac-bookmarks.herokuapp.com"; // "http://localhost:8080/bookmarks";	//
var BASE_PASS_URL= "http://password.fmtmac.cloudbees.net"; //"http://localhost:8080/password";
var BASE_BOOK_URL= "http://fmtmac-bookmarks.herokuapp.com"; //"http://localhost:8080/bookmarks"; //
//var BASE_BOOK_URL="http://localhost:8080/bookmarks"; //
/*var user= $.urlParam('user');
var password= $.urlParam('password');
var page= $.urlParam('page');

** gets a parameter from the URL 
used to get userName/accountName for this page
**
$.urlParam = function(name){
    var results = new RegExp('[\\?&]' + name + '=([^&#]*)').exec(window.location.href);
    return results[1] || 0;
}*/

/**
* determines whether element is null.
* params:
* 	e- element to check if null
**/
function isNull(e) {
  return (!e || typeof e === 'undefined');
}
/**
* determines whether element is Blank.
* params:
* 	e- element to check if null
**/
function isBlank(e) {
  return (!e || typeof e === 'undefined' || e.length === 0);
}
String.prototype.endsWith = function(str) {return (this.match(str+"$")==str)}
String.prototype.startsWith = function(str) {return (this.match("^"+str)==str)}
//------------------------------------------------------------------------------------
/**
* adds, edits and deletes links, paragraphs and pages.
* params:
*	popupWindow - reference to popupWindow (to close)
*	link_name - name of link
*	link_url_from_user - URL link
*	username - username
*	pageName - name of page to add link to
*	paragraphPosition - which paragraph to add link to
*	paragraphName - name of paragraph to add link to
*	linkPosition- which link in paragrapgh to add link before
*	action- action to perform("ADD/EDIT/DELETE PG/PAGE/LINK[FROMTOOLBAR]
*	format- in which format to display list of bookmarks
**/
function addLink(popupWindow, link_name, link_url_from_user, username, pageName, paragraphPosition, linkPosition, action, paragraphName, format) {
	var password= "";
	console.log("link_url_from_user: "+ link_url_from_user);
	var link_url= escape(link_url_from_user);
	console.log("link_url: "+ link_url);
	console.log("action: "+ action);
	
	var confirmed= true;
	var mainWin= window;
	if(action.startsWith("ADD") || action.startsWith("EDIT")) {
		mainWin= window.opener;
	}
	
	if(action.startsWith("DELETE")) {
		confirmed=confirm('Are You Sure You Want to Delete This?');
	}
	
	if(confirmed) {
		console.log("addLink("+link_name+", "+link_url+", "+username+", "+pageName+", "+paragraphPosition+", "+linkPosition+", "+action+", "+paragraphName+", "+format+")");

		if(!isBlank(action)) {
			if(action.endsWith("PG")) {
				console.log("PG");
				mainWin.location.replace("./links?edit=true&user="+username+"&page="+pageName+"&action="+action+"&paragraph_name="+link_name+"&old_paragraph_name="+paragraphName+"&paragraph_pos="+paragraphPosition);
			}
			else if(action.endsWith("PAGE")) {
				console.log("PAGE");
				mainWin.location.replace("./page?user="+username+"&page="+link_name+"&action="+action+"&oldPageName="+pageName);
			}
			else if(action.endsWith("LINK")) {
				console.log("LINK");
				if(action.indexOf("FROMTOOLBAR") != -1) {
					console.log("FROMTOOLBAR");
					paragraphPosition= parseInt(paragraphName.substr(paragraphName.indexOf("---")+ 3));
					paragraphName= paragraphName.substr(0, paragraphName.indexOf("---"));
					addLinkWebService(popupWindow, username, password, pageName, link_url, link_name, paragraphName, paragraphPosition, linkPosition);
					//mainWin.location.replace(BASE_BOOK_URL+ "/links?edit=true&user="+username+"&page="+pageName+"&action="+action+"&link_url="+link_url+"&link_name="+link_name+"&paragraph_name="+paragraphName+"&oldPageName="+pageName+"&paragraph_pos="+paragraphPosition+"&link_pos="+linkPosition);
				} else {
					mainWin.location.replace("./links?edit=true&user="+username+"&page="+pageName+"&action="+action+"&link_url="+link_url+"&link_name="+link_name+"&paragraph_name="+paragraphName+"&oldPageName="+pageName+"&paragraph_pos="+paragraphPosition+"&link_pos="+linkPosition+"&format="+format);
					
					if(!isNull(popupWindow)) {
						popupWindow.close();
					}
				}
			} else {
				console.log("Not PAGE, PG, or LINK")
			}
		}
		

	}
}

/**
creates an account.
called when user clicks on "Crerate Account" button.
param: accountName accountName/userName for account
		password password for accountName
**/
function createAccount(accountName, password) {
$('div.accountFeedback').html('<p>verifying account...</p>');

/**
checks whether account already exists, using non-blocking API.
params: accountName accountName/userName for account
		password password for accountName
**/
accountExists(accountName, password, function(){
	$('div.accountFeedback').html('<p style=\"color: red;\">Account Name already in use.  Please try another');
	//setTimeout("$('div.accountFeedback').html('<p style=\"color: red;\">Account Name already in use.  Please try another')", 500);
}, function(){
	uuu= "$('div.accountFeedback').html('<p style=\"color: darkgreen;\">Successfully created account, '+accountName+'!<br/>Bookmark this url to quick account access: <a href=\"./daily.htm?accountName='+accountName+'\">./daily.htm?accountName='+accountName+'</a></p>')";
	//setTimeout(uuu, 500);

	uuu= "login(\'"+accountName+"\', \'"+password+"\')";
	setTimeout(uuu, 5000);
});
}

/** returns whether an account exists.
blocking-api
params: accountName accountName/userName for account
		password password for accountName
		yesFunction, noFunction: functions to call depending on whether account exists
**/
function accountExists(accountName, password, yesFunction, noFunction) {
	var passUrl= BASE_PASS_URL+ "/rest/password?username="+accountName+"&site=dailybalance-js&password="+password+"&action=GET";

	$.get(passUrl,function(data, txtstatus, xbr){
		//alert("accountExists, .get() success: "+data);  
		var jData= eval(data);
		//alert("jData.status: "+jData.status+jData);			
		if(jData.status === "success" || (typeof jData.message === 'undefined' || !jData.message || -1 != jData.message.indexOf("no username"))) {
			//alert("role: "+ jData.role);
			//alert("status: "+ jData.status + ": "+ jData.message);

			if(jData.role === "user") {  
				yesFunction();
			} else {
				//alert(accountName+ " Created: "+ jData.message);
				noFunction();
			}

		} else {
			noFunction();	//alert(accountName+ " not found");
			//alert("error-accountExists: "+ jData.status);
		}
	});
}

/**
logs user in.
called when user clicks "Login" button.
params: accountName accountName/userName for account
		password password for accountName
**/
function login(accountName, password) {
	$('div.accountFeedback').html('<p>verifying account...</p>');
	
	accountExists(accountName, password, function(){window.location = './page?user='+ accountName;}, function(){$('div.accountFeedback').html('<p style=\"color: red;\">Account not found');})
	/*if(accountExists(accountName, password)) {
		//alert('going to login');
		window.location = './daily.htm?accountName='+ accountName;
	} else {
		$('div.accountFeedback').html('<p style=\"color: red;\">Account not found');
	}*/
}

/**
* adds, edits and deletes links, paragraphs and pages using REST-WS call.
* params:
*	popupWindow - reference to popupWindow (to close)
*	linkName - name of link
*	linkUrl - URL link
*	user - username
*	password password for user
*	pageName - name of page to add link to
*	pgPos - which paragraph to add link to
*	paragraphName - name of paragraph to add link to
*	linkPos- which link in paragrapgh to add link before
*	action- action to perform("ADD/EDIT/DELETE PG/PAGE/LINK[FROMTOOLBAR]
*	format- in which format to display list of bookmarks
**/
function addLinkWebService(popupWindow, user, password, pageName, linkUrl, linkName, paragraphName, pgPos, linkPos) {
	//var passUrl= BASE_BOOK_URL+ "/links?edit=true&user="+user+"&password="+password+"&page="+pageName+"&action=ADDLINK&link_url="+linkUrl+"&link_name="+linkName+"&paragraph_name=%s"+paragraphName+"oldPageName=&paragraph_pos="+pgPos+"&link_pos="+linkPos+"&desktop=false&format=blocks";
	var passUrl= BASE_BOOK_URL+ "/rest/data/?password="+password+"&user="+user+"&page="+pageName+"&site=com.fmt.bookmarks&action=GET"+"&paragraphPosition="+pgPos+"&linkPosition="+linkPos+"&paragraphName="+paragraphName+"&linkName="+linkName+"&linkUrl="+linkUrl;
	
	$.get(passUrl,function(data, txtstatus, xbr){
		//alert("accountExists, .get() success: "+data);  
		var jData= eval(data);
		//alert("jData.status: "+jData.status+jData);			
		if(jData) {
			if(-1 == data.indexOf("fail")) {
				alert("added Bookmark");
				
				if(!isNull(popupWindow)) {
					popupWindow.close();
				}
			} else {
				alert("Bookmark failed");
			}

		} else {
			//noFunction();	//alert(accountName+ " not found");
			alert("error-addLink: "+ jData);
		}
	});
}

/**
* gets paragraph names using REST-WS, then adds their names to web page.
* params:
*	user - username
*	password password for user
*	pageName - name of page to get paragraph names from
**/
function getAndChangeParagraphs(user, password, pageName) {
	var passUrl= BASE_BOOK_URL+ "/rest/data/?password="+password+"&user="+user+"&page="+pageName+"&site=com.fmt.bookmarks&action=GET";

	$.get(passUrl,function(data, txtstatus, xbr){
		//alert("accountExists, .get() success: "+data);  
		var jData= eval(data);
		//alert("jData.status: "+jData.status+jData);			
		if(jData) {
			
			changeParagraphs(jData);

		} else {
			//noFunction();	//alert(accountName+ " not found");
			alert("error-getParagraphs: "+ jData);
		}
	});
}

function getAndChangeParagraphLinks(user, password, pageName, paragraphName) {
	var passUrl= BASE_BOOK_URL+ "/rest/data/?password="+password+"&user="+user+"&page="+pageName+"&paragraphName="+paragraphName+"&site=com.fmt.bookmarks&action=GET";

	$.get(passUrl,function(data, txtstatus, xbr){
		//alert("accountExists, .get() success: "+data);  
		var jData= eval(data);
		//alert("jData.status: "+jData.status+jData);			
		if(jData) {
			
			changeParagraphLinks(jData);

		} else {
			//noFunction();	//alert(accountName+ " not found");
			alert("error-getParagraphs: "+ jData);
		}
	});
}

/** adds list of paragraph names to "paragraphname" list.
* params: pgNames- names of all paragraphs
**/
function changeParagraphs(pgNames) {
	var options= "";
	
    for(var pgName in pgNames)
    {
    	//options += "<option value=" + pgNames[pgName].substr(0,pgNames[pgName].indexOf("---")) + ">"+ pgNames[pgName].substr(0,pgNames[pgName].indexOf("---"))+ "</options>";
    	options += "<option value=" + encodeURIComponent(pgNames[pgName]) + ">"+ pgNames[pgName].substr(0,pgNames[pgName].indexOf("---"))+ "</options>";
    }

	$("#paragraphname").html(options);
}

function changeParagraphLinks(pgNames) {
	var options= "";
	
	var idx= 0;
    for(var pgName in pgNames)
    {
    	//options += "<option value=" + pgNames[pgName].substr(0,pgNames[pgName].indexOf("---")) + ">"+ pgNames[pgName].substr(0,pgNames[pgName].indexOf("---"))+ "</options>";
    	options += "<option value=" + idx++ + ">"+ pgNames[pgName]+ "</options>";
    }

	$("#paralinkname").html(options);
}
