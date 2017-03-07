package Classifier;

import java.util.Random;

import resample.OverSubsample;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;

public class OverBoosting extends AdaBoostM1{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6717894908824625225L;

	/**
	 * Boosting method. Boosts using resampling
	 *
	 * @param data the training data to be used for generating the
	 * boosted classifier.
	 * @throws Exception if the classifier could not be built successfully
	 */
	protected void buildClassifierUsingResampling(Instances data) 
			throws Exception {

		Instances trainData, sample, training;
		double epsilon, reweight, sumProbs;
		Evaluation evaluation;
		int numInstances = data.numInstances();
		Random randomInstance = new Random(m_Seed);
		int resamplingIterations = 0;

		// Initialize data
		m_Betas = new double [m_Classifiers.length];
		m_NumIterationsPerformed = 0;
		// Create a copy of the data so that when the weights are diddled
		// with it doesn't mess up the weights for anyone else
		training = new Instances(data, 0, numInstances);
		sumProbs = training.sumOfWeights();
		for (int i = 0; i < training.numInstances(); i++) {
			training.instance(i).setWeight(training.instance(i).
					weight() / sumProbs);
		}

		// Do boostrap iterations
		for (m_NumIterationsPerformed = 0; m_NumIterationsPerformed < m_Classifiers.length; 
				m_NumIterationsPerformed++) {
			if (m_Debug) {
				System.err.println("Training classifier " + (m_NumIterationsPerformed + 1));
			}

			// Select instances to train the classifier on
			if (m_WeightThreshold < 100) {
				trainData = selectWeightQuantile(training, 
						(double)m_WeightThreshold / 100);
			} else {
				trainData = new Instances(training);
			}

			// Resample
			resamplingIterations = 0;
			double[] weights = new double[trainData.numInstances()];
			for (int i = 0; i < weights.length; i++) {
				weights[i] = trainData.instance(i).weight();
			}
			do {
				sample = trainData.resampleWithWeights(randomInstance, weights);
				//use over sample to balance the sample
				Instances tempData = new Instances(data);
				tempData.randomize(randomInstance);
				OverSubsample oversample = new OverSubsample();
				oversample.setInputFormat(tempData);	    	  
				oversample.setDistributionSpread(1);//set the ratio of the major class sample to the minor class
				sample = Filter.useFilter(tempData, oversample);

				// Build and evaluate classifier
				m_Classifiers[m_NumIterationsPerformed].buildClassifier(sample);
				evaluation = new Evaluation(data);
				evaluation.evaluateModel(m_Classifiers[m_NumIterationsPerformed], 
						training);
				epsilon = evaluation.errorRate();
				resamplingIterations++;
			} while (Utils.eq(epsilon, 0) && 
					(resamplingIterations < 10));

			// Stop if error too big or 0
			if (Utils.grOrEq(epsilon, 0.5) || Utils.eq(epsilon, 0)) {
				if (m_NumIterationsPerformed == 0) {
					m_NumIterationsPerformed = 1; // If we're the first we have to to use it
				}
				break;
			}

			// Determine the weight to assign to this model
			m_Betas[m_NumIterationsPerformed] = Math.log((1 - epsilon) / epsilon);
			reweight = (1 - epsilon) / epsilon;
			if (m_Debug) {
				System.err.println("\terror rate = " + epsilon
						+"  beta = " + m_Betas[m_NumIterationsPerformed]);
			}

			// Update instance weights
			setWeights(training, reweight);
		}
	}


}
