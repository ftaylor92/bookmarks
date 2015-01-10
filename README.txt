heroku:

java 1.8 52.0 version problem


mvn clean package && heroku deploy:war --war target/*.war

------------------------
cloudbees:

svn co --username=ftaylor92 https://svn-fmtmac.forge.cloudbees.com/bookmarks/

ToDo:

Testing:
-Test Pages
-Test Paragraphs
-Test Links
-Test noedit finished page
-Test noedit finished page workFlowy
-Test noedit finished page ceoExpress
-Test add, move, edit, delete (Page, Paragraph, link)

http://tinyurl.com/simple-bookmarks == https://fmt-bookmarks.herokuapp.com
see counter.html for adding counter to any HTML page

https://fmt-bookmarks.herokuapp.com/page?user=ftaylor92

http://localhost:8080/bookmarks
http://localhost:8080/bookmarks/rest/secure
http://localhost:8080/bookmarks/rest/hello
http://localhost:8080/bookmarks/page
http://localhost:8080/bookmarks/page?user=ftaylor92
http://localhost:8080/bookmarks/links?user=ftaylor92&page=mylinx&edit=false
http://localhost:8080/bookmarks/links?user=ftaylor92&page=mylinx&edit=true&format=squares

http://localhost:8080/bookmarks/edit?link_name=linkname&link_url=likUrl&user=ftaylor92&page=&password=null&paragraph_pos=&link_pos=0&action=ADDFROMTOOLBARLINK&paragraph_name=

curl -v http://localhost:8080/bookmarks/rest/hello
curl -v http://localhost:8080/bookmarks/rest/secure
curl -v -d name=foo http://localhost:8080/bookmarks/rest/secure/contacts
curl -X PUT http://localhost:8080/bookmarks/rest/secure/1
curl -X DELETE http://localhost:8080/bookmarks/rest/secure/1
@HEAD fails.

ToDo:
format for paragraphs: serial or squares -> 3 column table
add, delete page
add, delete paragraph
add, edit, delete linx
REST:
PUT, DELETE
Full user, password, role, site-name suite

sed 's/<a href=\"\(.*\)\">\(.*\)<\/a>[ ]*|[ ]*/insert into bookmark_linx (link,link_name,paragraph_name,page_name,user,position,paragraph_position) values (\"\1\",\"\2\",\"fun\",\"mylinx\",\"ftaylor92\",1,1);/g' software.html > temp.sql

ToDo:
- mobile web app
- shortcut/links to cloud or phone/Windows
- make functions == webService
- open all links in paragraph at once
