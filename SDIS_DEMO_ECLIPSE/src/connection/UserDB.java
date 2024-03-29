package connection;

import java.io.Serializable;
import java.util.ArrayList;



@SuppressWarnings("serial")
public class UserDB implements Serializable {
	ArrayList<UserT> users;
	static ArrayList<Group> groups;

	public UserDB()  {
		users = new ArrayList<UserT>();
		groups = new ArrayList<Group>();
	}

	public boolean usercheck(String trim) {
		// TODO Auto-generated method stub
		if(!users.isEmpty()){
			for(int i=0; i<users.size(); i++){
				if(users.get(i).getUsername().matches(trim) == true)
					return true;
			}
		}
		return false;
	}

	public void addUser(UserT teste) {
		// TODO Auto-generated method stub
		if(usercheck(teste.getUsername()))
			System.out.println("ja existe caralho,userdb,adduser");
		else
			users.add(teste);
	}
	public Group getGroup(String groupname) {
		for(int i = 0; i < groups.size();i++)
			if(groups.get(i).getName().matches(groupname))
				return groups.get(i);
		return null;
	}
	public boolean passwordcheck(String username , String password) {
		if(!users.isEmpty()) {
			for(int i = 0; i < users.size();i++) {
				if(users.get(i).getUsername().matches(username) == true) {
					if(users.get(i).getPassword().matches(password) == true) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public int generateGroupID() {
		if(!groups.isEmpty())
			return groups.size()+1;
		else
			return 1;
	}

	public boolean checkgroupname(String trim) {
		// TODO Auto-generated method stub
		if(!groups.isEmpty()) {
			for(int i = 0; i < groups.size();i++) {
				if(groups.get(i).getName().matches(trim) == true)
					return true;
			}
		}
		return false;
	}

	public void addGroup(Group newgroup,String username) {
		// TODO Auto-generated method stub
		groups.add(newgroup);
		for(int i = 0; i < users.size();i++)
			if(users.get(i).getUsername().matches(username))
				users.get(i).getGroups().add(newgroup);

	}

	public boolean checkAccesstoken(String trim) {
		// TODO Auto-generated method stub
		if(!groups.isEmpty())
			for(int i = 0; i < groups.size();i++)
				if(groups.get(i).getAccesstoken().matches(trim))
					return true;
		return false;
	}
	public boolean checkAdmintoken(String trim) {
		// TODO Auto-generated method stub
		if(!groups.isEmpty())
			for(int i = 0; i < groups.size();i++)
				if(groups.get(i).getAdmintoken().matches(trim))
					return true;
		return false;
	}
	public void joinUserGroup(String trim, String accesstoken) {
		// TODO Auto-generated method stub
		for(int i = 0; i < users.size();i++) {
			if(users.get(i).getUsername().matches(trim))
				for(int j = 0; j < groups.size();j++)
					if(groups.get(j).getAccesstoken().matches(accesstoken))
						users.get(i).getGroups().add(groups.get(j));

		}

	}
	public void printGroup() {
		for(int i = 0; i < groups.size();i++)
			System.out.println(groups.get(i).getName() + " BUKAM "+ i  +"BALUE " + groups.get(i).getAccesstoken());
	}

	public void joinUserAdmin(String name, String admintoken) {
		// TODO Auto-generated method stub
		for(int i = 0; i < users.size();i++) {
			if(users.get(i).getUsername().matches(name))
				for(int j = 0; j < groups.size();j++)
					if(groups.get(j).getAdmintoken().matches(admintoken)) {
						groups.get(j).getAdmins().add(name);
						if(!userBelongsToGroup(name,admintoken))
							users.get(i).getGroups().add(groups.get(j));
					}

		}
	}
	public boolean userBelongsToGroup(String name,String admintoken) {
		for(int i = 0; i < users.size();i++) {
			if(users.get(i).getUsername().matches(name)) {
				for(int j = 0; j < users.get(i).getGroups().size();j++) {
					if(users.get(i).getGroups().get(j).getAdmintoken().matches(admintoken))
						return true;
				}
			}
		}
		return false;
	}
	public ArrayList<Group> getGroups(String username){

		if(!users.isEmpty()) {
			for(int i = 0; i < users.size();i++) {
				if(users.get(i).getUsername().matches(username) == true) {
					return users.get(i).getGroups();
				}
			}
		}
		return null;
	}
	public void listBD() {
		for(int i = 0; i < users.size();i++)
			System.out.println(users.get(i).getUsername());
		for(int j = 0; j < groups.size();j++)
			System.out.println(groups.get(j).getName());
	}
	
	public static void listGroupAdmins(String group){
		for(int i = 0; i < groups.size(); i++){
			if(groups.get(i).getName().matches(group) == true){
				for(int j = 0; j < groups.get(i).getAdmins().size(); j++){
					System.out.println((j+1) + " - " + groups.get(i).getAdmins().get(j));
				}
			}
		}
	}

	public void updateGroupfilelist(String group, String file) {
		// TODO Auto-generated method stub
		for(int i = 0; i < groups.size();i++) {
			if(groups.get(i).getName().matches(group) == true)
				groups.get(i).fileList.add(file);
		}
	}

	public boolean checkFileExist(String group, String file) {
		// TODO Auto-generated method stub
		for(int i = 0; i < groups.size();i++) {
			if(groups.get(i).getName().matches(group) == true)
				for(int j = 0; j < groups.get(i).getFilelist().size();j++)
					if(groups.get(i).getFilelist().get(j).matches(file) == true)
						return true;
		}
		return false;
	}

	public boolean revokeAdmin(String group, String admin, String username){
		for(int i = 0; i < groups.size(); i++){
			if(groups.get(i).getName().matches(group) == true){
				if(groups.get(i).getAdmins().get(0).matches(admin) == true){
					for(int j = 0; j < groups.get(i).getAdmins().size(); j++){
						if(groups.get(i).getAdmins().get(j).matches(username)){
							System.out.println("You just revoked " + groups.get(i).getAdmins().get(j) + " from admin rights!");
							groups.get(i).getAdmins().remove(j);
							return true;
						}	
					}
					System.out.println("The admin you selected doesn't exist!");
					return false;
				}
				else{
					System.out.println("Only the creator of the group can revoke other admins!");
					return false;
				}
			}
		}
		return false;
	}
}
