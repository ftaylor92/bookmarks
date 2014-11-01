package com.fmt.bookmark.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * custom ORM to convert database table into a populated POJO.
 **/
public class BookmarkTable {
	/** all paragraphs on a page. **/
	public List<Paragraph> paragraphs= new ArrayList<BookmarkTable.Paragraph>();

	/** given a result from a SQL query, populate this object. 
	 * @param resultset results from SQL query
	**/
	public void populate(ResultSet resultset) throws SQLException {
		paragraphs= new ArrayList<BookmarkTable.Paragraph>();
		
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
	}
		
	/**
	 * inner class, describes a paragraph.
	 **/
	public static class Paragraph {
		/** position of paragraph in page. **/
		public int position;
		/** name of paragraph. **/
		public String name;
		/** all links in this paragraph. **/
		public List<Link> linx; 
		
		/** Constructor.  **/
		public Paragraph() { 
			position= -1;
			linx= new ArrayList<BookmarkTable.Link>();
		}
		
		/** Constructor.
		 * @param position position of this paragraph on the page.
		 * @param name name of this paragraph
		**/
		public Paragraph(int position, String name) { 
			this.position= position;
			this.name= name;
			linx= new ArrayList<BookmarkTable.Link>();
		}

	}
	
	/**
	 * inner class, describes a link.
	 **/
	public static class Link {
		/** position of link in paragraph. **/
		public int position;
		/** link URL. **/
		public String link;
		/** name of link. **/
		public String name;

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
		}
	}
}
