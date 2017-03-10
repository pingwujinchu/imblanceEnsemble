package classification.bagging;

import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import classification.BasicClassification;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;

public class BaggingOverOverSample extends BasicClassification{

	public BaggingOverOverSample(Instances data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,
			EvaluationInfo ei) throws Exception {
		// TODO Auto-generated method stub
		double validationResult[] = new double[5*numClass + 3];
		Bagging bag_classifier = new Bagging(); //set the classifier as bagging
		bag_classifier.setClassifier(classifier); //set the basic classifier of bagging
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(bag_classifier, randomSeed, "over");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo> result = new ArrayList();
		result.add(getResult(",bagging over oversimple", classifier_name, validationResult, times,ei));
		return result;
	}

}
