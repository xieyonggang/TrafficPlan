/*
 * Copyright (C) 2010, 2011, 2012 by Arne Kesting, Martin Treiber, Ralph Germ, Martin Budden
 *                                   <movsim.org@gmail.com>
 * -----------------------------------------------------------------------------------------
 * 
 * This file is part of
 * 
 * MovSim - the multi-model open-source vehicular-traffic simulator.
 * 
 * MovSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MovSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MovSim. If not, see <http://www.gnu.org/licenses/>
 * or <http://www.movsim.org>.
 * 
 * -----------------------------------------------------------------------------------------
 */
package org.movsim.output.detector;

import org.movsim.simulator.MovsimConstants;
import org.movsim.simulator.SimulationTimeStep;
import org.movsim.simulator.roadnetwork.LaneSegment;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.movsim.simulator.vehicles.Vehicle;
import org.movsim.utilities.ObservableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class LoopDetector.
 */
public class LoopDetector extends ObservableImpl implements SimulationTimeStep {

    final static Logger logger = LoggerFactory.getLogger(LoopDetector.class);

    private final RoadSegment roadSegment;
    private final double dtSample;

    private final double detPosition;

    private double timeOffset;

    private final int[] vehCount;
    
    private final double[] vSum;

    private final double[] occTime;

    private final double[] sumInvV;

    private final double[] sumInvQ;

    private final double[] meanSpeed;

    private final double[] occupancy;

    private final int[] vehCountOutput;
    
    private final long[] vehCumulatedCountOutput;

    private final double[] meanSpeedHarmonic;

    private final double[] meanTimegapHarmonic;

    private int laneCount;

    private double meanSpeedAllLanes;

    /** vehicle count per update aggregation cycle. */
    private int vehCountOutputAllLanes;
        
    /** Cumulated vehicle count. */
    private long vehCumulatedCountOutputAllLanes;

    private double occupancyAllLanes;

    private double meanSpeedHarmonicAllLanes;

    private double meanTimegapHarmonicAllLanes;

    /**
     * Constructor.
     * 
     * @param detPosition
     * @param dtSample
     * @param laneCount
     */
    public LoopDetector(RoadSegment roadSegment, double detPosition, double dtSample) {
        this.roadSegment = roadSegment;
        this.detPosition = detPosition;
        this.dtSample = dtSample;
        laneCount = roadSegment.laneCount();

        vehCount = new int[laneCount];
        vSum = new double[laneCount];
        occTime = new double[laneCount];
        sumInvQ = new double[laneCount];
        sumInvV = new double[laneCount];

        meanSpeed = new double[laneCount];
        occupancy = new double[laneCount];
        vehCountOutput = new int[laneCount];
        meanSpeedHarmonic = new double[laneCount];
        meanTimegapHarmonic = new double[laneCount];
        
        vehCumulatedCountOutput = new long[laneCount];
        vehCumulatedCountOutputAllLanes = 0;

        timeOffset = 0;

        for (int i = 0; i < laneCount; i++) {
            reset(i);
            vehCumulatedCountOutput[i] = 0;  // initalization
        }
        resetLaneAverages();
        notifyObservers(0);
    }

    private void resetLaneAverages() {
        meanSpeedAllLanes = 0;
        vehCountOutputAllLanes = 0;
        occupancyAllLanes = 0;
        meanSpeedHarmonicAllLanes = 0;
        meanTimegapHarmonicAllLanes = 0;
    }

    private void reset(int lane) {
        vehCount[lane] = 0;
        vSum[lane] = 0;
        occTime[lane] = 0;
        sumInvQ[lane] = 0;
        sumInvV[lane] = 0;
    }

    @Override
    public void timeStep(double dt, double simulationTime, long iterationCount) {
        // brute force search: iterate over all lanes
        final int laneCount = roadSegment.laneCount();
        for (int lane = 0; lane < laneCount; ++lane) {
            final LaneSegment laneSegment = roadSegment.laneSegment(lane);
            for (final Vehicle veh : laneSegment) {
                if ((veh.getFrontPositionOld() < detPosition) && (veh.getFrontPosition() >= detPosition)) {
                    countVehiclesAndDataForLane(laneSegment, lane, veh);
                }
            }
        }

        if ((simulationTime - timeOffset + MovsimConstants.SMALL_VALUE) >= dtSample) {
            for (int lane = 0; lane < laneCount; lane++) {
                calculateAveragesForLane(lane);
            }
            calculateAveragesOverAllLanes();
            notifyObservers(simulationTime);
            timeOffset = simulationTime;
        }
    }

    /**
     * @param laneSegment
     * @param lane
     * @param veh
     */
    private void countVehiclesAndDataForLane(LaneSegment laneSegment, int lane, Vehicle veh) {
        // new vehicle crossed detector
        vehCount[lane]++;
        vehCumulatedCountOutput[lane]++;
        final double speedVeh = veh.getSpeed();
        vSum[lane] += speedVeh;
        occTime[lane] += (speedVeh > 0) ? veh.getLength() / speedVeh : 0;
        sumInvV[lane] += (speedVeh > 0) ? 1. / speedVeh : 0;
        // brut timegap not calculate from local detector data:
        final Vehicle vehFront = laneSegment.frontVehicle(veh);
        final double brutTimegap = (vehFront == null) ? 0 : veh.getBrutDistance(vehFront) / vehFront.getSpeed();
        // "microscopic flow"
        sumInvQ[lane] += (brutTimegap > 0) ? 1. / brutTimegap : 0;
    }

    /**
     * Calculate averages.
     * 
     * @param lane
     */
    private void calculateAveragesForLane(int lane) {
        meanSpeed[lane] = (vehCount[lane] == 0) ? 0 : vSum[lane] / vehCount[lane];
        occupancy[lane] = occTime[lane] / dtSample;
        vehCountOutput[lane] = vehCount[lane];
        vehCumulatedCountOutput[lane] += vehCount[lane];
        meanSpeedHarmonic[lane] = (vehCount[lane] == 0) ? 0 : 1. / (sumInvV[lane] / vehCount[lane]);
        meanTimegapHarmonic[lane] = (vehCount[lane] == 0) ? 0 : sumInvQ[lane] / vehCount[lane];
        reset(lane);
    }

    private void calculateAveragesOverAllLanes() {
        resetLaneAverages();
        for (int i = 0; i < laneCount; i++) {
            // vehicle count is extensive quantity
            vehCountOutputAllLanes += getVehCountOutput(i);
            // intensive quantities as averages weighted by vehicle counts
            meanSpeedAllLanes += getVehCountOutput(i) * getMeanSpeed(i);
            occupancyAllLanes += getOccupancy(i);
            meanSpeedHarmonicAllLanes += getVehCountOutput(i) * getMeanSpeedHarmonic(i);
            meanTimegapHarmonicAllLanes += getVehCountOutput(i) * getMeanTimegapHarmonic(i);
        }
        
        vehCumulatedCountOutputAllLanes += vehCountOutputAllLanes;

        meanSpeedAllLanes = (vehCountOutputAllLanes==0) ? 0 : meanSpeedAllLanes/vehCountOutputAllLanes;
        meanSpeedHarmonicAllLanes = (vehCountOutputAllLanes==0) ? 0 : meanSpeedHarmonicAllLanes/vehCountOutputAllLanes;
        meanTimegapHarmonicAllLanes = (vehCountOutputAllLanes==0) ? 0 : meanTimegapHarmonicAllLanes/vehCountOutputAllLanes;
        occupancyAllLanes /= laneCount;
    }

    public double getDensityArithmetic(int i) {
        return (Double.compare(meanSpeed[i], 0) == 0) ? 0 : getFlow(i) / meanSpeed[i];
    }

    public double getDensityArithmeticAllLanes() {
        return (Double.compare(meanSpeedAllLanes, 0) == 0) ? 0 : getFlowAllLanes() / meanSpeedAllLanes;
    }

    public double getFlow(int i) {
        return vehCountOutput[i] / dtSample;
    }

    public double getFlowAllLanes() {
        return vehCountOutputAllLanes / (dtSample * laneCount);
    }

    public double getDtSample() {
        return dtSample;
    }

    public double getDetPosition() {
        return detPosition;
    }

    public double getMeanSpeed(int i) {
        return meanSpeed[i];
    }

    public double getOccupancy(int i) {
        return occupancy[i];
    }

    public int getVehCountOutput(int i) {
        return vehCountOutput[i];
    }
    
    public double getMeanSpeedHarmonic(int i) {
        return meanSpeedHarmonic[i];
    }

    public double getMeanTimegapHarmonic(int i) {
        return meanTimegapHarmonic[i];
    }

    public double getMeanSpeedAllLanes() {
        return meanSpeedAllLanes;
    }

    public int getVehCountOutputAllLanes() {
        return vehCountOutputAllLanes;
    }

    public double getOccupancyAllLanes() {
        return occupancyAllLanes;
    }

    public double getMeanSpeedHarmonicAllLanes() {
        return meanSpeedHarmonicAllLanes;
    }

    public double getMeanTimegapHarmonicAllLanes() {
        return meanTimegapHarmonicAllLanes;
    }

    public long getVehCumulatedCountOutputAllLanes() {
        return vehCumulatedCountOutputAllLanes;
    }

    public long getVehCumulatedCountOutput(int index) {
        return vehCumulatedCountOutput[index];
    }
}