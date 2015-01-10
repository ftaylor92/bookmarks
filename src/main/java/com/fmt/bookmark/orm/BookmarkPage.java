package com.fmt.bookmark.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * custom ORM to convert database table into a populated POJO.
 **/
public class BookmarkPage {
	/** all paragraphs on a page. **/
	public List<Paragraph> paragraphs= new ArrayList<>();
	
	public String name;

	/** given a result from a SQL query, populate this object. 
	 * @param resultset results from SQL query
	*
	public void populate(ResultSet resultset) throws SQLException {
		paragraphs= new ArrayList<>();
		
		Paragraph aParagraph= new Paragraph();
		while(resultset.next()) {
			int para_pos= resultset.getInt("paragraph_position");
			if(aParagraph.position != para_pos) {
				if(-1 != aParagraph.position) {
					paragraphs.add(aParagraph);
				}
				aParagraph= new Paragraph(para_pos, resultset.getString("paragraph_name"));
			}
			aParagraph.linx.add(new Link(resultset.getInt("position"), resultset.getString("link"), resultset.getString("link_name")));
		}
		if(-1 != aParagraph.position) {
			paragraphs.add(aParagraph);
		}
	}*/
		
	/**
	 * inner class, describes a paragraph.
	 **/
	public static class Paragraph {
		/** position of paragraph in page. **/
		final public int position;
		/** name of paragraph. **/
		final public String name;
		/** all links in this paragraph. **/
		final public List<Link> linx; 
		final public int id;
		
		/** Constructor.  **/
		public Paragraph() { 
			position= -1;
			linx= new ArrayList<>();
			this.name= "unk";
			this.id= -1;
		}
		
		/** Constructor.
		 * @param position position of this paragraph on the page.
		 * @param name name of this paragraph
		**/
		public Paragraph(int id, int position, String name) { 
			this.position= position;
			this.name= name;
			this.id= id;
			linx= new ArrayList<>();
		}

	}
	
	/**
	 * inner class, describes a link.
	 **/
	public static class Link {
		/** position of link in paragraph. **/
		final public int position;
		/** link URL. **/
		final public String link;
		/** name of link. **/
		final public String name;

		/** Constructor. 
		 * @param position position of link in paragraph
		 * @param link URL of link
		 * @param name name of link
		**/
		public Link(int position, String link, String name) {
			this.position= position;
			this.link= link;
			this.name= name;
		}
		
		/** Constructor. **/
		public Link() {
			position= -1;
			this.link= "-";
			this.name= "unk";
		}
	}
	
	public static class User {
		final public int id;
		final public String user_name;
		final public String role_name;
		final public String site_name;
		final public String password;
		public List<Page> pages= null;
		
		public User(int id, String user_name, String role_name, String site_name, String password) {
			super();
			this.user_name = user_name;
			this.role_name = role_name;
			this.site_name = site_name;
			this.password = password;
			this.id= id;
		}
		
		
	}
	
	public static class Page {
		public Page(int id, User user, String name) {
			super();
			this.id = id;
			this.user = user;
			this.name = name;
		}
		final public int id;
		public final User user;
		final public String name;
		public List<Paragraph> pages= null;
	}
}
