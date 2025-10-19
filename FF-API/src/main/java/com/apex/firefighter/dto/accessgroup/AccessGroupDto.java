package com.apex.firefighter.dto.accessgroup;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for Access Group
 */
public class AccessGroupDto {

    private Long id;

    @NotBlank(message = "Group ID is required")
    private String groupId;

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    // Constructors
    public AccessGroupDto() {}

    public AccessGroupDto(String groupId, String name, String description) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
