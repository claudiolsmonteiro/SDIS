package connection;

import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

public class MainServer {
	
	public static void register() {
		@SuppressWarnings("deprecation")
		FacebookClient facebookClient = new DefaultFacebookClient("CAACEdEose0cBAObB6GZBqEJPXoURST1fLDJHfQo6VZB99ZBe1HFTZCBniNkf2s4GjRNFyhJhQ7uoGKfpuVNBpi6Nwt7nDxtoa1bIxbTZBZBpKUsG9WUw7H8I8cX3dqL33kloElm4e1YqJkv4EYNeQCYBZAb0wa4YVhDQxFO8PZAq9wWGgdTeeZBqXUBJWjT760KtyDa2r9C8pcvo2dTosMFZCP");
		
		User user = facebookClient.fetchObject("me", User.class);
		Page page = facebookClient.fetchObject("cocacola", Page.class);

		System.out.println("User name: " + user.getName());
		System.out.println("Page likes: " + page.getLikes());
		
		Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
		Connection<Post> myFeed = facebookClient.fetchConnection("me/feed", Post.class);

		System.out.println("Count of my friends: " + myFriends.getData().size());
		
		JsonObject photosConnection = facebookClient.fetchObject("me/photos", JsonObject.class);
		String firstPhotoUrl = photosConnection.getJsonArray("data").getJsonObject(0).getString("source");
		System.out.println(firstPhotoUrl);
	}
	
	void login() {
		
	}
}
