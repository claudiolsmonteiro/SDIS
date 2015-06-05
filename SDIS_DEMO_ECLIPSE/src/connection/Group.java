package connection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("serial")
public class Group implements Serializable {
	String name,accesstoken, admintoken;
	int id;
	ArrayList<String> admins,fileList;
	
	public Group (String name,int id,String username) {
		this.name = name;
		this.id = id;
		admins = new ArrayList<String>();
		admins.add(username);
		fileList = new ArrayList<String>();
		this.accesstoken = UUID.randomUUID().toString();
		System.out.println("Access token = " + accesstoken);
		this.admintoken = UUID.randomUUID().toString();
		System.out.println("Admin token = " + admintoken);
	}
	
	public String getAccesstoken() {
		return accesstoken;
	}
	public String getAdmintoken() {
		return admintoken;
	}
	public ArrayList<String> getFilelist() {
		return fileList;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}

	public ArrayList<String> getAdmins() {
		return admins;
	}

}
