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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> times = new ArrayList<TimeRange>();
    if (request.getDuration() <= 1440){
      Collection<String> meeting_attendees = request.getAttendees();
      boolean attendee_overlap = false;
      times.add(TimeRange.WHOLE_DAY);
      for (Event event : events){
        Set<String> event_attendees = event.getAttendees();
        for (String attendee : event_attendees){
          if (meeting_attendees.contains(attendee)){
            attendee_overlap = true;
            break;
            }
        }
        if (attendee_overlap ==  true){ 
            ArrayList<TimeRange> to_remove = new ArrayList<TimeRange>();
            ArrayList<TimeRange> to_add= new ArrayList<TimeRange>();
            for(TimeRange freeslot : times){
                if (freeslot.overlaps(event.getWhen())){
                    if (event.getWhen().start()<=freeslot.end() && event.getWhen().start()>=freeslot.start()){
                        if (freeslot.start() != event.getWhen().start()){
                          to_add.add(TimeRange.fromStartEnd(freeslot.start(), event.getWhen().start(),false));
                        }
                    }
                    if (event.getWhen().end()<= freeslot.end() && event.getWhen().end()>= freeslot.start()){
                        if(event.getWhen().end() != freeslot.end()){
                          to_add.add(TimeRange.fromStartEnd(event.getWhen().end(), freeslot.end(),false));
                        }
                    }
                    to_remove.add(freeslot);
                }
            }
            times.removeAll(to_remove);
            times.addAll(to_add);
        }
    }
    ArrayList<TimeRange> duration_too_short = new ArrayList<TimeRange>();
    for (TimeRange freeslot : times){
        if (freeslot.duration()< request.getDuration()){
            duration_too_short.add(freeslot);
        }
    }
    times.removeAll(duration_too_short);
    return times;
  }
  else {
      return times;
      }
  }
}