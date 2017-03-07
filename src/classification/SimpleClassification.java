package classification;


import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class SimpleClassification extends BasicClassification{

	public SimpleClassification(Instances data) {
		super(data);
	}

	//get the classification result without bagging
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[6*numClass + 4];
		List<EvaluationInfo> result = new ArrayList();
		//use different seed for 10-fold cross validation
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(classifier, randomSeed, "none");
			updateResult(validationResult, eval);
		}
		 result.add(getResult("simple", classifier_name, validationResult, times,ei));
		 return result;
	}
}
