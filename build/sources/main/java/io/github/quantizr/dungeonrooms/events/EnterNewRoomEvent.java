package io.github.quantizr.dungeonrooms.events;

import io.github.quantizr.dungeonrooms.DungeonRooms;
import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.io.File;

public class EnterNewRoomEvent extends Event {
    @Getter
    private final String roomName;
    public EnterNewRoomEvent(String roomName){
        this.roomName = roomName;
    }
}
