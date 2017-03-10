package Classifier;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;
import weka.core.Randomizable;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.instance.SpreadSubsample;

/*
 * This classifier extends Bagging, but rewrite the buildClassifier
 * method. It use undersample to create the trainind dataset for each base
 * classifier
 */
public class UnderBagging extends Bagging{
	/** 
	 * Stump method for building the classifiers.
	 *
	 * @param data the training data to be used for generating the
	 * bagged classifier.
	 * @exception Exception if the classifier could not be built successfully
	 */
	public void checkClassifier(Instances data) throws Exception {

		if (m_Classifier == null) {
			throw new Exception("A base classifier has not been specified!");
		}
		m_Classifiers = Classifier.makeCopies(m_Classifier, m_NumIterations);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5403874282828036162L;

	@Override
	public void buildClassifier(Instances data) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(data);

		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		checkClassifier(data);

		if (m_CalcOutOfBag && (m_BagSizePercent != 100)) {
			throw new IllegalArgumentException("Bag size needs to be 100% if "
					+ "out-of-bag error is to be calculated!");
		}

		int bagSize = data.numInstances() * m_BagSizePercent / 100;
		Random random = new Random(m_Seed);

		boolean[][] inBag = null;
		if (m_CalcOutOfBag)
			inBag = new boolean[m_Classifiers.length][];

		for (int j = 0; j < m_Classifiers.length; j++) {
			Instances bagData = null;

			// create the in-bag dataset
			if (m_CalcOutOfBag) {
				inBag[j] = new boolean[data.numInstances()];
				// bagData = resampleWithWeights(data, random, inBag[j]);
				bagData = data.resampleWithWeights(random, inBag[j]);
			} else {
				//bagData = data.resampleWithWeights(random);
				//rewrite the sampling method and use under sampling
				Instances tempData = new Instances(data);
				tempData.randomize(random);
				SpreadSubsample undersample = new SpreadSubsample();
				undersample.setInputFormat(tempData);
				undersample.setDistributionSpread(1);//set the ratio of the major class sample to the minor clas
				bagData = Filter.useFilter(tempData, undersample);
				if (bagSize < data.numInstances()) {
					bagData.randomize(random);
					Instances newBagData = new Instances(bagData, 0, bagSize);
					bagData = newBagData;
				}
			}

			if (m_Classifier instanceof Randomizable) {
				((Randomizable) m_Classifiers[j]).setSeed(random.nextInt());
			}

			// build the classifier
			m_Classifiers[j].buildClassifier(bagData);
		}

		// calc OOB error?
		if (getCalcOutOfBag()) {
			double outOfBagCount = 0.0;
			double errorSum = 0.0;
			boolean numeric = data.classAttribute().isNumeric();

			for (int i = 0; i < data.numInstances(); i++) {
				double vote;
				double[] votes;
				if (numeric)
					votes = new double[1];
				else
					votes = new double[data.numClasses()];

				// determine predictions for instance
				int voteCount = 0;
				for (int j = 0; j < m_Classifiers.length; j++) {
					if (inBag[j][i])
						continue;

					voteCount++;
					// double pred = m_Classifiers[j].classifyInstance(data.instance(i));
					if (numeric) {
						// votes[0] += pred;
						votes[0] = m_Classifiers[j].classifyInstance(data.instance(i));
					} else {
						// votes[(int) pred]++;
						double[] newProbs = m_Classifiers[j].distributionForInstance(data
								.instance(i));
						// average the probability estimates
						for (int k = 0; k < newProbs.length; k++) {
							votes[k] += newProbs[k];
						}
					}
				}

				// "vote"
				if (numeric) {
					vote = votes[0];
					if (voteCount > 0) {
						vote /= voteCount; // average
					}
				} else {
					if (Utils.eq(Utils.sum(votes), 0)) {
					} else {
						Utils.normalize(votes);
					}
					vote = Utils.maxIndex(votes); // predicted class
				}

				// error for instance
				outOfBagCount += data.instance(i).weight();
				if (numeric) {
					errorSum += StrictMath.abs(vote - data.instance(i).classValue())
							* data.instance(i).weight();
				} else {
					if (vote != data.instance(i).classValue())
						errorSum += data.instance(i).weight();
				}
			}

			m_OutOfBagError = errorSum / outOfBagCount;
		} else {
			m_OutOfBagError = 0;
		}
	}
}
