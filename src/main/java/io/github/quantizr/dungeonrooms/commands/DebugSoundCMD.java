package io.github.quantizr.dungeonrooms.commands;

import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.events.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

public class DebugSoundCMD extends CommandBase {
    private static boolean toggle = false;
    private static String whitelisted;
    private static final Set<String> blacklist = new HashSet<>();

    @Override
    public String getCommandName() {
        return "debugsound";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/<command>";
    }


    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        if (strings.length == 0) toggle = !toggle;
        if (strings.length == 1) {

            if(strings[0].equals("record")){
                return;
            }

            if (whitelisted == null || !whitelisted.equals(strings[0])) whitelisted = strings[0];
            else whitelisted = null;
        }
        if (strings.length == 2) {
            if (strings[0].equals("remove")) {
                blacklist.clear();
                whitelisted = null;
                return;
            }
            if (!blacklist.contains(strings[1])) blacklist.add(strings[1]);
            else blacklist.add(strings[1]);
        }
    }

    @SubscribeEvent
    public void getSound(PacketEvent.ReceiveEvent event) {
        if (!toggle) return;
        if (!(event.packet instanceof S29PacketSoundEffect)) return;
        S29PacketSoundEffect packet = (S29PacketSoundEffect) event.packet;
        if (whitelisted != null && !packet.getSoundName().startsWith(whitelisted)) return;
        if (blacklist.contains(packet.getSoundName())) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Sound: " + packet.getSoundName() + " with pitch " + String.format("%.2f", packet.getPitch()) + " at volume " + packet.getVolume()));
    }
}
