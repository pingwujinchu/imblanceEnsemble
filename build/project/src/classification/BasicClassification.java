package classification;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bean.EvaluationInfo;
import evaluation.OversampleEvaluation;
import evaluation.UndersampleEvaluation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/*
 * This is the super class of other classification method
 */
public class BasicClassification {

	protected Instances data;
	DecimalFormat df;
	EvaluationInfo ei;
	protected int numClass;

	public BasicClassification(Instances data) {
		this.data = data;
		numClass = data.attribute(data.numAttributes()-1).numValues();
	}


	public List<EvaluationInfo> classify(int times, Classifier classifier, String classifier_name,EvaluationInfo ei) throws Exception {
		return getClassificationResult(classifier, classifier_name, times,ei);//get the result without bagging
//		return predictResult;
	}

//	public EvaluationInfo classify(int times, Classifier classifier, String classifier_name,EvaluationInfo ei) throws Exception {
//		getClassificationResult(classifier, classifier_name, times);//get the result without bagging
//		this.ei = ei;
//		return ei;
//	}
	
	public Evaluation evaluate(Classifier classifier, int randomSeed, String sample)
			throws Exception {
		Random rand;
		rand = new Random(randomSeed);
		Evaluation eval = null;
		if(sample.equals("under")){
			eval = new UndersampleEvaluation(data);
			eval.crossValidateModel(classifier, data, 10, rand);
		}
		else if(sample.equals("over")){
			eval = new OversampleEvaluation(data);
			eval.crossValidateModel(classifier, data, 10, rand);
		}
		else{
			eval = new Evaluation(data);
			eval.crossValidateModel(classifier, data, 10, rand);//use 10-fold cross validataion
		}
		return eval;
	}

	//save the interested result of the classification
	public EvaluationInfo getResult(String methodname, String classifiername, double validationResult[], int times,EvaluationInfo ei) throws Exception {
		df = (DecimalFormat) NumberFormat.getInstance();//use df to format result of be form of 0.0000
		df.applyPattern("0.0");

		int pos = 0;
		//获取TPR
		for(int i = 0 ; i < numClass ; i++){
			ei.tpRate[i] = validationResult[pos]*100/times;
			pos ++;
		}

		//获取FPR
		for(int i = 0; i < numClass ; i++){
			ei.fpRate[i] = validationResult[ pos]*100/times;
			pos ++;
		}
		
		//获取precision
		for(int i = 0 ; i < numClass ; i++){
			ei.precision[i] = validationResult[pos]*100/times;
			pos ++;
		}
		
		//获取recall
		for(int i = 0 ; i < numClass ; i++){
			ei.recall[i] = validationResult[pos]*100/times;
			pos ++;
		}
		
		//获取FMeasure
		for(int i = 0 ; i < numClass ; i++){
			ei.fMeasure[i] = validationResult[pos]*100/times;
			pos ++;
		}
		
		ei.gmean = validationResult[pos++]*100/times;
		ei.auc = validationResult[pos++]*100/times;
		ei.accuracy = validationResult[pos ++]/times;
		return ei;
	}
	
//	public void getResult(String methodname, String classifiername, double validationResult[], int times,EvaluationInfo ei) throws Exception {
//		df = (DecimalFormat) NumberFormat.getInstance();//use df to format result of be form of 0.0000
//		df.applyPattern("0.0");
//		double accuracy = validationResult[0]/times;
//		double recall_0 = validationResult[1]*100/times;
//		double recall_1 = validationResult[2]*100/times;
//		double precison_0 = validationResult[3]*100/times;
//		double precison_1 = validationResult[4]*100/times;
//		double fmeasure_0 = validationResult[5]*100/times;
//		double fmeasure_1 = validationResult[6]*100/times;
//		double gmean = validationResult[7]*100/times;
//		double auc = validationResult[8]*100/times;
//		ei.setAccuracy(accuracy);
//		ei.setAuc(auc);
//		ei.setfMeasure(new double[]{fmeasure_0,fmeasure_1});
//		ei.setGmean(gmean);
//		ei.setRecall(new double[]{recall_0,recall_1});
//	}
	
//	public void updateResult(double validationResult[], Evaluation eval){
//		double accuracy = eval.pctCorrect();
//		double recall_0 = eval.recall(0);
//		double recall_1 = eval.recall(1);
//		double precison_0 = eval.precision(0);
//		double precison_1 = eval.precision(1);
//		double fmeasure_0 = eval.fMeasure(0);
//		double fmeasure_1 = eval.fMeasure(1);
//		double gmean = Math.sqrt(recall_0*recall_1);
//		double auc = eval.areaUnderROC(0);
//		
//		
//		validationResult[0] += accuracy;
//		validationResult[1] += recall_0;
//		validationResult[2] += recall_1;
//		validationResult[3] += precison_0;
//		validationResult[4] += precison_1;
//		validationResult[5] += fmeasure_0;
//		validationResult[6] += fmeasure_1;
//		validationResult[7] += gmean;
//		validationResult[8] += auc;
//		
//	}
	
	public void updateResult(double validationResult[], Evaluation eval){
		int pos = 0;
		
		//添加TPR
		for(int i  = 0 ; i < numClass ; i++){
			validationResult[i] += eval.truePositiveRate(i);
			pos ++;
		}
		
		//添加FPR
		for(int i = 0;i < numClass ; i++){
			validationResult[pos] += eval.falsePositiveRate(i);
			pos ++;
		}
		
		//添加precision
		for(int i = 0; i < numClass ; i++){
			validationResult[pos ] += eval.precision(i);
			pos ++;
		}
		
		double gmean = 1;
		
		//添加recall
		for(int i = 0 ; i < numClass ; i++){
			gmean *= eval.recall(i);
			validationResult[pos] += eval.recall(i);
			pos ++;
		}
		
		//添加Fmeasure
		for(int i = 0 ; i < numClass ; i++){
			validationResult[pos] += eval.fMeasure(i);
			pos ++;
		}
		validationResult[pos ++] += Math.sqrt(gmean);
		validationResult[pos ++] += eval.areaUnderROC(0);
		validationResult[pos ++] += eval.pctCorrect();
		
	}

	//save the interested result of the classification
	protected String getResultMatrix(Evaluation eval) throws Exception {
		return eval.toMatrixString() +"\n";
	}
	
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception{
//		return "";
		return new ArrayList();
	};
	
}
