// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> eventTimes = new ArrayList<>();
    for (Event event : events) {
      if (Collections.disjoint(event.getAttendees(), request.getAttendees())) {
        continue;
      }
      eventTimes.add(event.getWhen());
    }
    Collections.sort(eventTimes, TimeRange.ORDER_BY_START);
    ArrayList<TimeRange> availableRanges = findAvailableRanges(eventTimes, TimeRange.START_OF_DAY);
    ArrayList<TimeRange> availableRangesClone = (ArrayList<TimeRange>) availableRanges.clone();
    for (TimeRange range : availableRangesClone) {
      if (range.duration() < request.getDuration()) {
        availableRanges.remove(range);
      }
    }
    return availableRanges;
  }

  private ArrayList<TimeRange> findAvailableRanges(ArrayList<TimeRange> eventTimes, int prevEnd) {
    ArrayList<TimeRange> availableTimes = new ArrayList<>();
    for (int i = 0; i < eventTimes.size(); i++) {
      TimeRange time = eventTimes.get(i);
      if (time.start() > prevEnd) {
        availableTimes.add(TimeRange.fromStartEnd(prevEnd, time.start(), false));
      }
      prevEnd = Math.max(prevEnd, time.end());
    }
    availableTimes.add(TimeRange.fromStartEnd(prevEnd, TimeRange.END_OF_DAY, true));
    return availableTimes;
  }
}
