package com.example.gallerycart.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tag",
        indices = {@Index(value = "tagName", unique = true)})
public class Tag {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String tagName;
    private String description;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}