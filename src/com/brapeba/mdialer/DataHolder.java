/**
 * @author      Joanmi Bardera <joanmibb@gmail.com>
 * @version     1.0                
 * @since       2013-06-02          
 */

package com.brapeba.mdialer;

public class DataHolder
{
    private String name; // Name
    private String phone; // Phone
    private String rawId; // raw contacts table ID
    private String contactId; // contacts table ID
    private String accType; // Account type
    private int pos; // position (e.g. for the listview+filtering)
    private int phones; // how many phone numbers attached
    
    public DataHolder(String t,String st,String rawId,String contactId, String st3,int pos, int phones)
    {
        this.name=t;
        this.phone=st;
        this.rawId=rawId;
        this.contactId=contactId;
        this.accType=st3;
        this.pos=pos;
        this.phones=phones;
    }
    
    public DataHolder()
    {
    }
    
    public void setData(String t,String st,String rawId,String contactId, String st3, int pos,int phones)
    {
        this.name=t;
        this.phone=st;
        this.rawId=rawId;
        this.contactId=contactId;
        this.accType=st3;
        this.pos=pos;
        this.phones=phones;
    }
    
    public void setName(String name)
    {
    	this.name=name;
    }
    
    public void setPhone(String phone,int phones)
    {
    	if (this.phone.length()>0) this.phone=this.phone+", "+phone; else this.phone=phone;
    	this.phones=this.phones+phones;
    }
    
    public void setAccType(String accType)
    {
    	if (this.accType.length()>0) this.accType=this.accType+", "+accType; else this.accType=accType;
    }
    
    public String getName() {return name;}
    public String getPhone() {return phone;}
    public String getRawId() {return rawId;}
    public String getContactId() {return contactId;}
    public String getAccType() {return accType;}
    public int getPos() {return pos;} 
    public int getPhones() {return phones;}
}
