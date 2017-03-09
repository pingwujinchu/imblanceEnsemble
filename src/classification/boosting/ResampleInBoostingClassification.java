package classification.boosting;

import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

import Classifier.OverBoosting;
import Classifier.UnderBoosting;
import bean.EvaluationInfo;

public class ResampleInBoostingClassification extends BasicClassification{

	public ResampleInBoostingClassification(Instances data) {
		super(data);
	}

	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception{
		List predictResult = new ArrayList();
		predictResult.addAll( getOverBoostClassificationResult(classifier, classifier_name, times,ei));
		predictResult.addAll(getUnderBoostClassificationResult(classifier, classifier_name, times,new EvaluationInfo(2)));
		//predictResult += getSmoteBoostClassificationResult(maxseed, classifier, classifier_name, times);
		return predictResult;
	}

	//	private String getSmoteBoostClassificationResult(int maxseed,
	//			Classifier classifier, String classifier_name) throws Exception {
	//		String predictResult = "";
	//		Random rand;
	//		for(int randomSeed = 1;randomSeed<=maxseed;randomSeed++){
	//			SmoteBoosting boost_classifier = new SmoteBoosting(); //set the classifier as bagging
	//			boost_classifier.setClassifier(classifier); //set the basic classifier of bagging
	//			rand = new Random(randomSeed);	
	//			Evaluation eval = new Evaluation(data);
	//			eval.crossValidateModel(boost_classifier, data, 10, rand);//use 10-fold cross validataion
	//			predictResult = getName(",smoteboost", classifier_name);
	//			predictResult += getResult(eval);
	//		}
	//		return predictResult;
	//	}

	//using bagging classification method with under sampling
	public List<EvaluationInfo> getUnderBoostClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		UnderBoosting boost_classifier = new UnderBoosting(); //set the classifier as bagging
		boost_classifier.setClassifier(classifier); //set the basic classifier of bagging
		boost_classifier.setUseResampling(true);
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(boost_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		List result = new ArrayList();
		result.add(getResult(",underinboost", classifier_name, validationResult, times,ei));
		return result;
	}


	private List<EvaluationInfo> getOverBoostClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		OverBoosting boost_classifier = new OverBoosting(); //set the classifier as bagging
		boost_classifier.setClassifier(classifier); //set the basic classifier of bagging
		boost_classifier.setUseResampling(true);
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(boost_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		List result = new ArrayList();
		result.add(getResult("overinboost", classifier_name, validationResult, times,ei));
		return result;
	}

}
