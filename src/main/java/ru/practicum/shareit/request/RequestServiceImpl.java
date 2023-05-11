package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public Request add(Long userId, Request request) {
        User user = getUserOrThrowException(userId);
        request.setCreated(LocalDateTime.now())
                .setRequester(user);
        request = requestRepository.save(request);
        return request;
    }

    @Override
    public Request getById(Long requestId, Long userId) {
        User requester = getUserOrThrowException(userId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with ID: {0} not found", requestId));
    }

    @Override
    public List<Request> getAll(long userId, Pageable pageable) {
        return requestRepository.findAllByRequesterIdNot(userId, pageable).toList();
    }

    @Override
    public List<Request> getAllForUser(Long userId, Pageable pageable) {
        User requester = getUserOrThrowException(userId);
        return requestRepository.findAllByRequester(requester, pageable).toList();
    }

    private User getUserOrThrowException(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Variables.USER_WITH_ID_NOT_FOUND, userId));
    }
}
