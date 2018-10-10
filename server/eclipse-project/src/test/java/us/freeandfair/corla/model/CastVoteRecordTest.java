package us.freeandfair.corla.model;

import java.time.Instant;

import org.testng.annotations.*;

import us.freeandfair.corla.model.CastVoteRecord.RecordType;

import static org.testng.Assert.*;

public class CastVoteRecordTest {
    private CastVoteRecord cvr1;
    private CastVoteRecord cvr2;
    private CastVoteRecord cvr3;
    private CastVoteRecord cvr4;
    private Instant now;

    @BeforeClass
    public void CastVoteRecordTest() {
        now = Instant.now();
        cvr1 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch1", 1, "1-Batch1-1", null, null);
        cvr2 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch2", 1, "1-Batch2-1", null, null);
        cvr3 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch11", 1, "1-Batch11-1", null, null);
        cvr4 = new CastVoteRecord(RecordType.UPLOADED, now, 64L, 1, 1, 1, "Batch2", 1, "1-Batch2-1", null, null);

    }

    @Test()
    public void comparatorTest() {
        assertEquals(cvr1.compareTo(cvr2), -1);
        assertEquals(cvr2.compareTo(cvr4), 0);
        assertEquals(cvr3.compareTo(cvr2), 1);
    }
}
