package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private JdbcTemplate template;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry origin) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        template.update(conn -> {
            PreparedStatement statement = conn.prepareStatement(
                    "insert into time_entries (project_id, user_id, date, hours) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setLong(1, origin.getProjectId());
            statement.setLong(2, origin.getUserId());
            statement.setDate(3, Date.valueOf(origin.getDate()));
            statement.setInt(4, origin.getHours());

            return statement;
        }, generatedKeyHolder);
        return find(generatedKeyHolder.getKey().longValue());
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor = (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;

    @Override
    public TimeEntry find(long timeEntryId) {
        return template.query("select id, project_id, user_id, date, hours from time_entries where id = ?",
                new Object[]{timeEntryId}, extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return template.query("select id, project_id, user_id, date, hours from time_entries", mapper);
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry fromEntry) {
        template.update("update time_entries " +
                        "set project_id = ?, user_id = ?, date = ?, hours = ? " +
                        "where id = ?",
                fromEntry.getProjectId(), fromEntry.getUserId(), fromEntry.getDate(), fromEntry.getHours(),
                timeEntryId);
        return find(timeEntryId);
    }

    @Override
    public void delete(long timeEntryId) {
        template.update("delete from time_entries where id = ?", timeEntryId);
    }
}
