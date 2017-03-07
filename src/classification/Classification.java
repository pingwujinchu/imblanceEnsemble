package classification;

import java.util.List;

import Classifier.SmoteBagging;
import bean.DataSet;
import bean.EvaluationInfo;
import classification.bagging.BaggingClassification;
import classification.bagging.ResampleInBaggingClassification;
import classification.boosting.BoostingClassification;
import classification.boosting.ResampleBoostClassification;
import classification.boosting.ResampleInBoostingClassification;
import dataprocess.Util;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;


public class Classification {
	
	Instances data;
	String classifier_name;
	Classifier classifier;
	String output_file;
	String output_file_matrix;
	Util util = new Util();
	
	public Classification(Instances data) {
		this.data = data;

	}
	
	public void setClassifier(String classifier_name_input){
		classifier_name = classifier_name_input;
		switch(classifier_name){//use different classifier as the base classifier of bagging
		case "j48":
			classifier = new J48();
			break;
		case "naivebayes":
			classifier = new NaiveBayes();
			break;
		case "smo":
			classifier = new SMO();
			break;
		case "randomforest":
			classifier = new RandomForest();
			break;
		case "ripper":
			classifier = new JRip();
			break;
		case "IBk":
			classifier = new IBk();
			break;
		case "LR":
			classifier = new LinearRegression();
			break;
		}
	}
	
//	public String predict(String classifier_name_input, String filepath, String project, int times)  throws Exception{
//		setClassifier(classifier_name_input);
//		System.out.println("simple");
//		BasicClassification use_classification = new SimpleClassification(data);
//		String predict_result = "";
//		predict_result = project + "," + use_classification.classify(times, classifier, classifier_name);
//		System.out.println("resample");
//		use_classification = new ResampleSimpleClassification(data);
//		predict_result += "," + use_classification.classify(times, classifier, classifier_name);
//		System.out.println("bagging");
//		use_classification = new BaggingClassification(data);
//		predict_result += "," + use_classification.classify(times, classifier, classifier_name);
//		System.out.println("resample in bagging");
//		use_classification = new ResampleInBaggingClassification(data);
//		predict_result += "," + use_classification.classify(times, classifier, classifier_name);
//		
//		//boosting
//		System.out.println("boosting");
//		use_classification = new BoostingClassification(data);
//		predict_result += "," + use_classification.classify(times, classifier, classifier_name);
//		System.out.println("resample boost");
//		use_classification = new ResampleBoostClassification(data);
//		predict_result += "," + use_classification.classify(times, classifier, classifier_name);
//		System.out.println("resample in boost");
//		use_classification = new ResampleInBoostingClassification(data);
//		predict_result += "," + use_classification.classify(times, classifier, classifier_name);
//		return predict_result;
//	}
	
	
	public List<EvaluationInfo> predict(String simple,boolean isIn,String ensemble,String base, int times,EvaluationInfo ei)  throws Exception{
		String predict_result = "";
		BasicClassification use_classification = new SimpleClassification(data);
		classifier = (Classifier) Class.forName(base).newInstance();
		if(!isIn){
			if(ensemble.equals("Bagging")){
				if(simple.equals("SMOTE")){
					
				}else if(simple.equals("OverSample")){
					
				}else if(simple.equals("UnderSample")){
					
				}else if(simple.equals("NoneSample")){
					use_classification = new BaggingClassification(data);
				}
			}else if(ensemble.equals("Bosting")){
				if(simple.equals("SMOTE")){
					
				}else if(simple.equals("OverSample")){
					use_classification = new ResampleBoostClassification(data);
				}else if(simple.equals("UnderSample")){
					
				}else if(simple.equals("NoneSample")){
					use_classification = new BoostingClassification(data);
				}
			}else{
				if(simple.equals("SMOTE")){
					
				}else if(simple.equals("OverSample")){
					use_classification = new ResampleSimpleClassification(data);
				}else if(simple.equals("UnderSample")){
					
				}else if(simple.equals("NoneSample")){
				}
			}
		}else{
			if(ensemble.equals("Bagging")){
				if(simple.equals("SMOTE")){
					
				}else if(simple.equals("OverSample")){
					use_classification = new ResampleInBaggingClassification(data);
				}else if(simple.equals("UnderSample")){
					
				}else if(simple.equals("NoneSample")){
					
				}
			}else{
				if(simple.equals("SMOTE")){
					
				}else if(simple.equals("OverSample")){
					use_classification = new ResampleInBoostingClassification(data);
				}else if(simple.equals("UnderSample")){
					
				}else if(simple.equals("NoneSample")){
					
				}
			}
		}
		return use_classification.classify(times, classifier, base,ei);

	  }
	
}
