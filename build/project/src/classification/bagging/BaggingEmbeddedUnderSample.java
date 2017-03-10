package classification.bagging;

import java.util.ArrayList;
import java.util.List;

import Classifier.UnderBagging;
import bean.EvaluationInfo;
import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class BaggingEmbeddedUnderSample  extends BasicClassification{

	public BaggingEmbeddedUnderSample(Instances data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,
			EvaluationInfo ei) throws Exception {
		// TODO Auto-generated method stub
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

}
