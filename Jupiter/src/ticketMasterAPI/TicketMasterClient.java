package ticketMasterAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

public class TicketMasterClient {
	private static final String HOST = "https://app.ticketmaster.com";
	private static final String ENDPOINT = "/discovery/v2/events.json";//cause we are using json, so add .json
	private static final String DEFAULT_KEYWORD = "event";
	private static final String API_KEY = "	deVCIMw7OrFiTpOeQK69ujk1OYDCJ1Te";
	
	public List<Item> search(double lat, double lon, String keyword) {
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
		String url = HOST + ENDPOINT + "?" + query;
		StringBuilder responseBody = new StringBuilder();
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET"); //ticket master require a GET request, see https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2
		
			int responseCode = connection.getResponseCode(); //this method do both send request, and get response code.
			if(responseCode != 200) {
				return null;
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));  //client side receive response as input
			String line = "";
			while((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			reader.close();
						
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			JSONObject obj = new JSONObject(responseBody.toString());
			if(!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public List<Item> searchByKeyword(String keyword) {
		if(keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		String query = String.format("apikey=%s&keyword=%s", API_KEY, keyword);
		String url = HOST + ENDPOINT + "?" + query;
		StringBuilder responseBody = new StringBuilder();
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET"); //ticket master require a GET request, see https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/#search-events-v2
		
			int responseCode = connection.getResponseCode(); //this method do both send request, and get response code.
			if(responseCode != 200) {
				return null;
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));  //client side receive response as input
			String line = "";
			while((line = reader.readLine()) != null) {
				responseBody.append(line);
			}
			reader.close();
						
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(responseBody.toString());
		
		try {
			JSONObject obj = new JSONObject(responseBody.toString());
			if(!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for(int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			
			if(!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if(!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if(!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if(!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			if(!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			builder.setCategories(getCategories(event));
			builder.setLocalDate(getLocalDate(event));
			builder.setLocalTime(getLocalTime(event));
			
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	private String getAddress(JSONObject event) throws JSONException {
		if(!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if(!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for(int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder builder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							builder.append(address.getString("line1"));
						}
						
						if (!address.isNull("line2")) {
							builder.append(",");
							builder.append(address.getString("line2"));
						}
						
						if (!address.isNull("line3")) {
							builder.append(",");
							builder.append(address.getString("line3"));
						}
					}
					
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						builder.append(",");
						builder.append(city.getString("name"));
					}
					
					String result = builder.toString();
					if (!result.isEmpty()) {
						return result;
					}					
				}
			}
		}
		return "";
	}
	
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}

	private Set<String> getCategories(JSONObject event) throws JSONException {		
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	
	private String getLocalDate(JSONObject event) throws JSONException {
		if(!event.isNull("dates")) {
			JSONObject dates = event.getJSONObject("dates");
			if(!dates.isNull("start")) {
				JSONObject start = dates.getJSONObject("start");
				if(!start.isNull("localDate")) {
					return start.getString("localDate");
				}
			}
		}
		return "";
	}

	private String getLocalTime(JSONObject event) throws JSONException {
		if(!event.isNull("dates")) {
			JSONObject dates = event.getJSONObject("dates");
			if(!dates.isNull("start")) {
				JSONObject start = dates.getJSONObject("start");
				if(!start.isNull("localTime")) {
					String localTime = start.getString("localTime");
					return localTime.substring(0, localTime.length() - 3);
				}
			}
		}
		return "";
	}
	
	public static void main(String[] args) {
		TicketMasterClient client = new TicketMasterClient();
		List<Item> events = client.search(37.38, -122.08, null);
		//List<Item> events = client.searchByKeyword("event");
		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}

	}
}
