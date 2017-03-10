package classification.boosting;

import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;

public class BoostingClassification extends BasicClassification{

	public BoostingClassification(Instances data) {
		super(data);
	}


	//using bagging classification method
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		AdaBoostM1 boost_classifier = new AdaBoostM1(); //set the classifier as bagging
		boost_classifier.setClassifier(classifier); //set the basic classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(boost_classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		List result = new ArrayList();
		result.add(getResult("boost", classifier_name, validationResult, times,ei));
		return result;
	}

}
