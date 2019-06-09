package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository repo;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCountr;

    public TimeEntryController(TimeEntryRepository repository, MeterRegistry meterRegistry) {
        this.repo = repository;
        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCountr = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry entry) {
        TimeEntry created = repo.create(entry);
        actionCountr.increment();
        timeEntrySummary.record(repo.list().size());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long timeEntryId) {
        TimeEntry found = repo.find(timeEntryId);
        if (found == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        actionCountr.increment();
        return ResponseEntity.ok(found);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        actionCountr.increment();
        return ResponseEntity.ok(repo.list());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable("id") long timeEntryId, @RequestBody TimeEntry from) {
        TimeEntry updated = repo.update(timeEntryId, from);
        if (updated == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        actionCountr.increment();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long timeEntryId) {
        repo.delete(timeEntryId);
        actionCountr.increment();
        timeEntrySummary.record(repo.list().size());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
