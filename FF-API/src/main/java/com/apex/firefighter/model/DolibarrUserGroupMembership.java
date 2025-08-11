package com.apex.firefighter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "llx_usergroup_user")
public class DolibarrUserGroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rowid")
    private Long id;

    @Column(name = "fk_user", nullable = false)
    private Long userId;

    @Column(name = "fk_usergroup", nullable = false)
    private Long groupId;

    @Column(name = "import_key")
    private String importKey;

    public DolibarrUserGroupMembership() {}

    public DolibarrUserGroupMembership(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    //Getters and setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getImportKey() {
        return importKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setImportKey(String importKey) {
        this.importKey = importKey;
    }
}
