package com.influans.sp.entity;

import com.influans.sp.dto.VoteDto;
import com.influans.sp.entity.def.VoteEntityDef;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "vote")
public class VoteEntity {
    @Id
    private String voteId;
    @Field(VoteEntityDef.STORY_ID)
    private String storyId;
    @Field(VoteEntityDef.SESSION_ID)
    private String sessionId;
    @Field(VoteEntityDef.USERNAME)
    private String username;
    @Field(VoteEntityDef.VALUE)
    private String value;

    public VoteEntity() {
    }

    public VoteEntity(VoteDto voteDto) {
        this.voteId = voteDto.getVoteId();
        this.sessionId = voteDto.getSessionId();
        this.storyId = voteDto.getStoryId();
        this.username = voteDto.getUsername();
        this.value = voteDto.getValue();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVoteId() {
        return voteId;
    }

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }
}