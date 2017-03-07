package application;

import java.text.DecimalFormat;
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
	
	public Log( String log) {
		super();
		this.log = log;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return  log+"-"+method.getEnsamble()+" "+method.getIn()+" "+method.getSample();
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
	public double get(int index){
		double result = 0;
		switch(index){
		   case 0:
			   result = ei.get(0).tpRate[0];
			   break;
		   case 1:
			   result = ei.get(0).fpRate[0];
			   break;
		   case 2:
			   result = ei.get(0).precision[0];
			   break;
		   case 3:
			   result = ei.get(0).recall[0];
			   break;
		   case 4:
			   result = ei.get(0).fMeasure[0];
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
	public String getResultInfo(){
		StringBuilder strBuilder = new StringBuilder();
		String head = "======运行设置=====\n\n";
		String space="";
		strBuilder.append(head);
		strBuilder.append("数据集:			"+dataset.getDataSetName()+"\n");
		strBuilder.append("实例个数:			"+dataset.getInstancesNum()+"\n");
		strBuilder.append("属性个数：		"+dataset.getAttributesNum()+"\n");
		strBuilder.append("使用方法：		"+method.getEnsamble()+" "+method.getIn()+" "+method.getSample()+"\n");
		strBuilder.append("基分类器:			"+method.getBase());
		strBuilder.append("\n\n\n");
		strBuilder.append("======运行结果=====\n\n");
		strBuilder.append("					TPR			FPR			Precision		Recall		FMeasure		Gmeans		Acc			AUC			Class\n");
		DecimalFormat    df   = new DecimalFormat("#####00.00");   
		for(int i = 0 ; i < ei.size() ; i++){
			EvaluationInfo evi = ei.get(i);
			for(int j = 0 ; j < evi.classNum ; j++){
				strBuilder.append("					"+df.format(evi.tpRate[j])+"		"+df.format(evi.fpRate[j])+"		"+df.format(evi.precision[j])+"		"+df.format(evi.recall[j])+"		"+df.format(evi.fMeasure[j])+"		"+df.format(evi.gmean)+"		"+df.format(evi.accuracy)+"		"+df.format(evi.auc)+"		"+j+"\n");
			}
		}
		return strBuilder.toString();
	}
}
