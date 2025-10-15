package eu.irrationalcharm.messagepersistenceservice.service;


import eu.irrationalcharm.messagepersistenceservice.dto.ConversationSummaryDto;
import eu.irrationalcharm.messagepersistenceservice.mapper.ConversationMapper;
import eu.irrationalcharm.messagepersistenceservice.model.Conversation;
import eu.irrationalcharm.messagepersistenceservice.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrieveChatHistoryService {

    private final ConversationRepository conversationRepository;

    @PreAuthorize("authenticated()")
    public List<ConversationSummaryDto> fetchLastMessages(Authentication authentication) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContainsOrderByUpdatedAtDesc(authentication.getName());
        if (conversations.isEmpty())
            return Collections.emptyList();

        List<ConversationSummaryDto> conversationDto = new ArrayList<>(conversations.size());

        conversations.forEach(conv ->
            conversationDto.add(ConversationMapper.mapToDto(conv)));



        return conversationDto;
    }
}
