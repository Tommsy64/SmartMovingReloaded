/*
* Smart Moving Reloaded
* Copyright (C) 2018  Tommsy64
*
* Smart Moving Reloaded is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Smart Moving Reloaded is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Smart Moving Reloaded.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.tommsy.smartmoving.common.statistics;

import com.tommsy.smartmoving.common.SmartMovingEntityLivingBase;

/**
 * @author Divisor, Tommsy64
 */
public class SmartStatisticsData {
    public float prevLegYaw;
    public float legYaw;
    public float total;

    public float getCurrentSpeed(float renderPartialTicks) {
        return Math.min(1.0F, prevLegYaw + (legYaw - prevLegYaw) * renderPartialTicks);
    }

    public float getTotalDistance(float renderPartialTicks) {
        return total - legYaw * (1.0F - renderPartialTicks);
    }

    public void initialize(SmartStatisticsData previous) {
        prevLegYaw = previous.legYaw;
        legYaw = previous.legYaw;
        total = previous.total;
    }

    public float calculate(float distance) {
        distance = distance * 4F;

        legYaw += (distance - legYaw) * 0.4F;
        total += legYaw;

        return distance;
    }

    public void apply(SmartMovingEntityLivingBase entity) {
        entity.setPrevLimbSwingAmount(prevLegYaw);
        entity.setLimbSwingAmount(legYaw);
        entity.setLimbSwing(total);
    }
}