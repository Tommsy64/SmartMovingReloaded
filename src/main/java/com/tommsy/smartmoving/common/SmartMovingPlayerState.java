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

package com.tommsy.smartmoving.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import io.netty.buffer.ByteBuf;

@ToString
@EqualsAndHashCode
public class SmartMovingPlayerState {
    public boolean isRunning;
    public boolean isSprinting;

    public boolean isCrawling;
    public boolean isCrouching;

    public boolean isSneaking() {
        return isCrouching || isCrawling;
    }

    public void copy(SmartMovingPlayerState other) {
        this.isRunning = other.isRunning;
        this.isSprinting = other.isSprinting;

        this.isCrawling = other.isCrawling;
        this.isCrouching = other.isCrouching;
    }

    public void writeToBuffer(ByteBuf buf) {
        buf.writeBoolean(isRunning);
        buf.writeBoolean(isSprinting);

        buf.writeBoolean(isCrawling);
        buf.writeBoolean(isCrouching);
    }

    public void readFromBuffer(ByteBuf buf) {
        this.isRunning = buf.readBoolean();
        this.isSprinting = buf.readBoolean();

        this.isCrawling = buf.readBoolean();
        this.isCrouching = buf.readBoolean();
    }
}
