package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    private Map<Long, TimeEntry> repo;
    private long nextId;

    public InMemoryTimeEntryRepository() {
        repo = new HashMap<>();
        nextId = 1L;
    }

    public TimeEntry create(TimeEntry origin) {
        TimeEntry next = new TimeEntry(nextId, origin.getProjectId(), origin.getUserId(), origin.getDate(), origin.getHours());
        nextId++;
        repo.put(next.getId(), next);
        return next;
    }

    public TimeEntry find(long timeEntryId) {
        return repo.get(timeEntryId);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(repo.values());
    }

    public TimeEntry update(long timeEntryId, TimeEntry fromEntry) {
        TimeEntry origin = repo.get(timeEntryId);
        if (origin == null) return null;
        origin.setProjectId(fromEntry.getProjectId());
        origin.setUserId(fromEntry.getUserId());
        origin.setDate(fromEntry.getDate());
        origin.setHours(fromEntry.getHours());
        repo.put(origin.getId(), origin);
        return origin;
    }

    public void delete(long timeEntryId) {
        repo.remove(timeEntryId);
    }
}
