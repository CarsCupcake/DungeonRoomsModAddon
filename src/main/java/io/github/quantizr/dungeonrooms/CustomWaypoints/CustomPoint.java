package io.github.quantizr.dungeonrooms.CustomWaypoints;

import lombok.Getter;
import net.minecraft.util.BlockPos;

public class CustomPoint {
    @Getter
    private final BlockPos position;
    @Getter
    private final String name;
    public CustomPoint(BlockPos p, String s){
        this.position = p;
        this.name = s;
    }

}
