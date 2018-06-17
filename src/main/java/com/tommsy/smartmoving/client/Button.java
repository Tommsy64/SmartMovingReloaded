package com.tommsy.smartmoving.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

public class Button {
    public boolean Pressed;
    public boolean WasPressed;

    public boolean StartPressed;
    public boolean StopPressed;

    public void update(KeyBinding binding) {
        update(Minecraft.getMinecraft().inGameHasFocus && isKeyDown(binding));
    }

    public void update(int keyCode) {
        update(Minecraft.getMinecraft().inGameHasFocus && isKeyDown(keyCode));
    }

    public void update(boolean pressed) {
        WasPressed = Pressed;
        Pressed = pressed;

        StartPressed = !WasPressed && Pressed;
        StopPressed = WasPressed && !Pressed;
    }

    private static boolean isKeyDown(KeyBinding keyBinding) {
        return isKeyDown(keyBinding, keyBinding.isPressed());
    }

    private static boolean isKeyDown(KeyBinding keyBinding, boolean wasDown) {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen == null || currentScreen.allowUserInput)
            return isKeyDown(keyBinding.getKeyCode());
        return wasDown;
    }

    private static boolean isKeyDown(int keyCode) {
        if (keyCode >= 0)
            return Keyboard.isKeyDown(keyCode);
        return Mouse.isButtonDown(keyCode + 100);
    }
}