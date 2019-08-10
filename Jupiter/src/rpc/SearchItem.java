package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import ticketMasterAPI.TicketMasterClient;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search") // endpoint
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		HttpSession session = request.getSession(false);
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term");
		
		DBConnection connection = DBConnectionFactory.getConnection();
		List<Item> items = connection.searchItems(lat, lon, term);
		JSONArray array = new JSONArray();
		
		if (session == null) {
			for (Item item : items) {
				array.put(item.toJSONObject());
			}
			
			RpcHelper.writeJsonArray(response, array);
			connection.close();
		} else {		
			String userId = session.getAttribute("user_id").toString();		
			try {				
				Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
				for (Item item : items) {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
