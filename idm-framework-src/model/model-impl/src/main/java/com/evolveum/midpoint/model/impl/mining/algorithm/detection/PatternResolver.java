/*
 * Copyright (C) 2010-2023 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.model.impl.mining.algorithm.detection;

import com.evolveum.midpoint.common.mining.objects.chunk.MiningRoleTypeChunk;
import com.evolveum.midpoint.common.mining.objects.chunk.MiningUserTypeChunk;
import com.evolveum.midpoint.common.mining.objects.detection.DetectedPattern;
import com.evolveum.midpoint.common.mining.objects.detection.DetectionOption;
import com.evolveum.midpoint.common.mining.objects.handler.RoleAnalysisProgressIncrement;

import java.io.Serializable;
import java.util.*;

import static com.evolveum.midpoint.common.mining.utils.ExtractPatternUtils.prepareDetectedPattern;

public class PatternResolver implements DetectionOperation, Serializable {

    @Override
    public List<DetectedPattern> performUserBasedDetection(List<MiningRoleTypeChunk> miningRoleTypeChunks,
            DetectionOption detectionOption, RoleAnalysisProgressIncrement handler) {

        handler.enterNewStep("Data Preparation");
        handler.setActive(true);
        handler.setOperationCountToProcess(miningRoleTypeChunks.size());

        double minFrequency = detectionOption.getMinFrequencyThreshold() / 100;
        double maxFrequency = detectionOption.getMaxFrequencyThreshold() / 100;
        int minIntersection = detectionOption.getMinUsers();
        int minOccupancy = detectionOption.getMinRoles();

        List<DetectedPattern> intersections = new ArrayList<>();
        List<MiningRoleTypeChunk> preparedObjects = new ArrayList<>();

        for (MiningRoleTypeChunk miningRoleTypeChunk : miningRoleTypeChunks) {
            handler.iterateActualStatus();

            double frequency = miningRoleTypeChunk.getFrequency();
            if (frequency < minFrequency || frequency > maxFrequency) {
                continue;
            }
            Set<String> members = new HashSet<>(miningRoleTypeChunk.getUsers());
            if (members.size() < minIntersection) {
                continue;
            }
            preparedObjects.add(miningRoleTypeChunk);

            Set<String> properties = new HashSet<>(miningRoleTypeChunk.getRoles());
            int propertiesCount = properties.size();
            if (propertiesCount >= minOccupancy) {
                intersections.add(prepareDetectedPattern(properties, members));
            }

        }

        handler.enterNewStep("Outer Detection");
        handler.setOperationCountToProcess(preparedObjects.size());

        Set<List<String>> outerIntersections = new HashSet<>();
        for (int i = 0; i < preparedObjects.size(); i++) {
            handler.iterateActualStatus();

            Set<String> pointsA = new HashSet<>(preparedObjects.get(i).getUsers());
            for (int j = i + 1; j < preparedObjects.size(); j++) {
                Set<String> intersection = new HashSet<>(preparedObjects.get(j).getUsers());
                intersection.retainAll(pointsA);

                if (intersection.size() >= minIntersection) {
                    List<String> nInter = new ArrayList<>(intersection);
                    Collections.sort(nInter);
                    outerIntersections.add(nInter);
                }

            }
        }

        List<List<String>> outerIntersectionsList = new ArrayList<>(outerIntersections);

        handler.enterNewStep("Inner Detection");
        handler.setOperationCountToProcess(outerIntersectionsList.size());
        Set<List<String>> innerIntersections = new HashSet<>();
        for (int i = 0; i < outerIntersectionsList.size(); i++) {
            handler.iterateActualStatus();

            Set<String> pointsA = new HashSet<>(outerIntersectionsList.get(i));
            for (int j = i + 1; j < outerIntersectionsList.size(); j++) {
                Set<String> intersection = new HashSet<>(outerIntersectionsList.get(j));
                intersection.retainAll(pointsA);

                if (intersection.size() >= minIntersection) {
                    List<String> nInter = new ArrayList<>(intersection);
                    Collections.sort(nInter);
                    innerIntersections.add(nInter);
                }

            }
        }
        handler.enterNewStep("Inner Pattern Preparation");
        handler.setOperationCountToProcess(outerIntersectionsList.size());

        for (List<String> members : outerIntersectionsList) {
            handler.iterateActualStatus();

            Set<String> properties = new HashSet<>();
            for (MiningRoleTypeChunk preparedObject : preparedObjects) {
                Set<String> basicUsers = new HashSet<>(preparedObject.getUsers());
                if (basicUsers.containsAll(members)) {
                    properties.addAll(preparedObject.getRoles());
                }
            }

            int propertiesCount = properties.size();
            if (propertiesCount >= minOccupancy) {
                intersections.add(prepareDetectedPattern(properties, new HashSet<>(members)));
            }
        }

        handler.enterNewStep("Outer Pattern Preparation");
        handler.setOperationCountToProcess(innerIntersections.size());

        for (List<String> members : innerIntersections) {
            handler.iterateActualStatus();

            Set<String> properties = new HashSet<>();
            if (outerIntersectionsList.contains(members)) {
                continue;
            }

            for (MiningRoleTypeChunk preparedObject : preparedObjects) {
                Set<String> basicUsers = new HashSet<>(preparedObject.getUsers());
                if (basicUsers.containsAll(members)) {
                    properties.addAll(preparedObject.getRoles());
                }
            }

            int propertiesCount = properties.size();
            if (propertiesCount >= minOccupancy) {
                intersections.add(prepareDetectedPattern(properties, new HashSet<>(members)));
            }
        }

        return intersections;
    }

    @Override
    public List<DetectedPattern> performRoleBasedDetection(List<MiningUserTypeChunk> miningUserTypeChunks,
            DetectionOption roleAnalysisSessionDetectionOptionType, RoleAnalysisProgressIncrement handler) {

        handler.enterNewStep("Data Preparation");
        handler.setActive(true);
        handler.setOperationCountToProcess(miningUserTypeChunks.size());

        double minFrequency = roleAnalysisSessionDetectionOptionType.getMinFrequencyThreshold() / 100;
        double maxFrequency = roleAnalysisSessionDetectionOptionType.getMaxFrequencyThreshold() / 100;

        int minIntersection = roleAnalysisSessionDetectionOptionType.getMinRoles();
        int minOccupancy = roleAnalysisSessionDetectionOptionType.getMinUsers();

        List<DetectedPattern> intersections = new ArrayList<>();
        List<MiningUserTypeChunk> preparedObjects = new ArrayList<>();
        for (MiningUserTypeChunk miningUserTypeChunk : miningUserTypeChunks) {
            handler.iterateActualStatus();

            double frequency = miningUserTypeChunk.getFrequency();
            if (frequency < minFrequency || frequency > maxFrequency) {
                continue;
            }

            Set<String> members = new HashSet<>(miningUserTypeChunk.getRoles());
            if (members.size() < minIntersection) {
                continue;
            }

            preparedObjects.add(miningUserTypeChunk);

            Set<String> properties = new HashSet<>(miningUserTypeChunk.getUsers());
            int propertiesCount = miningUserTypeChunk.getUsers().size();
            if (propertiesCount >= minOccupancy) {
                intersections.add(prepareDetectedPattern(members, properties));
            }
        }

        handler.enterNewStep("Outer Pattern Detection");
        handler.setOperationCountToProcess(preparedObjects.size());

        Set<List<String>> outerIntersections = new HashSet<>();
        for (int i = 0; i < preparedObjects.size(); i++) {
            handler.iterateActualStatus();

            Set<String> pointsA = new HashSet<>(preparedObjects.get(i).getRoles());
            for (int j = i + 1; j < preparedObjects.size(); j++) {
                Set<String> intersection = new HashSet<>(preparedObjects.get(j).getRoles());
                intersection.retainAll(pointsA);

                if (intersection.size() >= minIntersection) {
                    List<String> nInter = new ArrayList<>(intersection);
                    Collections.sort(nInter);
                    outerIntersections.add(nInter);
                }

            }

        }

        List<List<String>> outerIntersectionsList = new ArrayList<>(outerIntersections);
        handler.enterNewStep("Inner Pattern Detection");
        handler.setOperationCountToProcess(outerIntersectionsList.size());

        Set<List<String>> innerIntersections = new HashSet<>();
        for (int i = 0; i < outerIntersectionsList.size(); i++) {
            handler.iterateActualStatus();

            Set<String> pointsA = new HashSet<>(outerIntersectionsList.get(i));
            for (int j = i + 1; j < outerIntersectionsList.size(); j++) {
                Set<String> intersection = new HashSet<>(outerIntersectionsList.get(j));
                intersection.retainAll(pointsA);

                if (intersection.size() >= minIntersection) {
                    List<String> nInter = new ArrayList<>(intersection);
                    Collections.sort(nInter);
                    innerIntersections.add(nInter);
                }

            }
        }

        handler.enterNewStep("Outer Pattern Preparation");
        handler.setOperationCountToProcess(outerIntersectionsList.size());

        for (List<String> members : outerIntersectionsList) {
            handler.iterateActualStatus();

            Set<String> properties = new HashSet<>();
            for (MiningUserTypeChunk preparedObject : preparedObjects) {
                Set<String> basicUsers = new HashSet<>(preparedObject.getRoles());
                if (basicUsers.containsAll(members)) {
                    properties.addAll(preparedObject.getUsers());
                }
            }

            int propertiesCount = properties.size();
            if (propertiesCount >= minOccupancy) {
                intersections.add(prepareDetectedPattern(new HashSet<>(members), properties));
            }
        }

        handler.enterNewStep("Inner Pattern Preparation");
        handler.setOperationCountToProcess(innerIntersections.size());
        for (List<String> members : innerIntersections) {
            handler.iterateActualStatus();

            int propertiesCount = 0;

            if (outerIntersectionsList.contains(members)) {
                continue;
            }

            Set<String> properties = new HashSet<>();
            for (MiningUserTypeChunk preparedObject : preparedObjects) {
                Set<String> basicUsers = new HashSet<>(preparedObject.getRoles());
                if (basicUsers.containsAll(members)) {
                    properties.addAll(preparedObject.getUsers());
                }
            }

            if (propertiesCount >= minOccupancy) {
                intersections.add(prepareDetectedPattern(new HashSet<>(members), properties));
            }

        }
        return intersections;
    }

}
