package com.blacknebula.scrumpoker.service;

import com.blacknebula.scrumpoker.dto.SessionCreationDto;
import com.blacknebula.scrumpoker.ApplicationTest;
import com.blacknebula.scrumpoker.builders.SessionCreationDtoBuilder;
import com.blacknebula.scrumpoker.builders.SessionEntityBuilder;
import com.blacknebula.scrumpoker.dto.SessionDto;
import com.blacknebula.scrumpoker.entity.SessionEntity;
import com.blacknebula.scrumpoker.entity.StoryEntity;
import com.blacknebula.scrumpoker.entity.UserEntity;
import com.blacknebula.scrumpoker.enums.CardSetEnum;
import com.blacknebula.scrumpoker.exception.CustomErrorCode;
import com.blacknebula.scrumpoker.exception.CustomException;
import com.blacknebula.scrumpoker.repository.SessionRepository;
import com.blacknebula.scrumpoker.repository.StoryRepository;
import com.blacknebula.scrumpoker.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author hazem
 */
public class SessionServiceTest extends ApplicationTest {

    @Autowired
    private SessionService sessionService;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * @verifies throw an error if withSessionId is null or empty
     * @see SessionService#getSession(String)
     */
    @Test
    public void getSession_shouldThrowAnErrorIfSessionIdIsNullOrEmpty() throws Exception {
        try {
            sessionService.getSession(null);
            Assert.fail("shouldThrowAnErrorIfSessionIdIsNullOrEmpty");
        } catch (CustomException e) {
            Assertions.assertThat(e.getCustomErrorCode()).isEqualTo(CustomErrorCode.BAD_ARGS);
        }
    }

    /**
     * @verifies throw an error if session does not exist
     * @see SessionService#getSession(String)
     */
    @Test
    public void getSession_shouldThrowAnErrorIfSessionDoesNotExist() throws Exception {
        try {
            sessionService.getSession("invalid_session_id");
            Assert.fail("shouldThrowAnErrorIfSessionDoesNotExist");
        } catch (CustomException e) {
            Assertions.assertThat(e.getCustomErrorCode()).isEqualTo(CustomErrorCode.OBJECT_NOT_FOUND);
        }
    }

    /**
     * @verifies return valid session if it exists
     * @see SessionService#getSession(String)
     */
    @Test
    public void getSession_shouldReturnValidSessionIfItExists() throws Exception {
        // given
        final String sessionId = "sessionId";
        final SessionEntity sessionEntity = SessionEntityBuilder.builder()
                .withSessionId(sessionId)
                .build();
        sessionRepository.save(sessionEntity);

        //when
        final SessionDto session = sessionService.getSession(sessionId);

        //then
        Assertions.assertThat(session).isNotNull();
        Assertions.assertThat(session.getSessionId()).isEqualTo(sessionId);
    }

    /**
     * @verifies throw an error if sessionDto is null
     * @see SessionService#createSession(SessionCreationDto, Consumer)
     */
    @Test
    public void createSession_shouldThrowAnErrorIfSessionDtoIsNull() throws Exception {
        try {
            sessionService.createSession(null, (t) -> {
            });
            Assert.fail("shouldThrowAnErrorIfSessionDtoIsNull");
        } catch (CustomException e) {
            Assertions.assertThat(e.getCustomErrorCode()).isEqualTo(CustomErrorCode.BAD_ARGS);
        }
    }

    /**
     * @verifies throw an error if withUsername is null
     * @see SessionService#createSession(SessionCreationDto, Consumer)
     */
    @Test
    public void createSession_shouldThrowAnErrorIfUsernameIsNull() throws Exception {
        final SessionCreationDto sessionCreationDto = SessionCreationDtoBuilder.builder()
                .withCardSet(CardSetEnum.FIBONACCI)
                .build();
        try {
            sessionService.createSession(sessionCreationDto, (t) -> {
            });
            Assert.fail("shouldThrowAnErrorIfUsernameIsNull");
        } catch (CustomException e) {
            Assertions.assertThat(e.getCustomErrorCode()).isEqualTo(CustomErrorCode.BAD_ARGS);
        }
    }

    /**
     * @verifies throw an error if cardSet is null
     * @see SessionService#createSession(SessionCreationDto, Consumer)
     */
    @Test
    public void createSession_shouldThrowAnErrorIfCardSetIsNull() throws Exception {
        final SessionCreationDto sessionCreationDto = SessionCreationDtoBuilder.builder()
                .withUsername("username")
                .build();
        try {
            sessionService.createSession(sessionCreationDto, (t) -> {
            });
            Assert.fail("shouldThrowAnErrorIfCardSetIsNull");
        } catch (CustomException e) {
            Assertions.assertThat(e.getCustomErrorCode()).isEqualTo(CustomErrorCode.BAD_ARGS);
        }
    }

    /**
     * @verifies create session and an admin user
     * @see SessionService#createSession(SessionCreationDto, Consumer)
     */
    @Test
    public void createSession_shouldCreateSessionAndAnAdminUser() throws Exception {
        // given
        final SessionCreationDto sessionCreationDto = SessionCreationDtoBuilder.builder()
                .withUsername("username")
                .withCardSet(CardSetEnum.FIBONACCI)
                .build();
        //when
        final SessionCreationDto createdSession = sessionService.createSession(sessionCreationDto, (t) -> {
        });

        //then
        Assertions.assertThat(createdSession).isNotNull();
        Assertions.assertThat(createdSession.getSessionId()).isNotNull();

        final SessionEntity sessionEntity = sessionRepository.findOne(createdSession.getSessionId());
        Assertions.assertThat(sessionEntity).isNotNull();
        Assertions.assertThat(sessionEntity.getSessionId()).isNotNull();
        Assertions.assertThat(sessionEntity.getCardSet().name()).isEqualTo(sessionCreationDto.getCardSet());
        Assertions.assertThat(sessionEntity.getSprintName()).isNotNull();

        final UserEntity userEntity = userRepository.findUser(createdSession.getSessionId(), createdSession.getUsername());
        Assertions.assertThat(userEntity).isNotNull();
        Assertions.assertThat(userEntity.getUserId().getEntityId()).isEqualTo(createdSession.getUsername());
        Assertions.assertThat(userEntity.isAdmin()).isTrue();
    }

    /**
     * @verifies create stories if stories list is not empty
     * @see SessionService#createSession(SessionCreationDto, Consumer)
     */
    @Test
    public void createSession_shouldCreateStoriesIfStoriesListIsNotEmpty() throws Exception {
        // given
        final SessionCreationDto sessionCreationDto = SessionCreationDtoBuilder.builder()
                .withUsername("username")
                .withCardSet(CardSetEnum.FIBONACCI)
                .withStories()
                .addStory("story-1")
                .addStory("story-2")
                .collect()
                .build();
        //when
        final SessionCreationDto createdSession = sessionService.createSession(sessionCreationDto, (t) -> {
        });

        //then
        final List<StoryEntity> storyEntities = storyRepository.findBySessionId(createdSession.getSessionId());
        Assertions.assertThat(storyEntities).hasSize(2);
    }
}