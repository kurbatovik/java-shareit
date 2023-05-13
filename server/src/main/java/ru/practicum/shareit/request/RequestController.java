package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Util;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {

    private final RequestService requestService;
    private final RequestMapper requestMapper;


    // Добавление нового запроса
    @PostMapping
    public RequestDto add(@RequestHeader(Variables.USER_ID) Long userId,
                          @RequestBody RequestDto requestDto) {
        log.info("Add request on item {}, user id: {}", requestDto, userId);
        Request request = requestMapper.fromDto(requestDto);
        requestDto = requestMapper.toDto(requestService.add(userId, request));
        return requestDto;
    }

    // Просмотр информации о конкретном запросе по его идентификатору
    @GetMapping("/{requestId}")
    public RequestDto getById(@RequestHeader(Variables.USER_ID) Long userId,
                              @PathVariable Long requestId) {
        log.info("Request from user ID: {} on get request. ID: {}", userId, requestId);
        return requestMapper.toDto(requestService.getById(requestId, userId));
    }

    //получить список запросов, созданных другими пользователями
    @GetMapping
    public List<RequestDto> getRequesterAll(@RequestHeader(Variables.USER_ID) Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = Util.getPageable(from, size);
        log.info("Request on get all requests from user id: {}", userId);
        return requestService.getAllForUser(userId, pageable)
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    // Просмотр пользователем списка всех его запросов с указанием ответов на них
    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader(Variables.USER_ID) Long userId,
                                   @RequestParam(defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = Util.getPageable(from, size);
        List<RequestDto> dtoList = requestService.getAll(userId, pageable)
                .stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
        log.info("Request on get all requests");
        return dtoList;
    }
}
