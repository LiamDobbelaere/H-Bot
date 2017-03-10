package net.digaly.suicide.model;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.Enumeration;

/**
 * Created by tomdo on 10/03/2017.
 */
public class SuicideClassifier {
    private static SuicideClassifier instance;

    public static boolean hasInstance() {
        return instance != null;
    }

    public static SuicideClassifier getInstance(Object classifier, ConverterUtils.DataSource dataSource) {
        if (instance == null) {
            instance = new SuicideClassifier(classifier, dataSource);
        }

        return instance;
    }

    private Classifier classifier;
    private Instances data;

    private SuicideClassifier(Object classifier, ConverterUtils.DataSource dataSource) {
        try {
            this.classifier = (Classifier) classifier;
            this.data = dataSource.getDataSet();

            if (this.data.classIndex() == -1)
            {
                this.data.setClassIndex(this.data.numAttributes() - 1);
            }
            /*System.out.println(System.getProperty("user.dir"));
            classifier = (MultilayerPerceptron) weka.core.SerializationHelper.read("suicide.classifier");

            ConverterUtils.DataSource source = new ConverterUtils.DataSource("reddithelp.arff");
            data = source.getDataSet();

            if (data.classIndex() == -1)
            {
                data.setClassIndex(data.numAttributes() - 1);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double classifiyString(String text) {
        DenseInstance di = new DenseInstance(data.numAttributes());
        di.setDataset(data);

        Enumeration<Attribute> ea = data.enumerateAttributes();

        while (ea.hasMoreElements()) {
            Attribute next = ea.nextElement();
            di.setValue(next.index(), 0);
        }

        text = text.toLowerCase();
        text = text.replaceAll("[^\\p{L}\\p{Z}]","");
        for (String s : text.split(" ")) {
            if (data.attribute(s) != null) {
                di.setValue(data.attribute(s), 1);
            }
        }

        double cl = 0;
        try {
            di.setClassValue(classifier.classifyInstance(di));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return di.classValue();
    }
}
