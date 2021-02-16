package WEKA;

import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.neighboursearch.LinearNNSearch;

import java.util.Random;

public class kNN_EKORKETA {

    private static Instances data;

    public static void main(String[] args) throws Exception {
        int kEstimatu = Integer.parseInt("10");
        datuak_Kargatu("");
        //K_Parametroaren_Ekorketa(kEstimatu);
        parametroGuztienEkorketa(kEstimatu);
    }

    private static void parametroGuztienEkorketa(int kEstimatu) throws Exception {
        LinearNNSearch[] distantziak = distantziakLortu();
        SelectedTag[] tags = tagsLortu();

        double maxMeasure = 0.0;
        int k = 1;
        int w = 0;
        IBk knn = new IBk();

        for(int i=1; i<=kEstimatu; i++){
            knn.setKNN(i);

            for(int j=0; j<distantziak.length; j++){
                knn.setNearestNeighbourSearchAlgorithm(distantziak[j]);

                for(int l=0; l<tags.length; l++) {
                    knn.setDistanceWeighting(tags[l]);

                    Evaluation evaluation = new Evaluation(data);
                    evaluation.crossValidateModel(knn, data, 10, new Random(1));
                    if (evaluation.weightedFMeasure() > maxMeasure) {
                        maxMeasure = evaluation.weightedFMeasure();
                        k = i;
                        w = j;
                    }
                }
            }
        }
        System.out.println("K hoberena -> " + k);
    }

    private static SelectedTag[] tagsLortu() {
        SelectedTag[] tags = new SelectedTag[3];

        SelectedTag none = new SelectedTag(IBk.WEIGHT_NONE,IBk.TAGS_WEIGHTING);
        tags[0] = none;

        SelectedTag inverse = new SelectedTag(IBk.WEIGHT_INVERSE,IBk.TAGS_WEIGHTING);
        tags[1] = inverse;

        SelectedTag similarity = new SelectedTag(IBk.WEIGHT_SIMILARITY,IBk.TAGS_WEIGHTING);
        tags[2] = similarity;

        return tags;
    }

    private static LinearNNSearch[] distantziakLortu() throws Exception {
        LinearNNSearch[] dist = new LinearNNSearch[3];

        LinearNNSearch euclide = new LinearNNSearch();
        euclide.setDistanceFunction(new EuclideanDistance());
        dist[0] = euclide;

        LinearNNSearch manhattan = new LinearNNSearch();
        manhattan.setDistanceFunction(new ManhattanDistance());
        dist[1] = manhattan;

        LinearNNSearch minkowski = new LinearNNSearch();
        minkowski.setDistanceFunction(new MinkowskiDistance());
        dist[2] = minkowski;

        return dist;
    }

    private static void K_Parametroaren_Ekorketa(int kEstimatu) throws Exception {
        double maxMeasure = 0.0;
        int k = 1;
        for(int i=1; i<=kEstimatu; i++) {
            IBk knn = new IBk();
            knn.buildClassifier(data);
            knn.setKNN(i);

            Evaluation evaluation = new Evaluation(data);
            evaluation.crossValidateModel(knn,data,10,new Random(1));
            System.out.println(evaluation.weightedFMeasure());
            if(evaluation.weightedFMeasure() > maxMeasure) {
                maxMeasure = evaluation.weightedFMeasure();
                k=i;
            }
        }
        System.out.println("\nEMAITZA -> " + maxMeasure + " ETA K -> " + k);
    }

    private static void datuak_Kargatu(String path) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);
        data = source.getDataSet();
        if(data.classIndex() == -1) data.setClassIndex(data.numAttributes()-1);
    }
}
