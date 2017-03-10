package classification;

import java.util.ArrayList;
import java.util.List;

import bean.EvaluationInfo;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * Ç·²ÉÑù
 * @author fan
 *
 */
public class UnderSampleClassification  extends BasicClassification{

	public UnderSampleClassification(Instances data) {
		super(data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<EvaluationInfo> getClassificationResult(Classifier classifier, String classifier_name, int times,
			EvaluationInfo ei) throws Exception {
		// TODO Auto-generated method stub
		double validationResult[] = new double[5*numClass + 3];
		for(int randomSeed = 1;randomSeed<=times;randomSeed++){
			Evaluation eval = evaluate(classifier, randomSeed, "under");
			updateResult(validationResult, eval);
		}
		List<EvaluationInfo > result = new ArrayList();
		result.add(getResult("undersample", classifier_name, validationResult, times,new EvaluationInfo(numClass)));
		return result;
	}

}
