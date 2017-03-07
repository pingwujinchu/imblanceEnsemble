package classification.boosting;

import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;

public class ResampleBoostClassification extends BasicClassification{

	public ResampleBoostClassification(Instances data) {
		super(data);
	}

	//get the classification result without bagging
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult1[] = new double[5*numClass + 3];
		double validationResult2[] = new double[5*numClass + 3];
		AdaBoostM1 boost_classifier = new AdaBoostM1();
		boost_classifier.setClassifier(classifier);
		boost_classifier.setUseResampling(true);
		//use different seed for 10-fold cross validation
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(boost_classifier, randomSeed, "over");
			updateResult(validationResult1, eval);
		}
		
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(boost_classifier, randomSeed, "under");
			updateResult(validationResult2, eval);
		}
		List result = new ArrayList();
		result.add(getResult("overboost", classifier_name, validationResult1, times,ei));
		result.add(getResult(",underboost", classifier_name, validationResult2, times,new EvaluationInfo(2)));
		return result;
	}
}
