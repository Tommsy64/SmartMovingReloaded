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

package com.tommsy.smartmoving.client;

import com.tommsy.smartmoving.SmartMovingInfo;
import com.tommsy.smartmoving.client.model.SmartMovingModelBiped;
import com.tommsy.smartmoving.common.util.WeakLinkedList;
import com.tommsy.smartmoving.network.SmartMovingPacketReceiver;
import com.tommsy.smartmoving.network.SmartMovingPacketSender;
import com.tommsy.smartmoving.server.SmartMovingServerPlayer;

import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmartMovingClient implements SmartMovingPacketReceiver, SmartMovingPacketSender {
    public static final WeakLinkedList<SmartMovingModelBiped> modelBipedInstances = new WeakLinkedList<>();

    public static final SmartMovingClient INSTANCE = new SmartMovingClient();

    @Override
    public void sendPacket(byte[] data) {
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection != null)
            connection.getNetworkManager().sendPacket(
                    new CPacketCustomPayload(SmartMovingInfo.NETWORK_ID, new PacketBuffer(Unpooled.wrappedBuffer(data))));
    }

    @Override
    public boolean processStatePacket(FMLProxyPacket packet, SmartMovingServerPlayer player, int entityId, long state) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
        if (entity == null)
            return true;

        SmartMovingOtherPlayer smPlayer = (SmartMovingOtherPlayer) ((EntityOtherPlayerMP) entity);
        smPlayer.getPlayerHandler().processStatePacket(state);
        return true;
    }

    @Override
    public boolean processConfigInfoPacket(FMLProxyPacket packet, SmartMovingServerPlayer player, String info) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean processConfigContentPacket(FMLProxyPacket packet, SmartMovingServerPlayer player, String[] content, String username) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean processConfigChangePacket(FMLProxyPacket packet, SmartMovingServerPlayer player) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean processSpeedChangePacket(FMLProxyPacket packet, SmartMovingServerPlayer player, int difference, String username) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean processHungerChangePacket(FMLProxyPacket packet, SmartMovingServerPlayer player, float hunger) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean processSoundPacket(FMLProxyPacket packet, SmartMovingServerPlayer player, String soundId, float distance, float pitch) {
        // TODO Auto-generated method stub
        return false;
    }
}
