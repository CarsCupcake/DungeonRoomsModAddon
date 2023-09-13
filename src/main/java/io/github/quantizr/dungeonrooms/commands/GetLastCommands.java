package io.github.quantizr.dungeonrooms.commands;

import io.github.quantizr.dungeonrooms.events.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class GetLastCommands extends CommandBase {
    private static final List<String> commands = new ArrayList<>();
    @Override
    public String getCommandName() {
        return "lastcommand";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/<command>";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) throws CommandException {
        for (String command : commands) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(command));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @SubscribeEvent
    public void onCommand(PacketEvent.SendEvent event){
        if(!(event.packet instanceof C01PacketChatMessage)) return;
        String cmd = ((C01PacketChatMessage) event.packet).getMessage();
        if(cmd.charAt(0) != '/') return;
        if(cmd.contains("lastcommand")) return;
        commands.add(0, cmd);
        if(commands.size() > 5) commands.remove(5);
    }
}
