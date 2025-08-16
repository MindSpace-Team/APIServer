package com.MindSpaceTeam.MindSpace.Entity.Editor;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class Elements {
    private long id;
    private String type;
    private int radius;
    private String color;
    private List<Long> children;
}
