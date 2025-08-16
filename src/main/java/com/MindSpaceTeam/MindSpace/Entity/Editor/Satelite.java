package com.MindSpaceTeam.MindSpace.Entity.Editor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

@Getter
@Setter
@TypeAlias("satelite")
public class Satelite extends EditorElements {
    private long parentId;
    private long initialAngle;

    public Satelite(List<Elements> elements) {
        super(elements);
    }
}
