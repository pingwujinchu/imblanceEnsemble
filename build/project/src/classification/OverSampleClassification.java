package classification;

import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * ¹ý²ÉÑù
 * @author fan
 *
 */
public class OverSampleClassification extends BasicClassification{

	public OverSampleClassification(Instances data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,
			EvaluationInfo ei) throws Exception {
		double validationResult[] = new double[5*numClass + 3];
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(classifier, randomSeed, "over");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo > result = new ArrayList();
		result.add(getResult("oversample", classifier_name, validationResult, times,ei));
		return result;
	}
 
	
}
