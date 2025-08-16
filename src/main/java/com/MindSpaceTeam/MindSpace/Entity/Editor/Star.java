package com.MindSpaceTeam.MindSpace.Entity.Editor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

@Getter
@Setter
@TypeAlias("star")
public class Star extends EditorElements {
    private int x;
    private int y;

    public Star(List<Elements> elements) {
        super(elements);
    }
}
