package application;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

public class Log implements Observable{
	private String log;
	private String Info;
	public Log( String log,String info) {
		super();
		this.log = log;
		Info = info;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return log;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	
}
