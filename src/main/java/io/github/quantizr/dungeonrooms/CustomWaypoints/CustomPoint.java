package io.github.quantizr.dungeonrooms.CustomWaypoints;

import lombok.Getter;
import net.minecraft.util.BlockPos;

public class CustomPoint {
    @Getter
    private final BlockPos position;
    @Getter
    private final String name;
    @Getter
    private final int pointer;
    public CustomPoint(BlockPos p, String s, int pointer){
        this.position = p;
        this.name = s;
        this.pointer = pointer;
    }

}
