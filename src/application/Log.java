package application;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import bean.DataSet;
import bean.EvaluationInfo;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

public class Log implements Observable{
	private String log;
	private String Info;
	private DataSet dataset;
	private List<EvaluationInfo> ei;
	private Method method;
	
	public List logList;
	String allInfo;
	
	public Log( String log) {
		super();
		this.log = log;
		logList = new ArrayList();
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  log;
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
	
	public String getMethodString(){
		return method.getEnsamble()+" "+method.getIn()+" "+method.getSample();
	}
	@Override
	public void addListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeListener(InvalidationListener listener) {
		// TODO Auto-generated method stub
		
	}
	public DataSet getDataset() {
		return dataset;
	}
	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}
	public List<EvaluationInfo> getEi() {
		return ei;
	}
	public void setEi(List<EvaluationInfo> ei) {
		this.ei = ei;
	}
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	//{"TP","FP","Precision","Recall","FMeasure","Gmeans","Acc","AUC"}
	public double get(int index,int classIndex){
		double result = 0;
		switch(index){
		   case 0:
			   result = ei.get(0).tpRate[classIndex];
			   break;
		   case 1:
			   result = ei.get(0).fpRate[classIndex];
			   break;
		   case 2:
			   result = ei.get(0).precision[classIndex];
			   break;
		   case 3:
			   result = ei.get(0).recall[classIndex];
			   break;
		   case 4:
			   result = ei.get(0).fMeasure[classIndex];
			   break;
		   case 5:
			   result = ei.get(0).gmean;
			   break;
		   case 6:
			   result = ei.get(0).accuracy;
			   break;
		   case 7:
			   result = ei.get(0).auc;
			   break;
		}
		return result;
	}
	
	public String getStr(int index){
		String result = "";
		switch(index){
		   case 0:
			   result = "TPR";
			   break;
		   case 1:
			   result = "FPR";
			   break;
		   case 2:
			   result = "Precision";
			   break;
		   case 3:
			   result = "Recall";
			   break;
		   case 4:
			   result = "FMeasure";
			   break;
		   case 5:
			   result = "Gmeans";
			   break;
		   case 6:
			   result = "Acc";
			   break;
		   case 7:
			   result = "AUC";
			   break;
		}
		return result;
	}
	
	//获取日志信息
	public String getResultInfo(Log log){
		StringBuilder strBuilder = new StringBuilder();
		String head = "\n\n======运行设置=====\n\n";
		String space="";
		strBuilder.append(head);
		strBuilder.append("数据集:			"+log.getDataset().getDataSetName()+"\n");
		strBuilder.append("实例个数:			"+log.getDataset().getInstancesNum()+"\n");
		strBuilder.append("属性个数：		"+log.getDataset().getAttributesNum()+"\n");
//		strBuilder.append("使用方法：		"+method.getEnsamble()+" "+method.getIn()+" "+method.getSample()+"\n");
//		strBuilder.append("基分类器:			"+method.getBase());
		
		return strBuilder.toString();
	}
	
	public String getLogShow(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getResultInfo(this));
		strBuilder.append(getResMethod(this));
		strBuilder.append(getResBase(this));
		
		return strBuilder.toString();
	}
	
	public String getResMethod(Log log){
		StringBuilder strBuilder = new StringBuilder();
		String head = "\n\n";
		String space="";
		strBuilder.append(head);
		strBuilder.append("使用方法：		"+log.getMethod().getEnsamble()+" "+log.getMethod().getIn()+" "+log.getMethod().getSample()+"\n");
		
		return strBuilder.toString();
	}
	
	public String getResBase(Log log){
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append("\n\n*************");
		strBuilder.append("基分类器:			"+log.getMethod().getBase());
		
		strBuilder.append("\n\n\n");
		strBuilder.append("*****运行结果*****\n\n");
		strBuilder.append("					TPR			FPR			Precision		Recall		FMeasure		Gmeans		Acc			AUC			Class\n");
		DecimalFormat    df   = new DecimalFormat("#####00.00");   
		for(int i = 0 ; i < log.getEi().size() ; i++){
			EvaluationInfo evi = log.getEi().get(i);
			for(int j = 0 ; j < evi.classNum ; j++){
				strBuilder.append("					"+df.format(evi.tpRate[j])+"		"+df.format(evi.fpRate[j])+"		"+df.format(evi.precision[j])+"		"+df.format(evi.recall[j])+"		"+df.format(evi.fMeasure[j])+"		"+df.format(evi.gmean)+"		"+df.format(evi.accuracy)+"		"+df.format(evi.auc)+"		"+j+"\n");
			}
		}
		return strBuilder.toString();
	}
	
	public String getAllInfo(){
		if(allInfo != null){
			return allInfo;
		}
		StringBuilder strBuilder = new StringBuilder();
		if(logList != null && logList.size() > 0){
			for(int i = 0 ; i < logList.size() ; i++){
				  strBuilder.append(getResultInfo(((Log)((List)((List)(logList.get(i))).get(0)).get(0))));
				  List methodList = (List) logList.get(i);
				  for(int j = 0 ; j < methodList.size() ; j++){
					  strBuilder.append(getResMethod((Log)((List)methodList.get(j)).get(0)));
					  List baseList = (List) methodList.get(j);
					  for(int k = 0 ; k < baseList.size() ; k++){
						  strBuilder.append(getResBase((Log)baseList.get(k)));
					  }
				  }
			}
			allInfo = strBuilder.toString();
			return allInfo;
		}else{
			return "";
		}
	}
}
