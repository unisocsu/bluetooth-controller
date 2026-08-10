package com.unisocsu.bluetoothcontroller;

public class AudioRouter {

    /**
     * Tries to route media audio to Bluetooth and other audios to speaker/earpiece using tinymix commands.
     * tinymix is standard in UNISOC and Qualcomm devices.
     */
    public static String routeViaTinyMix(boolean enableSplit) {
        StringBuilder sb = new StringBuilder();
        sb.append("Running tinymix routing:\n");
        if (enableSplit) {
            sb.append(ShellUtils.runCommand("tinymix \"I2S0 Loopback Switch\" 1"));
            sb.append(ShellUtils.runCommand("tinymix \"Audio Route\" \"Bluetooth\""));
        } else {
            sb.append(ShellUtils.runCommand("tinymix \"I2S0 Loopback Switch\" 0"));
            sb.append(ShellUtils.runCommand("tinymix \"Audio Route\" \"Default\""));
        }
        return sb.toString();
    }

    /**
     * Executes android system service calls to modify audio policy and routing.
     */
    public static String routeViaServiceCall(int routeType) {
        return ShellUtils.runCommand("service call audio 7 i32 " + routeType);
    }

    /**
     * Forces system properties that control audio routing and reloads the audio system.
     */
    public static String forceAudioSystemReload() {
        StringBuilder sb = new StringBuilder();
        sb.append(ShellUtils.runCommand("setprop persist.audio.routing.split 1"));
        sb.append(ShellUtils.runCommand("killall audioserver"));
        return sb.toString();
    }
}
