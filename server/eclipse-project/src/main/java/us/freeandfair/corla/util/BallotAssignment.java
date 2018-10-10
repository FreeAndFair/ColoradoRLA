/*
 * Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for assigning ballots to audit boards.
 *
 * @author Democracy Works, Inc. <dev@democracy.works>
 */
public final class BallotAssignment {
  /**
   * Prevent public construction
   */
  private BallotAssignment() {
  }

  /**
   * Assign a given number of ballots to a given number of boards.
   *
   * Any "extra" ballots that do not divide evenly into the number of boards
   * will be randomly assigned to a board.
   *
   * @param ballots number of ballots to assign
   * @param boards desired number of boards
   * @return a list with each element representing a board containing the number
   *         of ballots to assign that board.
   */
  public static List<Integer> assignToBoards(final int ballots,
                                             final int boards)
      throws IllegalArgumentException {
    if (ballots < 0) {
      throw new IllegalArgumentException("Number of ballots cannot be < 0.");
    }

    if (boards <= 0) {
      throw new IllegalArgumentException("Number of boards cannot be <= 0.");
    }

    // Integer division
    final int ballotsPerBoard = ballots / boards;
    final int leftoverBallots = ballots % boards;

    // Assign all boards the even number of ballots
    final List<Integer> result =
        new ArrayList<Integer>(Collections.nCopies(boards, ballotsPerBoard));

    // Assign the leftovers
    for (int i = 0; i < leftoverBallots; i++) {
      result.set(i, result.get(i) + 1);
    }

    // Shuffle the results, for fairness!
    Collections.shuffle(result);

    return result;
  }
}
