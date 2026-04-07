/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamilsongrasp;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author iure
 */
public class Grasp {
    
    public Solution executeGrasp (ModelAvaliator avaliator, ArrayList<Type> definitions,int graspIteration,int graspConstructionIterations, int localsearchIterarions, double rcl,long time){
 
        System.out.println((new Date()).toString());
        
         GenerateRamdown generateRamdown = new GenerateRamdown();
        
        Solution minCostSolution = generateRamdown.generateValue((ArrayList<Type>) Utils.copy(definitions));
        minCostSolution.putCost(Double.MAX_VALUE);
        
        Construction construction = new Construction();
        LocalSearch localSearch = new LocalSearch();
        
        for (int i = 0; i < graspIteration; i++) {
            
            Solution candidate = construction.runConstruction(avaliator, definitions, graspConstructionIterations, rcl, time);
            
            candidate = localSearch.runLocalSearch(avaliator, definitions, localsearchIterarions, candidate, true, time);
            
            if(candidate.isValid()&&candidate.getCost()<minCostSolution.getCost()){
                minCostSolution= candidate;
                minCostSolution.printParameter(true, true);
                //avaliator.printDefinition();
                System.out.println((new Date()).toString());
            }
            
        }
        
    return minCostSolution;
}
    
}
