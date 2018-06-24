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

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import com.tommsy.smartmoving.SmartMovingMod;

public class SmartMovingInput {

    public final Button forward = new Button();
    public final Button left = new Button();
    public final Button right = new Button();
    public final Button back = new Button();
    public final Button jump = new Button();
    public final Button sprint = new Button();
    public final Button sneak = new Button();
    public final Button grab = new Button();

    private GameSettings gameSettings;

    public SmartMovingInput(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public static class Button {
        public boolean pressed, wasPressed;
        public boolean startPressed, stopPressed;

        public void update(KeyBinding binding) {
            update(Minecraft.getMinecraft().inGameHasFocus && binding.isKeyDown());
        }

        public void update(boolean pressed) {
            this.wasPressed = this.pressed;
            this.pressed = pressed;

            this.startPressed = !this.wasPressed && pressed;
            this.stopPressed = this.wasPressed && !pressed;
        }

        // private static boolean isKeyDown(KeyBinding keyBinding) {
        // return isKeyDown(keyBinding, keyBinding.isPressed());
        // }

        // private static boolean isKeyDown(KeyBinding keyBinding, boolean wasDown) {
        // GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        // if (currentScreen == null || currentScreen.allowUserInput)
        // return isKeyDown(keyBinding.getKeyCode());
        // return wasDown;
        // }

        // private static boolean isKeyDown(int keyCode) {
        // if (keyCode >= 0)
        // return Keyboard.isKeyDown(keyCode);
        // return Mouse.isButtonDown(keyCode + 100);
        // }
    }

    public void update() {
        forward.update(gameSettings.keyBindForward);
        left.update(gameSettings.keyBindLeft);
        right.update(gameSettings.keyBindRight);
        back.update(gameSettings.keyBindBack);
        jump.update(gameSettings.keyBindJump);
        sprint.update(gameSettings.keyBindSprint);
        sneak.update(gameSettings.keyBindSneak);
        grab.update(SmartMovingMod.clientProxy.keyBindGrab);
    }
}
