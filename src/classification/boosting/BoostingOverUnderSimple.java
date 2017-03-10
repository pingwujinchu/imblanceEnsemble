package classification.boosting;

import java.util.ArrayList;
import java.util.List;

import Classifier.UnderBagging;
import bean.EvaluationInfo;
import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;

public class BoostingOverUnderSimple extends BasicClassification{

	public BoostingOverUnderSimple(Instances data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,
			EvaluationInfo ei) throws Exception {
		// TODO Auto-generated method stub
		
		double validationResult[] = new double[5*numClass + 3];
		AdaBoostM1 boost_classifier = new AdaBoostM1(); //set the classifier as bagging
		boost_classifier.setClassifier(classifier); //set the basic classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(boost_classifier, randomSeed, "over");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo> result = new ArrayList();
		result.add(getResult(",underbag", classifier_name, validationResult, times,ei));
		return result;
	}

}
