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

package com.tommsy.smartmoving.client.render;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tommsy.smartmoving.config.SmartMovingConfig;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Orientation {
    public static final Set<Orientation> Orthogonals = new HashSet<Orientation>();
    public static final Set<Orientation> Diagonals = new HashSet<Orientation>();
    public static final Set<Orientation> All = new HashSet<Orientation>();
    private static final Map<EnumFacing, Orientation> FacingToOrientation = new HashMap<EnumFacing, Orientation>();

    public static final Orientation ZZ = new Orientation(0, 0);

    public static final Orientation PZ = new Orientation(1, 0, EnumFacing.WEST);
    public static final Orientation ZP = new Orientation(0, 1, EnumFacing.NORTH);
    public static final Orientation NZ = new Orientation(-1, 0, EnumFacing.EAST);
    public static final Orientation ZN = new Orientation(0, -1, EnumFacing.SOUTH);

    public static final Orientation PP = new Orientation(1, 1, EnumFacing.NORTH, EnumFacing.WEST);
    public static final Orientation NN = new Orientation(-1, -1, EnumFacing.SOUTH, EnumFacing.EAST);
    public static final Orientation PN = new Orientation(1, -1, EnumFacing.SOUTH, EnumFacing.WEST);
    public static final Orientation NP = new Orientation(-1, 1, EnumFacing.NORTH, EnumFacing.EAST);

    public static final int DefaultMeta = -1;
    public static final int VineFrontMeta = 0;
    public static final int VineSideMeta = 1;

    private static final int top = 2;
    private static final int middle = 1;
    private static final int base = 0;
    private static final int sub = -1;
    private static final int subSub = -2;

    private static final int NoGrab = 0;
    private static final int HalfGrab = 1;
    private static final int AroundGrab = 2;

    protected int _i, _k;
    private boolean _isDiagonal;
    private Set<EnumFacing> _facings;
    final EnumFacing _facing;
    private float _directionAngle;
    private float _mimimumClimbingAngle;
    private float _maximumClimbingAngle;

    private Orientation(int i, int k, EnumFacing... facings) {
        _i = i;
        _k = k;
        _isDiagonal = _i != 0 && _k != 0;
        setClimbingAngles();

        _facings = new HashSet<EnumFacing>();
        for (EnumFacing facing : facings)
            _facings.add(facing);

        All.add(this);
        _facing = facings.length > 0 ? facings[0] : null;
        if (facings.length == 1) {
            Orthogonals.add(this);
            FacingToOrientation.put(_facing, this);
        } else
            Diagonals.add(this);
    }

    public Orientation rotate(int angle) {
        if (this == ZZ)
            throw new RuntimeException("unrotatable orientation");

        switch (angle) {
        case 0:
            return this;
        case 45:
            if (this == PZ)
                return PP;
            if (this == PP)
                return ZP;
            if (this == ZP)
                return NP;
            if (this == NP)
                return NZ;
            if (this == NZ)
                return NN;
            if (this == NN)
                return ZN;
            if (this == ZN)
                return PN;
            if (this == PN)
                return PZ;
            throw new RuntimeException("unknown orientation \"" + this + "\"");
        case -45:
            if (this == PZ)
                return PN;
            if (this == PN)
                return ZN;
            if (this == ZN)
                return NN;
            if (this == NN)
                return NZ;
            if (this == NZ)
                return NP;
            if (this == NP)
                return ZP;
            if (this == ZP)
                return PP;
            if (this == PP)
                return PZ;
            throw new RuntimeException("unknown orientation \"" + this + "\"");
        case 90:
            return rotate(45).rotate(45);
        case -90:
            return rotate(-45).rotate(-45);
        case 135:
            return rotate(180).rotate(-45);
        case -135:
            return rotate(-180).rotate(45);
        case 180:
        case -180:
            if (this == PZ)
                return NZ;
            if (this == PN)
                return NP;
            if (this == ZN)
                return ZP;
            if (this == NN)
                return PP;
            if (this == NZ)
                return PZ;
            if (this == NP)
                return PN;
            if (this == ZP)
                return ZN;
            if (this == PP)
                return NN;
            throw new RuntimeException("unknown orientation");
        }
        throw new RuntimeException("angle \"" + angle + "\" not supported");
    }

    public static Orientation getOrientation(EntityPlayer p, float tolerance, boolean orthogonals, boolean diagonals) {
        float rotation = p.rotationYaw % 360F;
        if (rotation < 0)
            rotation += 360F;

        float minimumRotation = rotation - tolerance;
        if (minimumRotation < 0)
            minimumRotation += 360F;

        float maximumRotation = rotation + tolerance;
        if (maximumRotation >= 360F)
            maximumRotation -= 360F;

        if (orthogonals) {
            if (NZ.isWithinAngle(minimumRotation, maximumRotation))
                return NZ;
            if (PZ.isWithinAngle(minimumRotation, maximumRotation))
                return PZ;
            if (ZN.isWithinAngle(minimumRotation, maximumRotation))
                return ZN;
            if (ZP.isWithinAngle(minimumRotation, maximumRotation))
                return ZP;
        }
        if (diagonals) {
            if (NP.isWithinAngle(minimumRotation, maximumRotation))
                return NP;
            if (PN.isWithinAngle(minimumRotation, maximumRotation))
                return PN;
            if (NN.isWithinAngle(minimumRotation, maximumRotation))
                return NN;
            if (PP.isWithinAngle(minimumRotation, maximumRotation))
                return PP;
        }
        return null;
    }

    public double getHorizontalBorderGap(Entity entity) {
        return getHorizontalBorderGap(entity.posX, entity.posZ);
    }

    private double getHorizontalBorderGap() {
        return getHorizontalBorderGap(base_id, base_kd);
    }

    private double getHorizontalBorderGap(double i, double k) {
        if (this == NZ)
            return i % 1;
        if (this == PZ)
            return 1 - (i % 1);
        if (this == ZN)
            return k % 1;
        if (this == ZP)
            return 1 - (k % 1);
        return 0D;
    }

    public boolean isTunnelAhead(World world, int i, int j, int k) {
        IBlockState state = getState(world, i + _i, j + 1, k + _k);
        if (isFullEmpty(state)) {
            Material aboveMaterial = world.getBlockState(new BlockPos(i + _i, j + 2, k + _k)).getMaterial();
            if (aboveMaterial != null && isSolid(aboveMaterial))
                return true;
        }
        return false;
    }

    public static HashSet<Orientation> getClimbingOrientations(EntityPlayer p, boolean orthogonals, boolean diagonals) {
        float rotation = p.rotationYaw % 360F;
        if (rotation < 0)
            rotation += 360F;

        if (_getClimbingOrientationsHashSet == null)
            _getClimbingOrientationsHashSet = new HashSet<Orientation>();
        else
            _getClimbingOrientationsHashSet.clear();

        if (orthogonals) {
            NZ.addTo(rotation);
            PZ.addTo(rotation);
            ZN.addTo(rotation);
            ZP.addTo(rotation);
        }
        if (diagonals) {
            NP.addTo(rotation);
            PN.addTo(rotation);
            NN.addTo(rotation);
            PP.addTo(rotation);
        }
        return _getClimbingOrientationsHashSet;
    }

    private static HashSet<Orientation> _getClimbingOrientationsHashSet = null;

    private void addTo(float rotation) {
        if (isRotationForClimbing(rotation))
            _getClimbingOrientationsHashSet.add(this);
    }

    public boolean isFeetLadderSubstitute(World world, int bi, int j, int bk) {
        int i = bi + _i;
        int k = bk + _k;

        return isLadderSubstitute(world, i, j, k, middle) > 0 || isLadderSubstitute(world, i, j, k, base) > 0;
    }

    public boolean isHandsLadderSubstitute(World world, int bi, int j, int bk) {
        int i = bi + _i;
        int k = bk + _k;

        return isLadderSubstitute(world, i, j, k, middle) > 0 || isLadderSubstitute(world, i, j, k, base) > 0 || isLadderSubstitute(world, i, j, k, sub) > 0;
    }

    private int isLadderSubstitute(World worldObj, int i, int j, int k, int halfOffset) {
        world = worldObj;
        remote_i = i;
        all_j = j;
        remote_k = k;
        all_offset = 0;
        return isLadderSubstitute(halfOffset, null);
    }

    public void seekClimbGap(float rotation, World world, int i, double id, double jhd, int k, double kd, boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling,
            HandsClimbing[] inout_handsClimbing, FeetClimbing[] inout_feetClimbing, ClimbGap out_handsClimbGap, ClimbGap out_feetClimbGap) {
        if (isRotationForClimbing(rotation)) {
            initialize(world, i, id, jhd, k, kd);

            inout_handsClimbing[0] = inout_handsClimbing[0].max(handsClimbing(isClimbCrawling, isCrawlClimbing, isCrawling, _climbGapOuterTemp), out_handsClimbGap,
                    _climbGapOuterTemp);
            inout_feetClimbing[0] = inout_feetClimbing[0].max(feetClimbing(isClimbCrawling, isCrawlClimbing, isCrawling, _climbGapOuterTemp), out_feetClimbGap, _climbGapOuterTemp);
        }
    }

    private HandsClimbing handsClimbing(boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling, ClimbGap out_climbGap) {
        out_climbGap.reset();
        _climbGapTemp.reset();

        initializeOffset(3D, isClimbCrawling, isCrawlClimbing, isCrawling);

        HandsClimbing result = HandsClimbing.None;
        int gap;

        if ((gap = isLadderSubstitute(middle, _climbGapTemp)) > 0)
            if (jh_offset > 1D - _handClimbingHoldGap)
                result = result.max(HandsClimbing.Up, out_climbGap, _climbGapTemp);
            else
                result = result.max(HandsClimbing.None, out_climbGap, _climbGapTemp); // No climbing (hands not long enough - up)

        if ((gap = isLadderSubstitute(base, _climbGapTemp)) > 0)
            if (jh_offset < _handClimbingHoldGap)
                result = result.max(HandsClimbing.BottomHold, out_climbGap, _climbGapTemp); // Climbing speed 1 (pulling weight up) or hold when climbing down
            else
                result = result.max(HandsClimbing.Up, out_climbGap, _climbGapTemp); // Climbing speed 1 (pulling weight up)

        _climbGapTemp.SkipGaps = isClimbCrawling || isCrawlClimbing;

        if ((gap = isLadderSubstitute(sub, _climbGapTemp)) > 0 && !(isCrawling && gap > 1))
            if (!isClimbCrawling && gap > 2)
                result = result.max(HandsClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 1 (pulling upper body into gap)
            else if (isClimbCrawling && gap > 1)
                result = result.max(HandsClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 1 (crawling into upper gap)
            else // (no gap for balancing upper body)
            if (jh_offset < _handClimbingHoldGap)
                if (grabType == AroundGrab)
                    result = result.max(HandsClimbing.Up, out_climbGap, _climbGapTemp); // Lower climbing up ladder
                else
                    result = result.max(HandsClimbing.TopHold, out_climbGap, _climbGapTemp); // Lower holding
            else if (grabType == AroundGrab)
                result = result.max(HandsClimbing.TopHold, out_climbGap, _climbGapTemp); // Sinking to lower holding level
            else
                result = result.max(HandsClimbing.Sink, out_climbGap, _climbGapTemp); // Sinking to lower holding level

        if ((gap = isLadderSubstitute(subSub, _climbGapTemp)) > 0 && !isCrawling)
            if ((gap > 2 && !isCrawlClimbing) || grabType == AroundGrab || (gap > 1 && isClimbCrawling)) // (hands not long enough - down)
                if (jh_offset < _handClimbingHoldGap && !isClimbCrawling)
                    result = result.max(HandsClimbing.TopHold, out_climbGap, _climbGapTemp); // Upper holding
                else if (isClimbCrawling)
                    result = result.max(HandsClimbing.FastUp, out_climbGap, _climbGapTemp); // Sinking to upper holding level
                else
                    result = result.max(HandsClimbing.Sink, out_climbGap, _climbGapTemp); // Sinking to upper holding level

        return result;
    }

    private FeetClimbing feetClimbing(boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling, ClimbGap out_climbGap) {
        out_climbGap.reset();
        _climbGapTemp.reset();

        initializeOffset(0D, isClimbCrawling, isCrawlClimbing, isCrawling);
        FeetClimbing result = FeetClimbing.None;
        int gap;

        if ((gap = isLadderSubstitute(top, _climbGapTemp)) > 0) // No climbing (feet not long enough - up)
            result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);

        _climbGapTemp.SkipGaps = isClimbCrawling || isCrawlClimbing;

        if ((gap = isLadderSubstitute(middle, _climbGapTemp)) > 0 && !isCrawling)
            if (gap > 3 && !isClimbCrawling)
                if (!isCrawlClimbing)
                    result = result.max(FeetClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 2 (pushing upper body up into big gap)
                else
                    result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);
            else if ((isClimbCrawling || isCrawlClimbing) && gap > 1)
                if (isCrawlClimbing)
                    result = result.max(FeetClimbing.BaseWithHands, out_climbGap, _climbGapTemp);
                else
                    result = result.max(FeetClimbing.FastUp, out_climbGap, _climbGapTemp);
            else if (gap > 2)
                if (!isClimbCrawling)
                    result = result.max(FeetClimbing.SlowUpWithHoldWithoutHands, out_climbGap, _climbGapTemp); // Climbing speed 1 (no gap for balancing upper body)
                else
                    result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);
            else
                result = result.max(FeetClimbing.TopWithHands, out_climbGap, _climbGapTemp); // Climbing with hands only

        if ((gap = isLadderSubstitute(base, _climbGapTemp)) > 0)
            if (gap > 3 && !isCrawling && !isCrawlClimbing)
                result = result.max(FeetClimbing.FastUp, out_climbGap, _climbGapTemp); // Climbing speed 2 (pushing whole body up into big gap)
            else if (gap > 2 && !isCrawling)
                if (!isClimbCrawling)
                    if (jh_offset < _handClimbingHoldGap)
                        result = result.max(FeetClimbing.SlowUpWithHoldWithoutHands, out_climbGap, _climbGapTemp);
                    else
                        result = result.max(FeetClimbing.SlowUpWithSinkWithoutHands, out_climbGap, _climbGapTemp); // Climbing speed 1 (no gap for balancing whole body)
                else
                    result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp);
            else if (jh_offset < 1D - _handClimbingHoldGap)
                result = result.max(FeetClimbing.BaseWithHands, out_climbGap, _climbGapTemp); // Climbing with hands only
            else
                result = result.max(FeetClimbing.BaseHold, out_climbGap, _climbGapTemp);

        if ((isLadderSubstitute(sub, _climbGapTemp)) > 0)
            result = result.max(FeetClimbing.None, out_climbGap, _climbGapTemp); // No climbing (feet not long enough - down)

        if (isCrawlClimbing || isCrawling)
            result = result.max(FeetClimbing.BaseWithHands, out_climbGap, _climbGapTemp);

        return result;
    }

    private int isLadderSubstitute(int local_Offset, ClimbGap out_climbGap) {
        initializeLocal(local_Offset);

        int gap;
        if (local_half == 1)
            if (hasHalfHold()) {
                if (!grabRemote) {
                    boolean overLadder = isOnLadderOrVine(0) || isOnOpenTrapDoor(0);
                    boolean overOverLadder = isOnLadderOrVine(1) || isOnOpenTrapDoor(1);
                    boolean overAccessible = isBaseAccessible(1, false, true);
                    boolean overOverAccessible = isBaseAccessible(2, false, true);
                    boolean overFullAccessible = overAccessible && isFullAccessible(1, grabRemote);
                    boolean overOverFullAccessible = overAccessible && isFullExtentAccessible(2, grabRemote);

                    if (overLadder)
                        if (overOverLadder)
                            gap = 1;
                        else if (overOverAccessible)
                            gap = 1;
                        else
                            gap = 1;
                    else if (overAccessible)
                        if (overFullAccessible)
                            if (overOverFullAccessible)
                                gap = 5;
                            else
                                gap = crawl ? 3 : 5;
                        else if (overOverLadder)
                            gap = 5;
                        else
                            gap = 1;
                    else
                        gap = 1;
                } else if (isBaseAccessible(0))
                    if (isUpperHalfFrontEmpty(remote_i, 0, remote_k))
                        if (isFullAccessible(1, grabRemote))
                            if (isFullExtentAccessible(2, grabRemote))
                                gap = 5;
                            else if (isJustLowerHalfExtentAccessible(2))
                                gap = 4;
                            else
                                gap = 3;
                        else if (isLowerHalfAccessible(1, grabRemote))
                            gap = 2;
                        else
                            gap = 1;
                    else
                        gap = 1;
                else
                    gap = 0;
            } else
                gap = 0;
        else if (hasBottomHold()) {
            if (!grabRemote) {
                boolean overLadder = isOnLadderOrVine(0) || isOnOpenTrapDoor(0);
                boolean overOverLadder = isOnLadderOrVine(1) || isOnOpenTrapDoor(1);
                boolean overAccessible = isBaseAccessible(0, false, true);
                boolean overOverAccessible = isBaseAccessible(1, false, true);
                boolean overFullAccessible = overAccessible && isFullAccessible(0, grabRemote);
                boolean overOverFullAccessible = overAccessible && isFullExtentAccessible(1, grabRemote);

                if (overLadder)
                    if (overOverLadder)
                        gap = 1;
                    else if (overOverAccessible)
                        gap = 1;
                    else
                        gap = 1;
                else if (overAccessible)
                    if (overFullAccessible)
                        if (overOverAccessible)
                            if (overOverFullAccessible)
                                gap = 4;
                            else
                                gap = crawl ? 2 : 4;
                        else
                            gap = 2;
                    else if (overOverLadder)
                        gap = 2;
                    else
                        gap = 1;
                else
                    gap = 1;
            } else if (isBaseAccessible(0))
                if (isFullAccessible(0, grabRemote))
                    if (isFullExtentAccessible(1, grabRemote))
                        gap = 4;
                    else
                        gap = 2;
                else
                    gap = 1;
            else
                gap = 0;
        } else
            gap = 0;

        if (out_climbGap != null && gap > 0) {
            out_climbGap.Block = grabBlock;
            out_climbGap.Meta = grabMeta;
            out_climbGap.CanStand = gap > 3;
            out_climbGap.MustCrawl = gap > 1 && gap < 4;
            out_climbGap.Direction = this;
        }
        return gap;
    }

    private boolean hasHalfHold() {
        if (SmartMovingConfig.isFreeBaseClimb) {
            if (isOnLadder(0) && isOnLadderFront(0))
                return setHalfGrabType(AroundGrab, getBaseBlock(0), false);

            if (remoteLadderClimbing(0))
                return setHalfGrabType(AroundGrab, getRemoteBlock(0), true);
        }

        IBlockState remoteState = getRemoteBlockState(0);
        if (isEmpty(base_i, 0, base_k)) {
            if (remoteState == Block.getBlockFromName("iron_bars") && headedToFrontWall(remote_i, 0, remote_k, remoteState))
                return setHalfGrabType(HalfGrab, remoteState);
        }

        IBlockState wallId = getWallBlockId(base_i, 0, base_k);
        if (wallId == Block.getBlockFromName("iron_bars") && headedToBaseWall(0, wallId))
            return setHalfGrabType(HalfGrab, wallId, false);
        if (wallId != null && isOnMiddleLadderFront(0))
            return setHalfGrabType(AroundGrab, remoteState, false);

        if (SmartMovingConfig.freeFenceClimbing) {
            if (isFence(remoteState) && headedToFrontWall(remote_i, 0, remote_k, remoteState))
                if (!isFence(getBaseBlockState(0)))
                    return setHalfGrabType(HalfGrab, remoteState);
                else if (headedToFrontSideWall(remote_i, 0, remote_k, remoteState))
                    return setHalfGrabType(HalfGrab, remoteState);

            IBlockState remoteBelowState = getRemoteBlockState(-1);
            if (isFence(remoteBelowState) && headedToFrontWall(remote_i, -1, remote_k, remoteBelowState))
                if (!isFence(getBaseBlockState(-1)))
                    return setHalfGrabType(HalfGrab, remoteState);
                else if (headedToFrontSideWall(remote_i, -1, remote_k, remoteBelowState))
                    return setHalfGrabType(HalfGrab, remoteState);

            if (isFence(wallId) && headedToBaseWall(0, wallId))
                return setHalfGrabType(HalfGrab, wallId, false);

            IBlockState belowWallId = getWallBlockId(base_i, -1, base_k);
            if (isFence(belowWallId) && headedToBaseWall(-1, belowWallId))
                return setHalfGrabType(HalfGrab, belowWallId, false);

            if (remoteState == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteState, 0))
                return setHalfGrabType(HalfGrab, remoteState);

            if (remoteBelowState == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteBelowState, -1))
                return setHalfGrabType(HalfGrab, remoteBelowState);
        }

        if (isBottomHalfBlock(remoteState) || (isStairCompact(remoteState) && isBottomStairCompactNotBack(remoteState) && !(isStairCompact(getBaseBlockState(-1))
                && isBottomStairCompactFront(getBaseBlockState(-1)))))
            return setHalfGrabType(HalfGrab, remoteState);

        if (isTrapDoor(remoteState) && isClosedTrapDoor(remoteState))
            return setHalfGrabType(HalfGrab, remoteState);

        IBlockState baseState = getBaseBlockState(0);
        if (isTrapDoor(baseState) && !isClosedTrapDoor(baseState))
            return setHalfGrabType(HalfGrab, baseState, false);

        if (SmartMovingConfig.isFreeBaseClimb) {
            int meta = baseVineClimbing(0);
            if (meta > -1)
                return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
            meta = remoteVineClimbing(0);
            if (meta > -1)
                return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
        }

        return setHalfGrabType(NoGrab);
    }

    private boolean hasBottomHold() {
        if (SmartMovingConfig.isFreeBaseClimb) {
            if (isOnLadder(-1) && isOnLadderFront(-1))
                return setBottomGrabType(AroundGrab, getBaseBlock(-1), false);

            if (isOnLadder(0) && isOnLadderFront(0))
                return setBottomGrabType(AroundGrab, getBaseBlock(0), false);

            if (remoteLadderClimbing(-1))
                return setBottomGrabType(AroundGrab, getRemoteBlock(-1), true);

            if (remoteLadderClimbing(0))
                return setBottomGrabType(AroundGrab, getRemoteBlock(0), true);
        }

        IBlockState remoteState = getRemoteBlockState(0);
        IBlockState remoteBelowState = getRemoteBlockState(-1);
        boolean remoteLowerHalfEmpty = isLowerHalfFrontFullEmpty(remote_i, 0, remote_k);
        // if (SmartMovingOptions.hasRedPowerWire) {
        // if (isRedPowerWire(remoteBelowState)) {
        // int coverSides = getRpCoverSides(remote_i, -1, remote_k);
        // if ((isRedPowerWireFullFront(coverSides) || isRedPowerWireTop(coverSides)) && remoteLowerHalfEmpty)
        // return setBottomGrabType(HalfGrab, remoteBelowState);
        // }
        //
        // if (isRedPowerWire(remoteState)) {
        // int coverSides = getRpCoverSides(remote_i, 0, remote_k);
        // if (isRedPowerWireBottom(coverSides) && remoteLowerHalfEmpty)
        // return setBottomGrabType(HalfGrab, remoteBelowState);
        // }
        //
        // IBlockState baseState = getBaseBlockState(-1);
        // if (isRedPowerWire(baseState)) {
        // int coverSides = getRpCoverSides(base_i, -1, base_k);
        // if (isRedPowerWireFullBack(coverSides) && remoteLowerHalfEmpty)
        // return setBottomGrabType(HalfGrab, remoteBelowState);
        // }
        //
        // if (isRedPowerWire(remoteBelowState))
        // return false;
        // }

        if (isEmpty(base_i, -1, base_k)) {
            if (remoteBelowState == Block.getBlockFromName("iron_bars") && headedToFrontWall(remote_i, -1, remote_k, remoteBelowState))
                return setBottomGrabType(HalfGrab, remoteBelowState);
        }

        if (SmartMovingConfig.freeFenceClimbing) {
            IBlockState baseBelowState = getBaseBlockState(-1);
            if (isFence(remoteBelowState) && headedToFrontWall(remote_i, -1, remote_k, remoteBelowState))
                if (!isFence(baseBelowState))
                    return setBottomGrabType(HalfGrab, remoteBelowState);
                else if (headedToFrontSideWall(remote_i, -1, remote_k, remoteBelowState))
                    return setBottomGrabType(HalfGrab, remoteBelowState);

            if (remoteBelowState == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteBelowState, -1))
                return setHalfGrabType(HalfGrab, remoteBelowState);

            if (remoteState == Block.getBlockFromName("cobblestone_wall") && !headedToRemoteFlatWall(remoteState, 0))
                return setHalfGrabType(HalfGrab, remoteState);
        }

        IBlockState belowWallState = getWallBlockId(base_i, -1, base_k);
        if (belowWallState != null) {
            if (isEmpty(base_i - _i, 0, base_k - _k) && isEmpty(base_i - _i, -1, base_k - _k)) {
                if (belowWallState == Block.getBlockFromName("iron_bars") && headedToBaseWall(-1, belowWallState))
                    return setBottomGrabType(HalfGrab, belowWallState, false);
                if (isOnMiddleLadderFront(-1))
                    return setHalfGrabType(AroundGrab, remoteState, false);

                if (headedToBaseGrabWall(-1, belowWallState))
                    return setBottomGrabType(HalfGrab, belowWallState, false);
            }

            if (SmartMovingConfig.freeFenceClimbing && isFence(belowWallState) && headedToBaseWall(-1, belowWallState))
                return setBottomGrabType(HalfGrab, belowWallState, false);

            return false;
        }

        if (remoteLowerHalfEmpty && isBaseAccessible(-1, true, false))
            if (isUpperHalfFrontAnySolid(remote_i, -1, remote_k))
                if (!isBottomHalfBlock(remoteBelowState))
                    if (!isStairCompact(remoteBelowState) || !isBottomStairCompactFront(remoteBelowState))
                        if (!isDoor(remoteBelowState) || isDoorTop(remoteBelowState))
                            if (!isDoor(getBaseBlockState(0)) || !isDoorFrontBlocked(base_i, 0, base_k))
                                if (SmartMovingConfig.freeFenceClimbing || !isFence(getRemoteBlockState(-1)))
                                    return setBottomGrabType(HalfGrab, remoteBelowState);

        if (isStairCompact(remoteState)) {
            if (isTopStairCompact(remoteState) && !isTopStairCompactBack(remoteState) && isUpperHalfFrontFullSolid(remote_i, -1, remote_k))
                return setBottomGrabType(HalfGrab, remoteBelowState);
        }

        IBlockState baseBelowState = getBaseBlockState(-1);

        // for trap door bottom hold
        // if(isTrapDoor(remoteState) && isClosedTrapDoor(remoteState))
        // return setBottomGrabType(HalfGrab, remoteState);

        // for trap door top hold
        if (isTrapDoor(baseBelowState) && !isClosedTrapDoor(baseBelowState))
            return setBottomGrabType(HalfGrab, baseBelowState, false);

        if (isDoor(baseBelowState) && isDoorTop(baseBelowState) && isDoorFrontBlocked(base_i, -1, base_k) && isBaseAccessible(0))
            return setBottomGrabType(HalfGrab, baseBelowState, false);

        if (SmartMovingConfig.isFreeBaseClimb) {
            int meta = baseVineClimbing(-1);
            if (meta != DefaultMeta)
                return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);

            meta = baseVineClimbing(0);
            if (meta != DefaultMeta)
                return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);

            meta = remoteVineClimbing(-1);
            if (meta != DefaultMeta)
                return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);

            meta = remoteVineClimbing(0);
            if (meta != DefaultMeta)
                return setHalfGrabType(HalfGrab, Block.getBlockFromName("vine"), false, meta);
        }

        return setBottomGrabType(NoGrab);
    }

    private boolean setHalfGrabType(int type) {
        return setHalfGrabType(type, (Block) null);
    }

    private boolean setHalfGrabType(int type, Block block) {
        return setHalfGrabType(type, block, true);
    }

    private boolean setHalfGrabType(int type, IBlockState state) {
        return setHalfGrabType(type, state.getBlock());
    }

    private boolean setHalfGrabType(int type, Block block, boolean remote) {
        return setHalfGrabType(type, block, remote, -1);
    }

    private boolean setHalfGrabType(int type, IBlockState state, boolean remote) {
        return setHalfGrabType(type, state.getBlock(), remote);
    }

    private boolean setHalfGrabType(int type, Block block, boolean remote, int metaClimb) {
        boolean hasGrab = type != NoGrab;
        if (hasGrab && remote && _isDiagonal) {
            boolean edgeConnectCCW = rotate(90).isUpperHalfFrontEmpty(base_i, 0, remote_k);
            boolean edgeConnectCW = rotate(-90).isUpperHalfFrontEmpty(remote_i, 0, base_k);
            hasGrab &= edgeConnectCCW && edgeConnectCW;
        }
        return setGrabType(type, block, remote, hasGrab, metaClimb);
    }

    private boolean setBottomGrabType(int type) {
        return setBottomGrabType(type, (Block) null);
    }

    private boolean setBottomGrabType(int type, Block block) {
        return setBottomGrabType(type, block, true);
    }

    private boolean setBottomGrabType(int type, IBlockState state) {
        return setBottomGrabType(type, state.getBlock());
    }

    private boolean setBottomGrabType(int type, Block block, boolean remote) {
        return setBottomGrabType(type, block, remote, -1);
    }

    private boolean setBottomGrabType(int type, IBlockState state, boolean remote) {
        return setBottomGrabType(type, state.getBlock(), remote);
    }

    private boolean setBottomGrabType(int type, Block block, boolean remote, int metaClimb) {
        boolean hasGrab = type != NoGrab;
        if (hasGrab && remote && _isDiagonal) {
            boolean edgeConnectCCW = rotate(90).isLowerHalfFrontFullEmpty(base_i, 0, remote_k);
            boolean edgeConnectCW = rotate(-90).isLowerHalfFrontFullEmpty(remote_i, 0, base_k);
            hasGrab &= edgeConnectCCW && edgeConnectCW;
        }
        return setGrabType(type, block, remote, hasGrab, metaClimb);
    }

    private static boolean setGrabType(int type, Block block, boolean remote, boolean hasGrab, int metaClimb) {
        grabRemote = remote;
        grabType = hasGrab ? type : NoGrab;
        grabBlock = block;
        grabMeta = metaClimb;
        return hasGrab;
    }

    @SuppressWarnings("incomplete-switch")
    private boolean setClimbingAngles() {
        switch (_i) {
        case -1:
            switch (_k) {
            case -1:
                return setClimbingAngles(135); // NN
            case 0:
                return setClimbingAngles(90); // NZ
            case 1:
                return setClimbingAngles(45); // NP
            }
            break;
        case 0:
            switch (_k) {
            case -1:
                return setClimbingAngles(180); // ZN
            case 0:
                return setClimbingAngles(0, 360); // ZZ
            case 1:
                return setClimbingAngles(0); // ZP
            }
            break;
        case 1:
            switch (_k) {
            case -1:
                return setClimbingAngles(225); // PN
            case 0:
                return setClimbingAngles(270); // PZ
            case 1:
                return setClimbingAngles(315); // PP
            }
            break;
        }
        return false;
    }

    private boolean setClimbingAngles(float directionAngle) {
        _directionAngle = directionAngle;
        float halfAreaAngle = (float) ((_isDiagonal ? SmartMovingConfig.freeClimbingDiagonalDirectionAngle : SmartMovingConfig.freeClimbingOrthogonalDirectionAngle) / 2.0);
        return setClimbingAngles(directionAngle - halfAreaAngle, directionAngle + halfAreaAngle);
    }

    private boolean setClimbingAngles(float mimimumClimbingAngle, float maximumClimbingAngle) {
        if (mimimumClimbingAngle < 0F)
            mimimumClimbingAngle += 360F;

        if (maximumClimbingAngle > 360F)
            maximumClimbingAngle -= 360F;

        _mimimumClimbingAngle = mimimumClimbingAngle;
        _maximumClimbingAngle = maximumClimbingAngle;

        return mimimumClimbingAngle != maximumClimbingAngle;
    }

    private boolean isWithinAngle(float minimumRotation, float maximumRotation) {
        return isWithinAngle(_directionAngle, minimumRotation, maximumRotation);
    }

    private boolean isRotationForClimbing(float rotation) {
        return isWithinAngle(rotation, _mimimumClimbingAngle, _maximumClimbingAngle);
    }

    private static boolean isWithinAngle(float rotation, float minimumRotation, float maximumRotation) {
        if (minimumRotation > maximumRotation)
            return rotation >= minimumRotation || rotation <= maximumRotation;
        else
            return rotation >= minimumRotation && rotation <= maximumRotation;
    }

    private int baseVineClimbing(int j_offset) {
        boolean result = isOnVine(j_offset);
        if (result) {
            result = isOnVineFront(j_offset);
            if (result)
                return VineFrontMeta;

            if (baseVineClimbing(j_offset, PZ) || baseVineClimbing(j_offset, NZ) || baseVineClimbing(j_offset, ZP) || baseVineClimbing(j_offset, ZN))
                return VineSideMeta;
        }
        return DefaultMeta;
    }

    private boolean baseVineClimbing(int j_offset, Orientation orientation) {
        if (orientation == this)
            return false;

        return orientation.rotate(180).hasVineOrientation(world, base_i, local_offset + j_offset, base_k) && orientation.getHorizontalBorderGap() >= 0.65;
    }

    private boolean remoteLadderClimbing(int j_offset) {
        return isBehindLadder(j_offset) && isOnLadderBack(j_offset);
    }

    private int remoteVineClimbing(int j_offset) {
        if (isBehindVine(j_offset) && isOnVineBack(j_offset))
            return VineFrontMeta;

        if (remoteVineClimbing(j_offset, PZ) || remoteVineClimbing(j_offset, NZ) || remoteVineClimbing(j_offset, ZP) || remoteVineClimbing(j_offset, ZN))
            return VineSideMeta;

        return DefaultMeta;
    }

    private boolean remoteVineClimbing(int j_offset, Orientation orientation) {
        if (orientation == this)
            return false;

        int i = base_i - orientation._i;
        int k = base_k - orientation._k;
        return isVine(getBlockState(i, j_offset, k)) && orientation.hasVineOrientation(world, i, local_offset + j_offset, k) && orientation.getHorizontalBorderGap() >= 0.65F;
    }

    private static boolean isOnLadder(int j_offset) {
        IBlockState state = getBaseBlockState(j_offset);
        if (isLadder(state))
            return true;
        if (isVine(state))
            return false;
        if (isClimbable(world, base_i, local_offset + j_offset, base_k))
            return true;
        return false;
    }

    private static boolean isBehindLadder(int j_offset) {
        IBlockState state = getRemoteBlockState(j_offset);
        if (isLadder(state))
            return true;
        if (isVine(state))
            return false;
        if (isClimbable(world, remote_i, local_offset + j_offset, remote_k))
            return true;
        return false;
    }

    private static boolean isOnVine(int j_offset) {
        return isVine(getBaseBlockState(j_offset));
    }

    private static boolean isBehindVine(int j_offset) {
        return isVine(getRemoteBlockState(j_offset));
    }

    private static boolean isOnLadderOrVine(int j_offset) {
        return isLadderOrVine(getBaseBlockState(j_offset)) || grabBlock == Block.getBlockFromName("vine");
    }

    public static boolean isLadder(IBlockState state) {
        return state.getBlock() == Block.getBlockFromName("ladder");
    }

    public static boolean isVine(IBlockState state) {
        return state.getBlock() == Block.getBlockFromName("vine");
    }

    public static boolean isLadderOrVine(IBlockState state) {
        return isLadder(state) || isVine(state) || isBlockOfType(state, _ladderKitLadderTypes);
    }

    public static boolean isKnownLadder(IBlockState state) {
        return isLadder(state) || isBlockOfType(state, _ladderKitLadderTypes);
    }

    public static boolean isClimbable(World world, int x, int y, int z) {
        BlockPos position = new BlockPos(x, y, z);
        IBlockState blockState = world.getBlockState(position);
        return blockState.getBlock().isLadder(blockState, world, position, Minecraft.getMinecraft().player);
    }

    private boolean isOnLadderFront(int j_offset) {
        return hasLadderOrientation(base_i, j_offset, base_k);
    }

    private boolean isOnLadderBack(int j_offset) {
        return rotate(180).hasLadderOrientation(remote_i, j_offset, remote_k);
    }

    private boolean isOnVineFront(int j_offset) {
        return hasVineOrientation(world, base_i, local_offset + j_offset, base_k);
    }

    private boolean isOnVineBack(int j_offset) {
        return rotate(180).hasVineOrientation(world, remote_i, local_offset + j_offset, remote_k);
    }

    @SuppressWarnings("incomplete-switch")
    private boolean isOnMiddleLadderFront(int j_offset) {
        switch (getCarpentersBlockData(base_i, j_offset, base_k)) {
        case 0:
            if (this == ZN)
                return isTopHalf(base_kd);
            if (this == ZP)
                return !isTopHalf(base_kd);
            break;
        case 1:
            if (this == NZ)
                return isTopHalf(base_id);
            if (this == PZ)
                return !isTopHalf(base_id);
            break;
        }
        return false;
    }

    private static int getCarpentersBlockData(int i, int j_offset, int k) {
        if (isBlockOfType(getBlockState(i, j_offset, k), _blockCarpentersLadder)) {
            IBlockState state = getState(world, i, j_offset, k);
            TileEntity entity = state.getBlock().createTileEntity(world, state);
            if (entity != null)
                return (Integer) Reflect.Invoke(_carpentersBlockPropertiesGetData, null, entity);
        }
        return -1;
    }

    /**
     * @return Returns null if FACING property is not found in the BlockState.
     */
    public static Orientation getKnownLadderOrientation(World world, int i, int j, int k) {
        IBlockState state = getState(world, i, j, k);
        EnumFacing facing = getValue(state, BlockLadder.FACING, null);
        return facing == null ? null : FacingToOrientation.get(facing);
    }

    public boolean hasVineOrientation(World world, int i, int j, int k) {
        IBlockState state = getState(world, i, j, k);
        if (this == NZ)
            return getValue(state, BlockVine.EAST);
        if (this == PZ)
            return getValue(state, BlockVine.WEST);
        if (this == ZP)
            return getValue(state, BlockVine.NORTH);
        if (this == ZN)
            return getValue(state, BlockVine.SOUTH);
        return false;
    }

    private boolean hasLadderOrientation(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        EnumFacing value = getValue(state, BlockLadder.FACING, this._facing);
        return value == this._facing;
    }

    public boolean isRemoteSolid(World world, int i, int j, int k) {
        return isSolid(world.getBlockState(new BlockPos(i + _i, j, k + _k)).getMaterial());
    }

    public static Orientation getOpenTrapDoorOrientation(World world, int i, int j, int k) {
        IBlockState state = getState(world, i, j, k);
        if (!isClosedTrapDoor(state))
            return FacingToOrientation.get(getValue(state, BlockTrapDoor.FACING));
        return null;
    }

    private boolean isHeadedToRope() {
        int iTriple = getTriple(base_id, base_kd);
        int kTriple = getTriple(base_kd, base_id);

        if (iTriple > 0)
            if (kTriple > 0)
                return this == NN;
            else if (kTriple < 0)
                return this == NP;
            else
                return this == NZ;
        else if (iTriple < 0)
            if (kTriple > 0)
                return this == PN;
            else if (kTriple < 0)
                return this == PP;
            else
                return this == PZ;
        else if (kTriple > 0)
            return this == ZN;
        else if (kTriple < 0)
            return this == ZP;
        else
            return this == ZZ;
    }

    private boolean isOnAnchorFront(int j_offset) {
        /*
         * IBlockState state = getBaseBlockState(j_offset); switch(metaData) { case 0: return false; case 1: return false; case 2: return this._k == 1; case 3: return this._k == -1; case 4: return
         * this._i == 1; case 5: return this._i == -1; }
         */
        return false;
    }

    private static boolean isOnOpenTrapDoor(int j_offset) {
        IBlockState state = getBaseBlockState(j_offset);
        return isTrapDoor(state) && !isClosedTrapDoor(state);
    }

    private boolean isTrapDoorFront(IBlockState state) {
        return _facings.contains(getValue(state, BlockTrapDoor.FACING));
    }

    private boolean isBottomStairCompactNotBack(IBlockState state) {
        return !isTopStairCompact(state) && !isStairCompactBack(state);
    }

    private boolean isBottomStairCompactFront(IBlockState state) {
        return !isTopStairCompact(state) && isStairCompactFront(state);
    }

    private boolean isTopStairCompactFront(IBlockState state) {
        return isTopStairCompact(state) && isStairCompactFront(state);
    }

    private boolean isTopStairCompactBack(IBlockState state) {
        return isTopStairCompact(state) && isStairCompactBack(state);
    }

    private boolean isStairCompactFront(IBlockState state) {
        EnumFacing facing = getValue(state, BlockStairs.FACING, this._facing);
        BlockStairs.EnumShape shape = getValue(state, BlockStairs.SHAPE);

        if (this == NZ)
            return ((north(facing) && outer_left(shape)) || (south(facing) && outer_right(shape)) || (west(facing) && (straight(shape) || outer_right(shape) || outer_left(
                    shape))));
        if (this == PZ)
            return ((north(facing) && outer_right(shape)) || (south(facing) && outer_left(shape)) || (east(facing) && (straight(shape) || outer_right(shape) || outer_left(
                    shape))));
        if (this == ZP)
            return ((east(facing) && outer_right(shape)) || (west(facing) && outer_left(shape)) || (south(facing) && (straight(shape) || outer_right(shape) || outer_left(shape))));
        if (this == ZN)
            return ((east(facing) && outer_left(shape)) || (west(facing) && outer_right(shape)) || (north(facing) && (straight(shape) || outer_right(shape) || outer_left(shape))));
        if (this == PN)
            return ((south(facing) && outer_left(shape)) || (west(facing) && outer_right(shape)) || (east(facing) && !inner_right(shape)) || (north(facing) && !inner_left(shape)));
        if (this == PP)
            return ((north(facing) && outer_right(shape)) || (west(facing) && outer_left(shape)) || (east(facing) && !inner_left(shape)) || (south(facing) && !inner_right(shape)));
        if (this == NN)
            return ((east(facing) && outer_left(shape)) || (south(facing) && outer_right(shape)) || (north(facing) && !inner_right(shape)) || (west(facing) && !inner_left(shape)));
        if (this == NP)
            return ((east(facing) && outer_right(shape)) || (north(facing) && outer_left(shape)) || (south(facing) && !inner_left(shape)) || (west(facing) && !inner_right(shape)));
        return false;
    }

    private boolean isStairCompactBack(IBlockState state) {
        EnumFacing facing = getValue(state, BlockStairs.FACING, this._facing);
        BlockStairs.EnumShape shape = getValue(state, BlockStairs.SHAPE);

        if (this == NZ)
            return ((north(facing) && inner_right(shape)) || (south(facing) && inner_left(shape)) || (east(facing) && (straight(shape) || inner_left(shape) || inner_right(
                    shape))));
        if (this == PZ)
            return ((north(facing) && inner_left(shape)) || (south(facing) && inner_right(shape)) || (west(facing) && (straight(shape) || inner_left(shape) || inner_right(
                    shape))));
        if (this == ZP)
            return ((east(facing) && inner_left(shape)) || (west(facing) && inner_right(shape)) || (north(facing) && (straight(shape) || inner_left(shape) || inner_right(shape))));
        if (this == ZN)
            return ((east(facing) && inner_right(shape)) || (west(facing) && inner_left(shape)) || (south(facing) && (straight(shape) || inner_left(shape) || inner_right(shape))));
        if (this == PN)
            return ((east(facing) && inner_right(shape)) || (north(facing) && inner_left(shape)) || (south(facing) && !outer_left(shape)) || (west(facing) && !outer_right(shape)));
        if (this == PP)
            return ((east(facing) && inner_left(shape)) || (south(facing) && inner_right(shape)) || (north(facing) && !outer_right(shape)) || (west(facing) && !outer_left(shape)));
        if (this == NN)
            return ((north(facing) && inner_right(shape)) || (west(facing) && inner_left(shape)) || (east(facing) && !outer_left(shape)) || (south(facing) && !outer_right(shape)));
        if (this == NP)
            return ((south(facing) && inner_left(shape)) || (west(facing) && inner_right(shape)) || (east(facing) && !outer_right(shape)) || (north(facing) && !outer_left(shape)));
        return false;
    }

    private static boolean outer_left(BlockStairs.EnumShape shape) {
        return shape == BlockStairs.EnumShape.OUTER_LEFT;
    }

    private static boolean inner_left(BlockStairs.EnumShape shape) {
        return shape == BlockStairs.EnumShape.INNER_LEFT;
    }

    private static boolean straight(BlockStairs.EnumShape shape) {
        return shape == BlockStairs.EnumShape.STRAIGHT;
    }

    private static boolean inner_right(BlockStairs.EnumShape shape) {
        return shape == BlockStairs.EnumShape.INNER_RIGHT;
    }

    private static boolean outer_right(BlockStairs.EnumShape shape) {
        return shape == BlockStairs.EnumShape.OUTER_RIGHT;
    }

    private static boolean west(EnumFacing facing) {
        return facing == EnumFacing.WEST;
    }

    private static boolean south(EnumFacing facing) {
        return facing == EnumFacing.SOUTH;
    }

    private static boolean north(EnumFacing facing) {
        return facing == EnumFacing.NORTH;
    }

    private static boolean east(EnumFacing facing) {
        return facing == EnumFacing.EAST;
    }

    private static boolean isTopStairCompact(IBlockState state) {
        return getValue(state, BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
    }

    private static boolean isRedPowerWireTop(int coverSides) {
        return (coverSides >> 1) % 2 == 1;
    }

    private static boolean isRedPowerWireBottom(int coverSides) {
        return (coverSides >> 0) % 2 == 1;
    }

    private boolean isRedPowerWireFullFront(int coverSides) {
        if (this == NZ)
            return (coverSides >> 5) % 2 == 1;
        if (this == PZ)
            return (coverSides >> 4) % 2 == 1;
        if (this == ZP)
            return (coverSides >> 2) % 2 == 1;
        if (this == ZN)
            return (coverSides >> 3) % 2 == 1;
        if (this == PN)
            return PZ.isRedPowerWireFullFront(coverSides) && ZN.isRedPowerWireFullFront(coverSides);
        if (this == PP)
            return PZ.isRedPowerWireFullFront(coverSides) && ZP.isRedPowerWireFullFront(coverSides);
        if (this == NN)
            return NZ.isRedPowerWireFullFront(coverSides) && ZN.isRedPowerWireFullFront(coverSides);
        if (this == NP)
            return NZ.isRedPowerWireFullFront(coverSides) && ZP.isRedPowerWireFullFront(coverSides);
        return false;
    }

    private boolean isRedPowerWireAnyFront(int coverSides) {
        if (this == NZ)
            return (coverSides >> 5) % 2 == 1;
        if (this == PZ)
            return (coverSides >> 4) % 2 == 1;
        if (this == ZP)
            return (coverSides >> 2) % 2 == 1;
        if (this == ZN)
            return (coverSides >> 3) % 2 == 1;
        if (this == PN)
            return PZ.isRedPowerWireFullFront(coverSides) || ZN.isRedPowerWireFullFront(coverSides);
        if (this == PP)
            return PZ.isRedPowerWireFullFront(coverSides) || ZP.isRedPowerWireFullFront(coverSides);
        if (this == NN)
            return NZ.isRedPowerWireFullFront(coverSides) || ZN.isRedPowerWireFullFront(coverSides);
        if (this == NP)
            return NZ.isRedPowerWireFullFront(coverSides) || ZP.isRedPowerWireFullFront(coverSides);
        return false;
    }

    private boolean isRedPowerWireFullBack(int coverSides) {
        if (this == NZ)
            return (coverSides >> 4) % 2 == 1;
        if (this == PZ)
            return (coverSides >> 5) % 2 == 1;
        if (this == ZP)
            return (coverSides >> 3) % 2 == 1;
        if (this == ZN)
            return (coverSides >> 2) % 2 == 1;
        if (this == PN)
            return PZ.isRedPowerWireFullBack(coverSides) && ZN.isRedPowerWireFullBack(coverSides);
        if (this == PP)
            return PZ.isRedPowerWireFullBack(coverSides) && ZP.isRedPowerWireFullBack(coverSides);
        if (this == NN)
            return NZ.isRedPowerWireFullBack(coverSides) && ZN.isRedPowerWireFullBack(coverSides);
        if (this == NP)
            return NZ.isRedPowerWireFullBack(coverSides) && ZP.isRedPowerWireFullBack(coverSides);
        return false;
    }

    private boolean isRedPowerWireAnyBack(int coverSides) {
        if (this == NZ)
            return (coverSides >> 4) % 2 == 1;
        if (this == PZ)
            return (coverSides >> 5) % 2 == 1;
        if (this == ZP)
            return (coverSides >> 3) % 2 == 1;
        if (this == ZN)
            return (coverSides >> 2) % 2 == 1;
        if (this == PN)
            return PZ.isRedPowerWireFullBack(coverSides) || ZN.isRedPowerWireFullBack(coverSides);
        if (this == PP)
            return PZ.isRedPowerWireFullBack(coverSides) || ZP.isRedPowerWireFullBack(coverSides);
        if (this == NN)
            return NZ.isRedPowerWireFullBack(coverSides) || ZN.isRedPowerWireFullBack(coverSides);
        if (this == NP)
            return NZ.isRedPowerWireFullBack(coverSides) || ZP.isRedPowerWireFullBack(coverSides);
        return false;
    }

    private boolean isFenceGateFront(IBlockState state) {
        EnumFacing facing = getValue(state, BlockDirectional.FACING, this._facing);
        Orientation orientation = FacingToOrientation.get(facing);
        return this == orientation.rotate(90) || this == orientation.rotate(-90);
    }

    private boolean headedToFrontWall(int i, int j_offset, int k, IBlockState state) {
        boolean zn = getWallFlag(ZN, i, j_offset, k, state);
        boolean zp = getWallFlag(ZP, i, j_offset, k, state);
        boolean nz = getWallFlag(NZ, i, j_offset, k, state);
        boolean pz = getWallFlag(PZ, i, j_offset, k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz)
            zn = zp = nz = pz = true;

        return headedToWall(NZ, pz) ||
                headedToWall(PZ, nz) ||
                headedToWall(ZN, zp) ||
                headedToWall(ZP, zn);
    }

    private boolean headedToFrontSideWall(int i, int j_offset, int k, IBlockState state) {
        boolean zn = getWallFlag(ZN, i, j_offset, k, state);
        boolean zp = getWallFlag(ZP, i, j_offset, k, state);
        boolean nz = getWallFlag(NZ, i, j_offset, k, state);
        boolean pz = getWallFlag(PZ, i, j_offset, k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz)
            zn = zp = nz = pz = true;

        boolean iTop = isTopHalf(base_id);
        boolean kTop = isTopHalf(base_kd);
        if (iTop)
            if (kTop)
                return headedToWall(NZ, zp) ||
                        headedToWall(PZ, zp) ||
                        headedToWall(ZN, pz) ||
                        headedToWall(ZP, pz);
            else
                return headedToWall(NZ, zn) ||
                        headedToWall(PZ, zn) ||
                        headedToWall(ZN, pz) ||
                        headedToWall(ZP, pz);
        else if (kTop)
            return headedToWall(NZ, zp) ||
                    headedToWall(PZ, zp) ||
                    headedToWall(ZN, nz) ||
                    headedToWall(ZP, nz);
        else
            return headedToWall(NZ, zn) ||
                    headedToWall(PZ, zn) ||
                    headedToWall(ZN, nz) ||
                    headedToWall(ZP, nz);
    }

    private boolean headedToWall(Orientation base, boolean result) {
        if (this == base || this == base.rotate(45) || this == base.rotate(-45))
            return result;
        return false;
    }

    private boolean headedToBaseWall(int j_offset, IBlockState state) {
        boolean zn = getWallFlag(ZN, base_i, j_offset, base_k, state);
        boolean zp = getWallFlag(ZP, base_i, j_offset, base_k, state);
        boolean nz = getWallFlag(NZ, base_i, j_offset, base_k, state);
        boolean pz = getWallFlag(PZ, base_i, j_offset, base_k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz)
            zn = zp = nz = pz = true;

        boolean leaf = zn || zp || nz || pz;
        boolean coreOnly = !allOnNone && !leaf;

        boolean iTop = isTopHalf(base_id);
        boolean kTop = isTopHalf(base_kd);
        if (iTop)
            if (kTop)
                return headedToBaseWall(NN, NZ, ZN, zp, nz, pz, zn, coreOnly, leaf);
            else
                return headedToBaseWall(NP, NZ, ZP, zn, nz, pz, zp, coreOnly, leaf);
        else if (kTop)
            return headedToBaseWall(PN, PZ, ZN, zp, pz, nz, zn, coreOnly, leaf);
        else
            return headedToBaseWall(PP, PZ, ZP, zn, pz, nz, zp, coreOnly, leaf);
    }

    private boolean headedToBaseWall(Orientation diagonal, Orientation left, Orientation right, boolean leftFront, boolean rightFrontOpposite, boolean rightFront,
            boolean leftFrontOpposite, boolean co, boolean leaf) {
        if (this == diagonal)
            return leaf || co;
        if (this == left)
            return headedToBaseWall(leftFront, rightFrontOpposite, rightFront, leftFrontOpposite, co);
        if (this == right)
            return headedToBaseWall(rightFront, leftFrontOpposite, leftFront, rightFrontOpposite, co);
        return false;
    }

    private static boolean headedToBaseWall(boolean front, boolean sideOpposite, boolean side, boolean frontOpposite, boolean coreOnly) {
        return front || sideOpposite && !side || frontOpposite && !front && !side || coreOnly;
    }

    private boolean headedToBaseGrabWall(int j_offset, IBlockState state) {
        boolean zn = getWallFlag(ZN, base_i, j_offset, base_k, state);
        boolean zp = getWallFlag(ZP, base_i, j_offset, base_k, state);
        boolean nz = getWallFlag(NZ, base_i, j_offset, base_k, state);
        boolean pz = getWallFlag(PZ, base_i, j_offset, base_k, state);
        boolean allOnNone = getAllWallsOnNoWall(state);

        if (allOnNone && !zn && !zp && !nz && !pz)
            zn = zp = nz = pz = true;

        boolean azn, azp, anz, apz;

        IBlockState aboveBlock = getBlockState(base_i, j_offset + 1, base_k);
        if (isFullEmpty(aboveBlock))
            azn = azp = anz = apz = false;
        else if (isWallBlock(aboveBlock, base_i, j_offset + 1, base_k)) {
            azn = getWallFlag(ZN, base_i, j_offset + 1, base_k, aboveBlock);
            azp = getWallFlag(ZP, base_i, j_offset + 1, base_k, aboveBlock);
            anz = getWallFlag(NZ, base_i, j_offset + 1, base_k, aboveBlock);
            apz = getWallFlag(PZ, base_i, j_offset + 1, base_k, aboveBlock);
            boolean aboveAllOnNone = Orientation.getAllWallsOnNoWall(aboveBlock);

            if (aboveAllOnNone && !azn && !azp && !anz && !apz)
                azn = azp = anz = apz = true;
        } else
            azn = azp = anz = apz = true;

        boolean iTop = isTopHalf(base_id);
        boolean kTop = isTopHalf(base_kd);
        if (iTop)
            if (kTop)
                return headedToBaseGrabWall(-this._i, -this._k, zp, pz, nz, zn, azp, apz, anz, azn);
            else
                return headedToBaseGrabWall(-this._i, this._k, pz, zn, zp, nz, apz, azn, azp, anz);
        else if (kTop)
            return headedToBaseGrabWall(this._i, -this._k, nz, zp, zn, pz, anz, azp, azn, apz);
        else
            return headedToBaseGrabWall(this._i, this._k, zn, nz, pz, zp, azn, anz, apz, azp);
    }

    private static boolean headedToBaseGrabWall(int i, int k, boolean front, boolean side, boolean frontOpposite, boolean sideOpposite, boolean aboveFront, boolean aboveSide,
            boolean aboveFrontOpposite, boolean aboveSideOpposite) {
        if (sideOpposite && !aboveSideOpposite && !front && !aboveFront && i == 1)
            return true;
        if (frontOpposite && !aboveFrontOpposite && !side && !aboveSide && k == 1)
            return true;
        if (side && !aboveSide && k >= 0)
            return true;
        if (front && !aboveFront && k >= 0)
            return true;
        if (frontOpposite && !aboveFrontOpposite && !aboveFront && i == 1 && k >= 0)
            return true;
        if (sideOpposite && !aboveSideOpposite && !aboveSide && k == 1 && i >= 0)
            return true;
        return false;
    }

    private boolean headedToRemoteFlatWall(IBlockState state, int j_offset) {
        return !getWallFlag(this, remote_i, j_offset, remote_k, state) &&
                getWallFlag(this.rotate(90), remote_i, j_offset, remote_k, state) &&
                !getWallFlag(this.rotate(180), remote_i, j_offset, remote_k, state) &&
                getWallFlag(this.rotate(-90), remote_i, j_offset, remote_k, state);
    }

    private boolean getWallFlag(Orientation direction, int i, int j_offset, int k, IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof BlockPane)
            return ((BlockPane) block).canPaneConnectTo(world, new BlockPos(i + direction._i, j_offset, k + direction._k), this._facing);
        else if (isFenceBase(state)) {
            if (block instanceof BlockFence)
                return ((BlockFence) block).canConnectTo(world, new BlockPos(i + direction._i, local_offset + j_offset, k + direction._k), this._facing);
            if (block instanceof BlockWall)
                return ((BlockWall) block).canConnectTo(world, new BlockPos(i + direction._i, local_offset + j_offset, k + direction._k), this._facing);
        } else if (isFenceGate(state)) {
            return isClosedFenceGate(state) && isFenceGateFront(state);
        } else {
            switch (getCarpentersBlockData(i, j_offset, k)) {
            case -1:
                break;
            case 0:
                if (direction._k == 0 && direction._i != 0)
                    return true;
                break;
            case 1:
                if (direction._i == 0 && direction._k != 0)
                    return true;
                break;
            }
        }
        return false;
    }

    private static boolean getAllWallsOnNoWall(IBlockState state) {
        return state.getBlock() instanceof BlockPane;
    }

    private static boolean isTopHalf(double d) {
        return (int) Math.abs(Math.floor(d * 2D)) % 2 == 1;
    }

    private static int getTriple(double primary, double secondary) {
        primary = primary - Math.floor(primary) - 0.5;
        secondary = secondary - Math.floor(secondary) - 0.5;

        if (Math.abs(primary) * 2 < Math.abs(secondary))
            return 0;
        else if (primary > 0)
            return 1;
        else if (primary < 0)
            return -1;
        else
            return 0;
    }

    private static boolean isBottomHalfBlock(IBlockState state) {
        if (isHalfBlock(state) && isHalfBlockBottomMetaData(state))
            return true;
        if (state.getBlock() == Block.getBlockFromName("bed"))
            return true;
        return false;
    }

    private static boolean isTopHalfBlock(IBlockState state) {
        return isHalfBlock(state) && isHalfBlockTopMetaData(state);
    }

    private static boolean isHalfBlockBottomMetaData(IBlockState state) {
        return getValue(state, BlockSlab.HALF) == BlockSlab.EnumBlockHalf.BOTTOM;
    }

    private static boolean isHalfBlockTopMetaData(IBlockState state) {
        return getValue(state, BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
    }

    private static boolean isHalfBlock(IBlockState state) {
        return isBlock(state, BlockSlab.class, _knownHalfBlocks) && !((BlockSlab) state.getBlock()).isOpaqueCube(state);
    }

    private static boolean isStairCompact(IBlockState state) {
        return isBlock(state, BlockStairs.class, _knownCompactStairBlocks);
    }

    private static boolean isLowerHalfEmpty(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean empty = isFullEmpty(state);

        if (!empty && isHalfBlock(state) && isHalfBlockTopMetaData(state))
            empty = true;

        return empty;
    }

    private boolean isLowerHalfFrontFullEmpty(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean empty = isFullEmpty(state);

        // if (!empty && SmartMovingOptions.hasRedPowerWire) {
        // if (isRedPowerWire(state)) {
        // int coverSides = getRpCoverSides(i, j_offset, k);
        // if (!isRedPowerWireAnyFront(coverSides))
        // empty = true;
        // }
        // }

        if (!empty && isStairCompact(state) && isTopStairCompactFront(state))
            empty = true;

        if (!empty && isHalfBlock(state) && isHalfBlockTopMetaData(state))
            empty = true;

        if (!empty && isWallBlock(state, i, j_offset, k) && !headedToFrontWall(i, j_offset, k, state))
            empty = true;

        if (!empty && isDoor(state) && !rotate(180).isDoorFrontBlocked(i, j_offset, k))
            empty = true;

        if (empty && isBlockOfType(state, _ladderKitLadderTypes) && rotate(180).hasLadderOrientation(i, j_offset, k))
            empty = false;

        if (!empty && isTrapDoor(state) && (isClosedTrapDoor(state) || !rotate(180).isTrapDoorFront(state)))
            empty = true;

        return empty;
    }

    private boolean isUpperHalfFrontAnySolid(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean solid = isUpperHalfFrontFullSolid(i, j_offset, k);
        if (solid && isWallBlock(state, i, j_offset, k) && !headedToFrontWall(i, j_offset, k, state))
            solid = false;
        return solid;
    }

    private static boolean isUpperHalfFrontFullSolid(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        if (state == null)
            return false;

        boolean solid = isSolid(state.getMaterial());
        if (solid && state == Block.getBlockFromName("standing_sign"))
            solid = false;
        if (solid && state == Block.getBlockFromName("wall_sign"))
            solid = false;
        if (solid && state instanceof BlockPressurePlate)
            solid = false;
        if (solid && isTrapDoor(state))
            solid = false;
        if (solid && isOpenFenceGate(state))
            solid = false;
        return solid;
    }

    private static boolean isFullEmpty(IBlockState state) {
        if (state == null)
            return true;

        Block block = state.getBlock();
        boolean empty = !isSolid(state.getMaterial());
        if (!empty && block == Block.getBlockFromName("standing_sign"))
            empty = true;
        if (!empty && block == Block.getBlockFromName("wall_sign"))
            empty = true;
        if (!empty && block instanceof BlockPressurePlate)
            empty = true;
        return empty;
    }

    private static boolean isFenceBase(IBlockState state) {
        return isBlock(state, BlockFence.class, _knownFenceBlocks) || isBlock(state, BlockWall.class, _knownWallBlocks);
    }

    private static boolean isFence(IBlockState state) {
        return getFenceId(state) != null;
    }

    private static IBlockState getFenceId(IBlockState state) {
        if (isFenceBase(state) || isClosedFenceGate(state))
            return state;
        return null;
    }

    private static boolean isClosedFenceGate(IBlockState state) {
        return isFenceGate(state) && !getValue(state, BlockFenceGate.OPEN);
    }

    private static boolean isFenceGate(IBlockState state) {
        return isBlock(state, BlockFenceGate.class, _knownFanceGateBlocks);
    }

    private static boolean isOpenFenceGate(IBlockState state) {
        return isFenceGate(state) && !getValue(state, BlockFenceGate.OPEN);
    }

    private static boolean isOpenTrapDoor(int i, int j_offset, int k) {
        return isTrapDoor(i, j_offset, k) && !isClosedTrapDoor(getBlockState(i, j_offset, k));
    }

    private static boolean isClosedTrapDoor(int i, int j_offset, int k) {
        return isTrapDoor(i, j_offset, k) && isClosedTrapDoor(getBlockState(i, j_offset, k));
    }

    private static boolean isTrapDoor(int i, int j_offset, int k) {
        return isTrapDoor(getBlockState(i, j_offset, k));
    }

    public static boolean isTrapDoor(IBlockState block) {
        return isBlock(block, BlockTrapDoor.class, _knownTrapDoorBlocks);
    }

    private static boolean isBlock(IBlockState state, Class<?> type, Block[] baseBlocks) {
        if (state == null)
            return false;

        Block block = state.getBlock();
        if (type != null && baseBlocks.length > 1 && isBlockOfType(state, type))
            return true;

        for (int i = 0; i < baseBlocks.length; i++)
            if (baseBlocks[i] != null && block == baseBlocks[i])
                return true;

        if (type != null && isBlockOfType(state, type))
            return true;

        Class<?> blockType = block.getClass();
        for (int i = 0; i < baseBlocks.length; i++)
            if (baseBlocks[i] != null && baseBlocks[i].getClass().isAssignableFrom(blockType))
                return true;

        return false;
    }

    public static boolean isClosedTrapDoor(IBlockState state) {
        return !getValue(state, BlockTrapDoor.OPEN);
    }

    private static boolean isDoor(IBlockState state) {
        return isBlockOfType(state, BlockDoor.class);
    }

    private static boolean isDoorTop(IBlockState state) {
        return getValue(state, BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER;
    }

    @SuppressWarnings("incomplete-switch")
    private boolean isDoorFrontBlocked(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        if (isDoorTop(state))
            return isDoorFrontBlocked(i, j_offset - 1, k);

        switch (getDoorFacing(state)) {
        case SOUTH:
            return this._k < 0;
        case WEST:
            return this._i > 0;
        case NORTH:
            return this._k > 0;
        case EAST:
            return this._i < 0;
        }

        return true;
    }

    private EnumFacing getDoorFacing(IBlockState state) {
        EnumFacing facing = getValue(state, BlockDoor.FACING, this._facing);
        if (!getValue(state, BlockDoor.OPEN))
            return facing;

        switch (getValue(state, BlockDoor.HINGE)) {
        case LEFT:
            switch (facing) {
            case EAST:
                return EnumFacing.NORTH;
            case NORTH:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.EAST;
            default:
                return facing;
            }
        case RIGHT:
            switch (facing) {
            case EAST:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.NORTH;
            case NORTH:
                return EnumFacing.EAST;
            default:
                return facing;
            }
        default:
            return facing;
        }
    }

    private static IBlockState getWallBlockId(int i, int j_offset, int k) {
        IBlockState block = getBlockState(i, j_offset, k);
        if (isWallBlock(block, i, j_offset, k))
            return block;
        return null;
    }

    @SuppressWarnings("unused")
    private static boolean isWallBlock(IBlockState state, int i, int j_offset, int k) {
        return isBlock(state, BlockPane.class, _knownThinWallBlocks) || isFence(state) || isBlockOfType(state, _blockCarpentersLadder);
    }

    private static boolean isBaseAccessible(int j_offset) {
        return isBaseAccessible(j_offset, false, false);
    }

    private static boolean isBaseAccessible(int j_offset, boolean bottom, boolean full) {
        IBlockState state = getBaseBlockState(j_offset);
        boolean accessible = isEmpty(base_i, j_offset, base_k);
        // if (SmartMovingOptions.hasRedPowerWire && !accessible) {
        // if (isRedPowerWire(state)) {
        // int coverSides = getRpCoverSides(base_i, j_offset, base_k);
        // accessible = !isRedPowerWireBottom(coverSides);
        //
        // IBlockState lowerId = getBaseBlockState(j_offset - 1);
        // if (isRedPowerWire(lowerId)) {
        // int lowerCoverSides = getRpCoverSides(base_i, j_offset - 1, base_k);
        // accessible &= !isRedPowerWireTop(lowerCoverSides);
        // }
        // }
        // }

        if (!accessible && isFullEmpty(state))
            accessible = true;

        if (!accessible && isOpenTrapDoor(base_i, j_offset, base_k))
            accessible = true;

        if (!accessible && bottom && isClosedTrapDoor(base_i, j_offset, base_k))
            accessible = true;

        if (!accessible && !full && isWallBlock(state, base_i, j_offset, base_k))
            accessible = true;

        if (!accessible && isDoor(state))
            accessible = true;

        return accessible;
    }

    private boolean isRemoteAccessible(int j_offset) {
        boolean accessible = isEmpty(remote_i, j_offset, remote_k);
        // if (SmartMovingOptions.hasRedPowerWire && !accessible) {
        // IBlockState remoteState = getRemoteBlockState(j_offset);
        // if (isRedPowerWire(remoteState)) {
        // int coverSides = getRpCoverSides(remote_i, j_offset, remote_k);
        // accessible = !isRedPowerWireAnyFront(coverSides);
        //
        // IBlockState baseState = getBaseBlockState(j_offset);
        // if (isRedPowerWire(baseState)) {
        // int baseCoverSides = getRpCoverSides(base_i, j_offset, base_k);
        // accessible &= !isRedPowerWireAnyBack(baseCoverSides);
        // }
        // }
        // }

        if (accessible) {
            IBlockState baseState = getBaseBlockState(j_offset);
            if (isTrapDoor(baseState))
                accessible = !isTrapDoorFront(baseState);

            if (accessible && isDoor(baseState))
                accessible = !isDoorFrontBlocked(base_i, j_offset, base_k);

            if (remoteLadderClimbing(j_offset))
                accessible = false;
        }

        if (!accessible && isTrapDoor(remote_i, j_offset, remote_k))
            accessible = isClosedTrapDoor(getRemoteBlockState(j_offset));

        if (!accessible) {
            IBlockState remoteState = getRemoteBlockState(j_offset);
            if (isWallBlock(remoteState, remote_i, j_offset, remote_k) && !headedToFrontWall(remote_i, j_offset, remote_k, remoteState) && !isFence(getRemoteBlockState(j_offset
                    - 1)))
                accessible = true;

            IBlockState remoteBelowState = getRemoteBlockState(j_offset - 1);
            if (!accessible && isFence(remoteBelowState) && (!headedToFrontWall(remote_i, j_offset - 1, remote_k, remoteBelowState) || isWallBlock(getBaseBlockState(j_offset - 1),
                    base_i, j_offset - 1, base_k)))
                if (remoteBelowState != Block.getBlockFromName("cobblestone_wall") || headedToRemoteFlatWall(remoteBelowState, -1))
                    accessible = true;

            if (!accessible && isDoor(remoteState) && !rotate(180).isDoorFrontBlocked(remote_i, j_offset, remote_k))
                accessible = true;
        }

        return accessible;
    }

    private boolean isAccessAccessible(int j_offset) {
        if (!_isDiagonal)
            return true;

        return isEmpty(remote_i, j_offset, base_k) && isEmpty(base_i, j_offset, remote_k);
    }

    private boolean isFullExtentAccessible(int j_offset, boolean grabRemote) {
        boolean accessible = isFullAccessible(j_offset, grabRemote);
        // if (SmartMovingOptions.hasRedPowerWire && accessible) {
        // IBlockState topState = getRemoteBlockState(j_offset);
        // if (isRedPowerWire(topState)) {
        // int coverSides = getRpCoverSides(remote_i, j_offset, remote_k);
        // if (isRedPowerWireBottom(coverSides))
        // accessible = false;
        //
        // }
        //
        // IBlockState bottomState = getRemoteBlockState(j_offset - 1);
        // if (isRedPowerWire(bottomState)) {
        // int coverSides = getRpCoverSides(remote_i, j_offset - 1, remote_k);
        // if (isRedPowerWireTop(coverSides))
        // accessible = false;
        // }
        // }
        return accessible;
    }

    private boolean isJustLowerHalfExtentAccessible(int j_offset) {
        IBlockState remoteState = getRemoteBlockState(j_offset);

        boolean accessible = false;
        if (!accessible)
            accessible = isTopHalfBlock(remoteState);
        if (!accessible)
            accessible = isStairCompact(remoteState) && isTopStairCompactFront(remoteState);
        return accessible;
    }

    private boolean isFullAccessible(int j_offset, boolean grabRemote) {
        if (grabRemote)
            return isBaseAccessible(j_offset) && isRemoteAccessible(j_offset) && isAccessAccessible(j_offset);
        else
            return isEmpty(base_i, j_offset, base_k);
    }

    private boolean isLowerHalfAccessible(int j_offset, boolean grabRemote) {
        if (grabRemote)
            return isBaseAccessible(1, true, false) && rotate(180).isLowerHalfFrontFullEmpty(base_i, 1, base_k) && isLowerHalfFrontFullEmpty(remote_i, 1, remote_k);
        else
            return isLowerHalfEmpty(base_i, j_offset, base_k);
    }

    private static boolean isEmpty(int i, int j_offset, int k) {
        return isFullEmpty(getBlockState(i, j_offset, k)) && !isFence(getBlockState(i, j_offset - 1, k));
    }

    private boolean isUpperHalfFrontEmpty(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        boolean empty = isFullEmpty(state);

        if (!empty) {
            if (isBottomHalfBlock(state))
                empty = true;

            if (!empty && isStairCompact(state) && isBottomStairCompactFront(state))
                empty = true;
        }

        // if (SmartMovingOptions.hasRedPowerWire && !empty) {
        // if (isRedPowerWire(state)) {
        // int coverSides = getRpCoverSides(i, j_offset, k);
        // if (!isRedPowerWireAnyFront(coverSides))
        // empty = true;
        // }
        // }
        if (!empty && isTrapDoor(state))
            empty = true;

        if (!empty) {
            IBlockState wallId = getWallBlockId(i, j_offset, k);
            if (wallId != null && (!headedToFrontWall(i, j_offset, k, wallId) || isWallBlock(getBlockState(i - _i, j_offset, k - _k), i - _i, j_offset, k - _k)))
                empty = true;
        }

        if (empty && isBlockOfType(state, _ladderKitLadderTypes) && rotate(180).hasLadderOrientation(i, j_offset, k))
            empty = false;

        return empty;
    }

    private static int getRpCoverSides(int i, int j_offset, int k) {
        IBlockState state = getBlockState(i, j_offset, k);
        TileEntity tileEntity = state.getBlock().createTileEntity(world, state);
        Class<?> tileEntityClass = tileEntity.getClass();
        while (!tileEntityClass.getSimpleName().equals("TileCovered"))
            tileEntityClass = tileEntityClass.getSuperclass();
        return (Integer) Reflect.GetField(tileEntityClass, tileEntity, new Name("CoverSides"));
    }

    private static boolean isRedPowerWire(IBlockState state) {
        return hasBlockName(state.getBlock(), "tile.rpwire");
    }

    public static int getFiniteLiquidWater(Block block) {
        String blockName = getBlockName(block);
        if (blockName == null)
            return 0;
        if (blockName.equals("tile.nocean"))
            return 2;
        if (blockName.equals("tile.nwater_still"))
            return 1;
        return 0;
    }

    private static boolean isSolid(Material material) {
        return material.isSolid() && material.blocksMovement();
    }

    private static IBlockState getBlockState(int i, int j_offset, int k) {
        return getState(world, i, local_offset + j_offset, k);
    }

    private static Block getBaseBlock(int j_offset) {
        return getBaseBlockState(j_offset).getBlock();
    }

    private static IBlockState getBaseBlockState(int j_offset) {
        return getState(world, base_i, local_offset + j_offset, base_k);
    }

    private static boolean isBlockOfType(IBlockState state, Class<?>... types) {
        if (types == null || state == null)
            return false;

        Class<?> blockType = state.getBlock().getClass();
        for (Class<?> type : types)
            if (type != null && type.isAssignableFrom(blockType))
                return true;

        return false;
    }

    private static Block getRemoteBlock(int j_offset) {
        return getRemoteBlockState(j_offset).getBlock();
    }

    private static IBlockState getRemoteBlockState(int j_offset) {
        return getState(world, remote_i, local_offset + j_offset, remote_k);
    }

    private static boolean hasBlockName(Block block, String name) {
        String blockName = getBlockName(block);
        return blockName != null && blockName.equals(name);
    }

    private static String getBlockName(Block block) {
        if (block == null)
            return null;
        return block.getUnlocalizedName();
    }

    private void initialize(World w, int i, double id, double jhd, int k, double kd) {
        world = w;

        base_i = i;
        base_id = id;
        base_jhd = jhd;
        base_k = k;
        base_kd = kd;

        remote_i = i + _i;
        remote_k = k + _k;
    }

    private static void initializeOffset(double offset_halfs, boolean isClimbCrawling, boolean isCrawlClimbing, boolean isCrawling) {
        crawl = isClimbCrawling || isCrawlClimbing || isCrawling;

        double offset_jhd = base_jhd + offset_halfs;
        int offset_jh = MathHelper.floor(offset_jhd);
        jh_offset = offset_jhd - offset_jh;

        all_j = offset_jh / 2;
        all_offset = offset_jh % 2;
    }

    private static void initializeLocal(int localOffset) {
        local_halfOffset = localOffset + all_offset;
        local_half = Math.abs(local_halfOffset) % 2;
        local_offset = all_j + (local_halfOffset - local_half) / 2;
    }

    private final static float _handClimbingHoldGap = Math.min(0.25F, 0.06F * (float) Math.max(SmartMovingConfig.freeClimbingUpSpeedFactor,
            SmartMovingConfig.freeClimbingDownSpeedFactor));

    private static ClimbGap _climbGapTemp = new ClimbGap();
    private static ClimbGap _climbGapOuterTemp = new ClimbGap();

    private static World world;
    private static double base_jhd, jh_offset;
    private static int all_j, all_offset;
    private static int base_i, base_k;
    private static double base_id, base_kd;
    private static int remote_i, remote_k;
    private static boolean crawl;

    private static int local_halfOffset;
    private static int local_half;
    private static int local_offset;

    private static boolean grabRemote;
    private static int grabType;
    private static Block grabBlock;
    private static int grabMeta;

    @Override
    public String toString() {
        if (this == ZZ)
            return "ZZ";
        if (this == NZ)
            return "NZ";
        if (this == PZ)
            return "PZ";
        if (this == ZP)
            return "ZP";
        if (this == ZN)
            return "ZN";
        if (this == PN)
            return "PN";
        if (this == PP)
            return "PP";
        if (this == NN)
            return "NN";
        if (this == NP)
            return "NP";
        return "UNKNOWN(" + _i + "," + _k + ")";
    }

    private static final Block[] _knownFanceGateBlocks;
    private static final Block[] _knownFenceBlocks;
    private static final Block[] _knownWallBlocks;
    private static final Block[] _knownHalfBlocks;
    private static final Block[] _knownCompactStairBlocks;
    private static final Block[] _knownTrapDoorBlocks;
    private static final Block[] _knownThinWallBlocks;

    private static final Class<?>[] _ladderKitLadderTypes = null;
    private static final Class<?> _blockCarpentersLadder;
    private static final Method _carpentersBlockPropertiesGetData;

    static {

        _blockCarpentersLadder = Reflect.LoadClass(Block.class, new Name("carpentersblocks.block.BlockCarpentersLadder"), false);
        if (_blockCarpentersLadder != null) {
            Class<?> carpentersBlockProperties = Reflect.LoadClass(Block.class, new Name("carpentersblocks.util.BlockProperties"), false);
            Class<?> carpentersTEBaseBlock = Reflect.LoadClass(Block.class, new Name("carpentersblocks.tileentity.TEBase"), false);
            _carpentersBlockPropertiesGetData = Reflect.GetMethod(carpentersBlockProperties, new Name("getMetadata"), carpentersTEBaseBlock);
        } else
            _carpentersBlockPropertiesGetData = null;

        _knownFanceGateBlocks = new Block[] { Block.getBlockFromName("fence_gate") };
        _knownFenceBlocks = new Block[] { Block.getBlockFromName("fence"), Block.getBlockFromName("nether_brick_fence") };
        _knownWallBlocks = new Block[] { Block.getBlockFromName("cobblestone_wall") };
        _knownHalfBlocks = new Block[] { Block.getBlockFromName("stone_slab"), Block.getBlockFromName("double_stone_slab"), Block.getBlockFromName("wooden_slab"), Block
                .getBlockFromName("double_wooden_slab") };
        _knownCompactStairBlocks = new Block[] { Block.getBlockFromName("stone_stairs"), Block.getBlockFromName("oak_stairs"), Block.getBlockFromName("dark_oak_stairs"), Block
                .getBlockFromName("brick_stairs"), Block.getBlockFromName("nether_brick_stairs"), Block.getBlockFromName("sandstone_stairs"), Block.getBlockFromName(
                        "stone_brick_stairs"), Block.getBlockFromName("birch_stairs"), Block.getBlockFromName("jungle_stairs"), Block.getBlockFromName("spruce_stairs"), Block
                                .getBlockFromName("quartz_stairs"), Block.getBlockFromName("acacia_stairs") };
        _knownTrapDoorBlocks = new Block[] { Block.getBlockFromName("trapdoor") };
        _knownThinWallBlocks = new Block[] { Block.getBlockFromName("iron_bars"), Block.getBlockFromName("glass_pane") };
    }

    public static final float ClimbPullMotion = 0.3F;

    public static final double FastUpMotion = 0.2D;
    public static final double MediumUpMotion = 0.14D;
    public static final double SlowUpMotion = 0.1D;
    public static final double HoldMotion = 0.08D;
    public static final double SinkDownMotion = 0.05D;
    public static final double ClimbDownMotion = 0.01D;
    public static final double CatchCrawlGapMotion = 0.17D;

    public static final float SwimCrawlWaterMaxBorder = 1F;
    public static final float SwimCrawlWaterTopBorder = 0.65F;
    public static final float SwimCrawlWaterMediumBorder = 0.6F;
    public static final float SwimCrawlWaterBottomBorder = 0.55F;

    public static final float HorizontalGroundDamping = 0.546F;
    public static final float HorizontalAirDamping = 0.91F;
    public static final float HorizontalAirodynamicDamping = 0.999F;

    public static final float SwimSoundDistance = 1F / 0.7F;
    public static final float SlideToHeadJumpingFallDistance = 0.05F;

    // public static final SmartMovingClient Client = new SmartMovingClient();
    // public static final SmartMovingOptions Options = new SmartMovingOptions();
    // public static final SmartMovingServerConfig ServerConfig = new SmartMovingServerConfig();
    // public static SmartMovingClientConfig Config = Options;

    public static Block getBlock(World world, int x, int y, int z) {
        return getState(world, x, y, z).getBlock();
    }

    public static IBlockState getState(World world, BlockPos blockPos) {
        return world.getBlockState(blockPos);
    }

    public static IBlockState getState(World world, int x, int y, int z) {
        return world.getBlockState(new BlockPos(x, y, z));
    }

    public static Material getMaterial(World world, int x, int y, int z) {
        return getState(world, x, y, z).getMaterial();
    }

    public static boolean getValue(IBlockState state, PropertyBool property) {
        return state.getValue(property);
    }

    public static int getValue(IBlockState state, PropertyInteger property) {
        return state.getValue(property);
    }

    public static EnumFacing getValue(IBlockState state, PropertyDirection property, EnumFacing defaultValue) {
        Comparable<?> comparable = state.getProperties().get(property);
        return comparable == null ? null : property.getValueClass().cast(comparable);
    }

    public static <T extends Enum<T> & IStringSerializable> T getValue(IBlockState state, PropertyEnum<T> property) {
        return state.getValue(property);
    }

    private static MinecraftServer lastMinecraftServer = null;
}