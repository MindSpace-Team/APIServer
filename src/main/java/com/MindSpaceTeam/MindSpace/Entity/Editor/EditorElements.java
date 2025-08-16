package com.MindSpaceTeam.MindSpace.Entity.Editor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Setter
@Getter
@Document(collection="workspaces")
public class EditorElements {
    @MongoId
    private long id;
    private List<Elements> elements;

    public EditorElements(List<Elements> elements) {
        this.elements = elements;
    }
}
