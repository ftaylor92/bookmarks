package com.fmt.database;

import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.fmt.bookmark.orm.BookmarkPage;
import com.fmt.bookmark.orm.BookmarkPage.Page;
import com.fmt.bookmark.orm.BookmarkPage.Paragraph;
import com.fmt.bookmark.orm.BookmarkPage.User;

public class CopyMySqlToPostgres {

	public static void main(String[] args) {
		final Connection pg= HerokuConnection.getConnection();
		final Connection my= CloudbeexConnection.getConnection();
		
		//JdbcTemplate springPg= HerokuConnection.getSpringConnection();
		final JdbcTemplate springMy= CloudbeexConnection.getSpringConnection();
		
		/*counter
		List<Pair<String,Integer>> sites= springMy.query("SELECT * FROM counter", (rs, rowNum) -> new ImmutablePair<String, Integer>(rs.getString("site"), rs.getInt("count")));
		sites.forEach(rs -> {
			final String sql= "INSERT into counter (site,count) VALUES (?,?)";
			try {
				System.out.printf("%s %s\n", rs.getLeft(), rs.getRight());
				springPg.update(sql, new Object[]{rs.getLeft(), rs.getRight()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});*/
		
		List<BookmarkPage.User> cbUsers= springMy.query("SELECT * FROM rest_users", (rs, rowNum) -> new BookmarkPage.User(-1, rs.getString("user_name"), rs.getString("role_name"), rs.getString("site_name"), rs.getString("password")));
		List<Linx> cbLinx= springMy.query("SELECT * FROM bookmark_linx", (rs, rowNum) -> new Linx(rs.getString("link"), rs.getString("link_name"), rs.getString("paragraph_name"), rs.getString("page_name"), rs.getString("user"), rs.getInt("position"), rs.getInt("paragraph_position")));
		
		cbLinx.forEach(cbLink -> { try{

			//user
			BookmarkPage.User pgUser= null;
			/*cbUsers.stream().filter(u -> u.user_name == cbLink.user).collect(Collectors.toList()).forEach(x -> {
			cbUsers.stream().forEach(x -> {
				System.out.printf("User: %s %s %s %s\n", x.user_name, x.role_name, x.site_name, x.password);
				try {
					springPg.update("INSERT INTO rest_users (user_name, role_name, site_name, password) VALUES (?,?,?,?)", new Object[]{x.user_name, x.role_name, x.site_name, x.password});
				} catch (DuplicateKeyException e) {
					System.err.printf("Duplicate User: %s\n", x.user_name);
				}
			});*/
			
			System.out.printf("CBUser %s", cbLink.user);
			final String cbUserName= cbLink.user;

			final List<User> foundCbUser= cbUsers.stream().filter((User u) -> { 
				return (u.user_name.equals(cbUserName) && u.site_name.equals("com.fmt.bookmarks"));
			}).collect(Collectors.toList());
			final User cbUser= foundCbUser.get(0);
			try {
				pgUser= HerokuConnection.getSpringConnection().queryForObject("SELECT * FROM rest_users WHERE user_name=? AND site_name=?", new Object[]{cbUserName, "com.fmt.bookmarks"}, (row, rn) -> new BookmarkPage.User(row.getInt("id"), row.getString("user_name"), row.getString("role_name"), row.getString("site_name"), row.getString("password")));
			} catch (EmptyResultDataAccessException ex) {
				System.out.printf("New user %s\n", cbUserName);
			}
			if(null == pgUser) {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				HerokuConnection.getSpringConnection().update("INSERT INTO rest_users (user_name, site_name, role_name, password) VALUES (?,?,?,?)", new Object[]{cbUserName, "com.fmt.bookmarks", cbUser.role_name, cbUser.password}, keyHolder);
				final int userId= keyHolder.getKey().intValue();
				pgUser= HerokuConnection.getSpringConnection().queryForObject("SELECT * FROM rest_users WHERE id=?", new Object[]{userId}, (row, rn) -> new BookmarkPage.User(userId, row.getString("user_name"), row.getString("role_name"), row.getString("site_name"), row.getString("password")));
			}
			if(null == pgUser) throw new Exception("No User: "+ cbUserName); 
			
			//page
			BookmarkPage.Page pgPage= null;
			try {
				final User fpgUser= pgUser;
				pgPage= HerokuConnection.getSpringConnection().queryForObject("SELECT * FROM bookmark_page WHERE user_id=? and page_name=?", new Object[]{fpgUser.id, cbLink.page_name}, (rw, n) -> {
				/*final int userIdd= rw.getInt("user_id");
				List<User> usrs= cbUsers.parallelStream().filter(uu -> {
					return (uu.id == userIdd);
				}).collect(Collectors.toList());
				final User usr= usrs.get(0);*/
				
				return new BookmarkPage.Page(rw.getInt("id"), fpgUser, rw.getString("page_name"));
				}
			);
			} catch(EmptyResultDataAccessException ex) {
				System.out.printf("New page %s\n", cbLink.page_name);				
			}
			if(null == pgPage) {
				KeyHolder keyHolder = new GeneratedKeyHolder();
				//springPg.update("INSERT INTO bookmark_page (user_id, page_name) VALUES (?,?)", new Object[]{pgUser.id, cbLink.page_name}, keyHolder);
				//PreparedStatementCreatorFactory sql= new PreparedStatementCreatorFactory("INSERT INTO bookmark_page (user_id, page_name) VALUES (?,?)");
				PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory("INSERT INTO bookmark_page (user_id, page_name) VALUES (?,?)", new int[]{Types.INTEGER, Types.VARCHAR});
				factory.setReturnGeneratedKeys(true);
				PreparedStatementCreator psc= factory.newPreparedStatementCreator(new Object[]{pgUser.id, cbLink.page_name});
				HerokuConnection.getSpringConnection().update(psc, keyHolder);
				final int pageId= Integer.valueOf(keyHolder.getKeys().get("id").toString());
				final User fpgUser= CopyMySqlToPostgres.<User>use(pgUser);
				pgPage= HerokuConnection.getSpringConnection().queryForObject("SELECT * FROM bookmark_page WHERE id=?", new Object[]{pageId}, (row, rn) -> new BookmarkPage.Page(pageId, fpgUser, row.getString("page_name")));
			}
			if(null == pgPage) throw new Exception("No Page: "+ cbLink.user); 
			
			//paragraph
			BookmarkPage.Paragraph pgPara= null;
			try {
				//springPg= HerokuConnection.getSpringConnection();
				pgPara= HerokuConnection.getSpringConnection().queryForObject("SELECT * FROM bookmark_paragraphs WHERE page_id=? AND name=?", new Object[]{pgPage.id, cbLink.paragraph_name}, (row, pgn) -> new BookmarkPage.Paragraph(row.getInt("id"), row.getInt("position"), row.getString("name")));
			} catch(EmptyResultDataAccessException ex) {
				System.out.printf("New paragraph %s\n", cbLink.paragraph_name);
			}
			if(null == pgPara) {
				KeyHolder keyHolder = new GeneratedKeyHolder();

				PreparedStatementCreatorFactory factory = new PreparedStatementCreatorFactory("INSERT INTO bookmark_paragraphs (page_id, name, position) VALUES (?,?,?)", new int[]{Types.INTEGER, Types.VARCHAR, Types.INTEGER});
				factory.setReturnGeneratedKeys(true);
				PreparedStatementCreator psc= factory.newPreparedStatementCreator(new Object[]{pgPage.id, cbLink.paragraph_name, cbLink.paragraph_position});
				HerokuConnection.getSpringConnection().update(psc, keyHolder);
				final int paragraphId= Integer.valueOf(keyHolder.getKeys().get("id").toString());

				final Page fpgPage= CopyMySqlToPostgres.<Page>use(pgPage);
				pgPara= HerokuConnection.getSpringConnection().queryForObject("SELECT * FROM bookmark_paragraphs WHERE page_id=? AND name=?", new Object[]{pgPage.id, cbLink.paragraph_name}, (row, rn) -> new Paragraph(row.getInt("id"), cbLink.paragraph_position, cbLink.paragraph_name));
			}
			if(null == pgPara) throw new Exception("No Paragraph: "+ cbLink.paragraph_name);
			
			//links
			HerokuConnection.getSpringConnection().update("INSERT INTO bookmark_linx (paragraph_id, name, link_url, position) VALUES (?,?,?,?)", new Object[]{pgPara.id, cbLink.link_name, cbLink.link, cbLink.position});

			System.out.printf("INSERT: page: %s para: %s ln-name: %s\n", cbLink.page_name, cbLink.paragraph_name, cbLink.link_name);
		} catch(Exception ex) {
			System.out.println(ex);
		}
 		});
		
		/* counter
		List<Pair<String,Integer>> sites= springMy.query("SELECT * FROM counter", (rs, rowNum) -> new ImmutablePair<String, Integer>(rs.getString("site"), rs.getInt("count")));
		sites.forEach(rs -> {
			final String sql= "INSERT into counter (site,count) VALUES (?,?)";
			try {
				System.out.printf("%s %s\n", rs.getLeft(), rs.getRight());
				//springPg.update(sql, new Object[]{rs.getLeft(), rs.getRight()});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});*/

		
	}

	public static class Linx {
		final public String link, link_name, paragraph_name, page_name, user;
		  final public int position, paragraph_position;
		  
		  public Linx(String link, String link_name, String paragraph_name, String page_name, String user, int position, int paragraph_position) {
			super();
			this.link = link;
			this.link_name = link_name;
			this.paragraph_name = paragraph_name;
			this.page_name = page_name;
			this.user = user;
			this.position = position;
			this.paragraph_position = paragraph_position;
		}
	}
	
	public static <T> T use(final T o) {
		return o;
	}
}
