package ru.practicum.shareit.request;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("in-memory")
public class InMemoryRequestStorage implements RequestStorage {

    private final Map<Long, Request> requests = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Request addRequest(Request request) {
        request.setId(nextId.getAndIncrement());
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Request updateRequest(Long requestId, Request updatedRequest) {
        if (!requests.containsKey(requestId)) {
            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
        }
        updatedRequest.setId(requestId);
        requests.put(requestId, updatedRequest);
        return updatedRequest;
    }

    @Override
    public Collection<Request> getAllRequests() {
        return Collections.unmodifiableCollection(requests.values());
    }

    @Override
    public Optional<Request> findRequestById(Long requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }

    @Override
    public Collection<Request> getRequestsByRequestorId(Long requestorId) {
        return requests.values().stream()
                .filter(request -> Objects.equals(request.getRequestor().getId(), requestorId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteRequestById(Long requestId) {
        if (!requests.containsKey(requestId)) {
            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
        }
        requests.remove(requestId);
    }

    @Override
    public void deleteAllRequests() {
        requests.clear();
    }

    @Override
    public boolean existsById(Long requestId) {
        return requests.containsKey(requestId);
    }
}