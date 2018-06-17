package com.tommsy.smartmoving.server;

import com.tommsy.smartmoving.common.SmartMovingEntityLivingBase;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public interface SmartMovingServerPlayer extends SmartMovingEntityLivingBase {
    public SmartMovingServerPlayerHandler getPlayerHandler();

    public MinecraftServer getMinecraftServer();
    
    public default EntityPlayerMP asEntityPlayerMP() {
        return (EntityPlayerMP) ((Object) this);
    }
}
