package bean;

public class DataSet {
       private String dataSetName;
       private String relation;
       private int instancesNum;
       private int attributesNum;
       private String testMode;
       
       
       
	public DataSet() {
		
	}
	
	
	public DataSet(String dataSetName) {
		this.dataSetName = dataSetName;
	}


	public DataSet(String dataSetName, String relation, int instancesNum, int attributesNum, String testMode) {
		
		this.dataSetName = dataSetName;
		this.relation = relation;
		this.instancesNum = instancesNum;
		this.attributesNum = attributesNum;
		this.testMode = testMode;
	}
	public String getDataSetName() {
		return dataSetName;
	}
	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public int getInstancesNum() {
		return instancesNum;
	}
	public void setInstancesNum(int instancesNum) {
		this.instancesNum = instancesNum;
	}
	public int getAttributesNum() {
		return attributesNum;
	}
	public void setAttributesNum(int attributesNum) {
		this.attributesNum = attributesNum;
	}
	public String getTestMode() {
		return testMode;
	}
	public void setTestMode(String testMode) {
		this.testMode = testMode;
	}
}
