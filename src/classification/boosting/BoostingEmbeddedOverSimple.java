package classification.boosting;

import java.util.ArrayList;
import java.util.List;

import Classifier.OverBoosting;
import bean.EvaluationInfo;
import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class BoostingEmbeddedOverSimple extends BasicClassification{

	public BoostingEmbeddedOverSimple(Instances data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,
			EvaluationInfo ei) throws Exception {
		// TODO Auto-generated method stub
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
