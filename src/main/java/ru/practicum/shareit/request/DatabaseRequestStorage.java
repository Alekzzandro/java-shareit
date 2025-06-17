package ru.practicum.shareit.request;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@Profile("!in-memory")
public class DatabaseRequestStorage implements RequestStorage {

    private final RequestRepository requestRepository;

    public DatabaseRequestStorage(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public Request addRequest(Request request) {
        return requestRepository.save(request);
    }

    @Override
    public Request updateRequest(Long requestId, Request updatedRequest) {
        if (!requestRepository.existsById(requestId)) {
            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
        }
        updatedRequest.setId(requestId);
        return requestRepository.save(updatedRequest);
    }

    @Override
    public Collection<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public Optional<Request> findRequestById(Long requestId) {
        return requestRepository.findById(requestId);
    }

    @Override
    public Collection<Request> getRequestsByRequestorId(Long requestorId) {
        return requestRepository.findByRequestorId(requestorId);
    }

    @Override
    public void deleteRequestById(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new NoSuchElementException("Запрос с ID=" + requestId + " не найден");
        }
        requestRepository.deleteById(requestId);
    }

    @Override
    public void deleteAllRequests() {
        requestRepository.deleteAll();
    }

    @Override
    public boolean existsById(Long requestId) {
        return requestRepository.existsById(requestId);
    }
}