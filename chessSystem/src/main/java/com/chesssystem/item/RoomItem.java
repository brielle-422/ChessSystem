package com.chesssystem.item;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:30
 */
public class RoomItem {
	private String roomId;
	private String roomName;
	private double price;
	private double time;
	private String storeId;
	private int roomContains;
	private int status;
	private String roomPic;
	private String roomNumber;
	
	public RoomItem(String roomId,String roomName,double price,double time,String storeId,
			int status,String roomPic){
		this.roomId=roomId;
		this.roomName=roomName;
		this.price=price;
		this.time=time;
		this.storeId=storeId;
		this.status=status;
		this.roomPic=roomPic;
	}
	public String getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}
	public String getRoomPic() {
		return roomPic;
	}
	public void setRoomPic(String roomPic) {
		this.roomPic = roomPic;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public int getRoomContains() {
		return roomContains;
	}
	public void setRoomContains(int roomContains) {
		this.roomContains = roomContains;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
