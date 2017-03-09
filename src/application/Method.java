package application;

public class Method {
	 int index;
     private String sample;
     private String in;
     private String ensamble;
     private String base;
     
     
	public Method(int index,String sample, String in, String ensamble) {
		super();
		this.index = index;
		this.sample = sample;
		this.in = in;
		this.ensamble = ensamble;
	}
	
	public Method(String sample, String in, String ensamble,String base) {
		this.sample = sample;
		this.in = in;
		this.ensamble = ensamble;
		this.base = base;
	}
	
	public Method(Method method){
		 this.index = method.index;
	     this.sample = method.sample;
	     this.in = method.in;
	     this.ensamble = method.ensamble;
	     this.base = method.base;
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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
