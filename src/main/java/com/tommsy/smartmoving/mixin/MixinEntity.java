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

package com.tommsy.smartmoving.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.tommsy.smartmoving.common.SmartMovingEntity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

@Mixin(Entity.class)
public abstract class MixinEntity extends Entity implements SmartMovingEntity {

    public MixinEntity() {
        super(null);
    }

    public MixinEntity(World worldIn) {
        super(worldIn);
    }

    @Shadow
    @Override
    public abstract AxisAlignedBB getEntityBoundingBox();

    @Override
    public double getPosX() {
        return this.posX;
    }

    @Override
    public double getPosY() {
        return this.posY;
    }

    @Override
    public double getPosZ() {
        return this.posZ;
    }

    @Override
    public double getPrevPosX() {
        return this.prevPosX;
    }

    @Override
    public double getPrevPosY() {
        return this.prevPosY;
    }

    @Override
    public double getPrevPosZ() {
        return this.prevPosZ;
    }

    @Override
    public double getDeltaX() {
        return this.posX - this.prevPosX;
    }

    @Override
    public double getDeltaY() {
        return this.posY - this.prevPosY;
    }

    @Override
    public double getDeltaZ() {
        return this.posZ - this.prevPosZ;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public List<AxisAlignedBB> getIntersectingCollisionBoxes(AxisAlignedBB aabb) {
        return this.world.getCollisionBoxes(this, aabb);
    }
}