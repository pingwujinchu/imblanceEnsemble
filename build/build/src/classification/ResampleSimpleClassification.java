package classification;

import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class ResampleSimpleClassification extends BasicClassification{

	
	public ResampleSimpleClassification(Instances data) {
		super(data);
		
	}

	//get the classification result without bagging
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,EvaluationInfo ei) throws Exception {
		double validationResult1[] = new double[5*numClass + 3];
		double validationResult2[] = new double[5*numClass + 3];
		//use different seed for 10-fold cross validation
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(classifier, randomSeed, "over");
			updateResult(validationResult1, eval);
		}
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(classifier, randomSeed, "under");
			updateResult(validationResult2, eval);
		}
		List<EvaluationInfo > result = new ArrayList();
		result.add(getResult("oversample", classifier_name, validationResult1, times,ei));
		result.add(getResult("undersample", classifier_name, validationResult2, times,new EvaluationInfo(numClass)));
		return result;
	}
}