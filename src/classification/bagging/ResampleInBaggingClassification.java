package classification.bagging;

import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import Classifier.OverBagging;
import Classifier.SmoteBagging;
import Classifier.UnderBagging;
import Classifier.UnderOverBagging;
import Classifier.UnderOverBaggingOld;
import bean.EvaluationInfo;

public class ResampleInBaggingClassification extends BasicClassification{

	public ResampleInBaggingClassification(Instances data) {
		super(data);
	}

	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception{
		String predictResult = "";
		//predictResult += getOverBagClassificationResult(classifier, classifier_name, times);
		//predictResult += getUnderBagClassificationResult(classifier, classifier_name, times);
		return getUnderOverBagClassificationResult(classifier, classifier_name, times,ei);
		//predictResult += getSmoteBagClassificationResult(classifier, classifier_name, times);
		
	}

	public List<EvaluationInfo> getSmoteBagClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		List<EvaluationInfo> result = new ArrayList();
		SmoteBagging bag_classifier = new SmoteBagging(); //set the classifier as bagging
		bag_classifier.setClassifier(classifier); //set the basic classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(bag_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		result.add(getResult(",smotebag", classifier_name, validationResult, times,ei));
		return result;
	}

	//using bagging classification method with under sampling
	public List<EvaluationInfo> getUnderBagClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		UnderBagging bag_classifier = new UnderBagging(); //set the classifier as bagging
		bag_classifier.setClassifier(classifier); //set the basic classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(bag_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo> result = new ArrayList();
		result.add(getResult(",underbag", classifier_name, validationResult, times,ei));
		return result;
	}


	private List<EvaluationInfo> getOverBagClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		OverBagging bag_classifier = new OverBagging(); //set the classifier as bagging
		bag_classifier.setClassifier(classifier); //set the basic classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(bag_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo> result = new ArrayList();
		result.add(getResult("overbag", classifier_name, validationResult, times,ei));
		return result;
	}

	//¸ÉÉ¶µÄ£¿£¿£¿
	private List<EvaluationInfo> getUnderOverBagClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		UnderOverBagging bag_classifier = new UnderOverBagging(); //set the classifier as bagging
		bag_classifier.setBaseClassifier(classifier);//set the base classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(bag_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo> result = new ArrayList();
		result.add(getResult("underoverbag", classifier_name, validationResult, times,ei));
		return result;
	}

}
