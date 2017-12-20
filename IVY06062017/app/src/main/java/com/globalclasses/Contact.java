package com.globalclasses;

public class Contact {
	
	//private variables
	String id_contact,user_name,email;
	String phone_number,is_already_added;
	String user_id_login;
	
	// Empty constructor
	public Contact(){
		
	}

	// constructor
	public Contact( String user_id_login, String id_contact, String is_already_added){
		this.user_id_login = user_id_login;
		this.id_contact = id_contact;
		this.is_already_added=is_already_added;
	}
	
	// constructor
	public Contact(String id_contact,String user_name,String email, String phone_number,
				   String is_already_added){
		this.id_contact=id_contact;
		this.user_name = user_name;
		this.phone_number = phone_number;
		this.email=email;
		this.is_already_added=is_already_added;
		this.user_id_login=user_id_login;
	}



	// getting name
	public String getName(){
		return this.user_name;
	}
	
	// setting name
	public void setName(String name){
		this.user_name = name;
	}
	
	// getting phone number
	public String getPhoneNumber(){
		return this.phone_number;
	}
	
	// setting phone number
	public void setPhoneNumber(String phone_number){
		this.phone_number = phone_number;
	}

	// getting e_mail
	public String getE_mail(){
		return this.phone_number;
	}

	// setting E_mail
	public void setE_mail(String phone_number){
		this.phone_number = phone_number;
	}

	// getting id_contact
	public String getIdContact(){
		return this.id_contact;
	}

	// setting id_contact
	public void setIdContact(String id_contact){
		this.id_contact = id_contact;
	}

	// getting is_already_added
	public String getIs_already_added(){
		return this.is_already_added;
	}

	// setting id_contact
	public void setIs_already_added(String is_already_added){
		this.is_already_added = is_already_added;
	}

	// get login user_id
	public String getUserId()
	{
		return this.user_id_login;
	}
}
