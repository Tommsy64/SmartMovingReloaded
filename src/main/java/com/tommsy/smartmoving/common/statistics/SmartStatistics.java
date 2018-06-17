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

import com.tommsy.smartmoving.client.SmartMovingAbstractClientPlayer;

import lombok.Setter;
import net.minecraft.util.math.MathHelper;

/**
 * @author Divisor, Tommsy64
 */
public class SmartStatistics {

    @Setter
    protected static boolean calculateHorizontalStats = false;

    private final SmartMovingAbstractClientPlayer player;

    private float tickDistance;

    public int ticksRiding;
    public float prevHorizontalAngle = Float.NaN;

    private final static SmartStatisticsDatas dummy = new SmartStatisticsDatas();
    private final SmartStatisticsDatas[] datas = new SmartStatisticsDatas[10];
    private int currentDataIndex = -1;

    public SmartStatistics(SmartMovingAbstractClientPlayer player) {
        this.player = player;
    }

    public void calculateAllStats(boolean isRemote) {
        double diffX = player.getDeltaX();
        double diffY = player.getDeltaY();
        double diffZ = player.getDeltaZ();

        SmartStatisticsDatas previous = get();

        currentDataIndex++;
        if (currentDataIndex >= datas.length)
            currentDataIndex = 0;

        SmartStatisticsDatas data = datas[currentDataIndex];
        if (data == null)
            data = datas[currentDataIndex] = new SmartStatisticsDatas();
        data.initialize(previous);

        data.horizontal.calculate(MathHelper.sqrt(diffX * diffX + diffZ * diffZ));
        data.vertical.calculate((float) Math.abs(diffY));
        tickDistance = data.all.calculate(MathHelper.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ));

        if (calculateHorizontalStats && !isRemote)
            data.horizontal.apply(player);
    }

    public void calculateRiddenStats() {
        ticksRiding++;
    }

    public float getHorizontalPrevLegYaw() {
        return player.getPrevLimbSwingAmount();
    }

    public float getHorizontalLegYaw() {
        return player.getLimbSwingAmount();
    }

    public float getHorizontalTotal() {
        return player.getLimbSwing();
    }

    public float getVerticalPrevLegYaw() {
        return datas[currentDataIndex].vertical.prevLegYaw;
    }

    public float getVerticalLegYaw() {
        return datas[currentDataIndex].vertical.legYaw;
    }

    public float getVerticalTotal() {
        return datas[currentDataIndex].vertical.total;
    }

    public float getAllPrevLegYaw() {
        return datas[currentDataIndex].all.prevLegYaw;
    }

    public float getAllLegYaw() {
        return datas[currentDataIndex].all.legYaw;
    }

    public float getAllTotal() {
        return datas[currentDataIndex].all.total;
    }

    public float getTickDistance() {
        return tickDistance;
    }

    public float getTotalHorizontalDistance(float renderPartialTicks) {
        return get(renderPartialTicks).getTotalHorizontalDistance();
    }

    public float getTotalVerticalDistance(float renderPartialTicks) {
        return get(renderPartialTicks).getTotalVerticalDistance();
    }

    public float getTotalDistance(float renderPartialTicks) {
        return get(renderPartialTicks).getTotalDistance();
    }

    public float getCurrentHorizontalSpeed(float renderPartialTicks) {
        return get(renderPartialTicks).getCurrentHorizontalSpeed();
    }

    public float getCurrentVerticalSpeed(float renderPartialTicks) {
        return get(renderPartialTicks).getCurrentVerticalSpeed();
    }

    public float getCurrentSpeed(float renderPartialTicks) {
        return get(renderPartialTicks).getCurrentSpeed();
    }

    private SmartStatisticsDatas get() {
        return currentDataIndex == -1 ? dummy : datas[currentDataIndex];
    }

    private SmartStatisticsDatas get(float renderPartialTicks) {
        SmartStatisticsDatas data = get();
        data.setReady(renderPartialTicks);
        return data;
    }

    public float getCurrentHorizontalSpeedFlattened(float renderPartialTicks, int strength) {
        strength = Math.min(strength, datas.length);
        if (strength < 0)
            strength = datas.length;

        get(renderPartialTicks);
        float sum = 0;
        int count = 0;
        for (int i = 0, dataIndex = currentDataIndex; i < strength; i++, dataIndex--) {
            if (dataIndex < 0)
                dataIndex = datas.length - 1;
            SmartStatisticsDatas data = datas[dataIndex];
            if (data == null || !data.isReady())
                break;

            sum += data.getCurrentHorizontalSpeed();
            count++;
        }

        return sum / count;
    }

    public float getCurrentVerticalSpeedFlattened(float renderPartialTicks, int strength) {
        strength = Math.min(strength, datas.length);
        if (strength < 0)
            strength = datas.length;

        get(renderPartialTicks);
        float sum = 0;
        int count = 0;
        for (int i = 0, dataIndex = currentDataIndex; i < strength; i++, dataIndex--) {
            if (dataIndex < 0)
                dataIndex = datas.length - 1;
            SmartStatisticsDatas data = datas[dataIndex];
            if (data == null || !data.isReady())
                break;

            sum += data.getCurrentVerticalSpeed();
            count++;
        }

        return sum / count;
    }

    public float getCurrentSpeedFlattened(float renderPartialTicks, int strength) {
        strength = Math.min(strength, datas.length);
        if (strength < 0)
            strength = datas.length;

        get(renderPartialTicks);
        float sum = 0;
        int count = 0;
        for (int i = 0, dataIndex = currentDataIndex; i < strength; i++, dataIndex--) {
            if (dataIndex < 0)
                dataIndex = datas.length - 1;
            SmartStatisticsDatas data = datas[dataIndex];
            if (data == null || !data.isReady())
                break;

            sum += data.getCurrentSpeed();
            count++;
        }

        return sum / count;
    }
}
