package us.freeandfair.corla.util;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.*;

public class BallotAssignmentTest {
  @Test()
  public void assignToBoardsTest() {

    final List<Integer> assignment1 = BallotAssignment.assignToBoards(36, 4);
    assertEquals(assignment1.size(), 4);
    assertEquals(Collections.frequency(assignment1, 9), 4);

    final List<Integer> assignment2 = BallotAssignment.assignToBoards(35, 4);
    assertEquals(assignment2.size(), 4);
    assertEquals(Collections.frequency(assignment2, 8), 1);
    assertEquals(Collections.frequency(assignment2, 9), 3);

    final List<Integer> assignment3 = BallotAssignment.assignToBoards(567, 5);
    assertEquals(assignment3.size(), 5);
    assertEquals(Collections.frequency(assignment3, 113), 3);
    assertEquals(Collections.frequency(assignment3, 114), 2);

    final List<Integer> assignment4 = BallotAssignment.assignToBoards(567, 1);
    assertEquals(assignment4.size(), 1);
    assertEquals(Collections.frequency(assignment4, 567), 1);

    final List<Integer> assignment5 = BallotAssignment.assignToBoards(3, 5);
    assertEquals(assignment5.size(), 5);
    assertEquals(Collections.frequency(assignment5, 1), 3);
    assertEquals(Collections.frequency(assignment5, 0), 2);
  }
}
