/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author iure
 */
public class LocalSearch {

    public Solution runLocalSearch(ModelAvaliator modelAvaliator, ArrayList<Type> definitions, int maxIterations, Solution constructionSolution, boolean firstImprovement, long time) {

        Solution minSolution = constructionSolution;
        GenerateRamdown generateRamdown = new GenerateRamdown();

        int count = 0;

        while (maxIterations < count) {

            Solution sollocal = generateRamdown.getNeighbor(constructionSolution, definitions);

            modelAvaliator.setAllDefinition(sollocal);
            
            HashMap<String, Double> metrics = modelAvaliator.stationarySimulationSolve(time);


            sollocal.setMetrics(metrics);

            if(sollocal.getCost()<minSolution.getCost()&&sollocal.isValid()){
                minSolution = sollocal;
                if(firstImprovement){
                    return minSolution;
                }
            }
            count++;

        }

        return minSolution;
    }

}
