package com.MindSpaceTeam.MindSpace.Entity.Editor;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection="workspaces")
public class EditorElements {
    private int id;
    private List<Elements> elements;

    public EditorElements(List<Elements> elements) {
        this.elements = elements;
    }
}
