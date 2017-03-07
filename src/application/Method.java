package application;

public class Method {
     private String sample;
     private String in;
     private String ensamble;
     private String base;
     
     
	public Method(String sample, String in, String ensamble,String base) {
		this.sample = sample;
		this.in = in;
		this.ensamble = ensamble;
		this.base = base;
	}
	public String getSample() {
		return sample;
	}
	public void setSample(String sample) {
		this.sample = sample;
	}
	public String getIn() {
		return in;
	}
	public void setIn(String in) {
		this.in = in;
	}
	public String getEnsamble() {
		return ensamble;
	}
	public void setEnsamble(String ensamble) {
		this.ensamble = ensamble;
	}
	public String getBase() {
		return base;
	}
	public void setBase(String base) {
		this.base = base;
	}
}
