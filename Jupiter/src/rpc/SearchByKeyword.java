package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchByKeyword
 */
@WebServlet("/searchKeyword")
public class SearchByKeyword extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchByKeyword() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		String keyword = request.getParameter("keyword");

		DBConnection connection = DBConnectionFactory.getConnection();

		List<Item> itemList = connection.searchItemsByKeyword(keyword);
		JSONArray array = new JSONArray();
		
		HttpSession session = request.getSession(false);
		if(session == null) {		
			for (Item item : itemList) {
				array.put(item.toJSONObject());
			}
		
			RpcHelper.writeJsonArray(response, array);
			connection.close();
		} else {
			String userId = session.getAttribute("user_id").toString();		
			try {				
				Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
				for (Item item : itemList) {
					JSONObject obj = item.toJSONObject();
					obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
					array.put(obj);
				}
				RpcHelper.writeJsonArray(response, array);	
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				connection.close();
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
