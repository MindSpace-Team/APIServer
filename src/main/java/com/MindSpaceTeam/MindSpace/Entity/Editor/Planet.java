package com.MindSpaceTeam.MindSpace.Entity.Editor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

@Getter
@Setter
@TypeAlias("planet")
public class Planet extends EditorElements{
    private long parentId;
    private long initialAngle;

    public Planet(List<Elements> elements) {
        super(elements);
    }
}
