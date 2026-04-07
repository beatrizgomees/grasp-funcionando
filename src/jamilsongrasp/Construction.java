/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author iure
 */
public class Construction {

    public Solution runConstruction(ModelAvaliator modelAvaliator, ArrayList<Type> definitions, int constructionIteraction,double greesyness, long time) {

        GenerateRamdown generateRamdown = new GenerateRamdown();

        Solution minCostSolution = generateRamdown.generateValue((ArrayList<Type>) Utils.copy(definitions));
        
        minCostSolution.putCost(Double.MAX_VALUE);
        
        ArrayList<Solution> RLC = new ArrayList<>();
        
        int count=0;

        while ( count < constructionIteraction) {

            Solution candidate = generateRamdown.generateValue((ArrayList<Type>) Utils.copy(definitions));
            modelAvaliator.setAllDefinition(candidate);

            HashMap<String, Double> metrics = modelAvaliator.stationarySimulationSolve(time);

            candidate.setMetrics(metrics);

            if (candidate.isValid()) {

                //data= data+generateStringData(candidate)+"\r";
                double newCost = candidate.getCost();
                double minCost = minCostSolution.getCost();
                //System.out.println(minCostCandidate.getMetrics().get("TotalInstances"));
                if (newCost < minCost) {

                    if (!RLC.isEmpty() && (RLC.size() + 1) >= (int) constructionIteraction * (1 - greesyness)) {
                        RLC.remove(RLC.size() - 1);
                    }

                    RLC.add(candidate);

                    RLC.sort((a, b) -> a.getCost() < b.getCost() ? -1 : a.getCost() == b.getCost() ? 0 : 1);

                    minCostSolution = RLC.get(RLC.size() - 1);
                }

                count++;

            }

        }
        
        Solution solution = RLC.get((new Random()).nextInt(RLC.size()));
        
        return solution;
    }

}
