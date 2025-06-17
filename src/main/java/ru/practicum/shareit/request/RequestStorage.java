package ru.practicum.shareit.request;

import java.util.Collection;
import java.util.Optional;

public interface RequestStorage {
    Request addRequest(Request request);

    Request updateRequest(Long requestId, Request updatedRequest);

    Collection<Request> getAllRequests();

    Optional<Request> findRequestById(Long requestId);

    Collection<Request> getRequestsByRequestorId(Long requestorId);

    void deleteRequestById(Long requestId);

    void deleteAllRequests();

    boolean existsById(Long requestId);
}