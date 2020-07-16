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
    if (request.getDuration() <= TimeRange.WHOLE_DAY.duration()){
        Collection<TimeRange> mandatoryTimes = findAvailableMeetingTimes(events, request, true);
        Collection<TimeRange> optionalTimes =  findAvailableMeetingTimes(events,request, false);
        Collection<String> mandatoryAttendees = request.getAttendees();
        Collection<String> optionalAttendees = request.getOptionalAttendees();

        if(optionalAttendees.size()== 0){
            return mandatoryTimes;
        }
        if(mandatoryAttendees.size()== 0){
            return optionalTimes;
        }
        Collection<TimeRange> commonTimes = findCommonTimes(mandatoryTimes,optionalTimes, request);
        if(commonTimes.size() == 0){
            return mandatoryTimes;
        }
        else{
            return commonTimes;
        }
    
    }
    else {
      return Collections.emptyList();
    }
  }
  
  //for mandatoryOrOptional, true means mandatory attendees and false means optional attendees
   public Collection<TimeRange> findAvailableMeetingTimes(Collection<Event> events, MeetingRequest request, boolean mandatoryOrOptional){
      Collection<TimeRange> meetingTimes = new ArrayList<TimeRange>(Arrays.asList(TimeRange.WHOLE_DAY));
      Collection<String> meetingAttendees;
      if (mandatoryOrOptional == true){
          meetingAttendees = request.getAttendees();
      }
      else{
          meetingAttendees = request.getOptionalAttendees();
      }
      for (Event event : events){
        Set<String> eventAttendees = event.getAttendees();
        if (Collections.disjoint(meetingAttendees, eventAttendees) ==  false){ 
        ArrayList<TimeRange> toRemove = new ArrayList<TimeRange>();
          ArrayList<TimeRange> toAdd= new ArrayList<TimeRange>();
          for(TimeRange freeslot : meetingTimes){
            if (freeslot.overlaps(event.getWhen())){
              if (event.getWhen().start()<=freeslot.end() && event.getWhen().start()>=freeslot.start()){
                if (freeslot.start() != event.getWhen().start()){
                  toAdd.add(TimeRange.fromStartEnd(freeslot.start(), event.getWhen().start(),false));
                }
              }
              if (event.getWhen().end()<= freeslot.end() && event.getWhen().end()>= freeslot.start()){
                if(event.getWhen().end() != freeslot.end()){
                  toAdd.add(TimeRange.fromStartEnd(event.getWhen().end(), freeslot.end(),false));
                }
              }
              toRemove.add(freeslot);
            }
          }
          meetingTimes.removeAll(toRemove);
          meetingTimes.addAll(toAdd);
        }
      }
    ArrayList<TimeRange> durationTooShort = new ArrayList<TimeRange>();
    for (TimeRange freeslot : meetingTimes){
        if (freeslot.duration()< request.getDuration()){
            durationTooShort.add(freeslot);
        }
    }
    meetingTimes.removeAll(durationTooShort);
    return meetingTimes;
   }
     

   public Collection<TimeRange> findCommonTimes(Collection<TimeRange> mandatoryTimes , Collection<TimeRange> optionalTimes, MeetingRequest request){
      Collection<TimeRange> commonTimes = new ArrayList<TimeRange>();
      for (TimeRange mandatoryFreeslot : mandatoryTimes){
        for (TimeRange optionalFreeslot : optionalTimes){
          if (mandatoryFreeslot.overlaps(optionalFreeslot)){
            if (mandatoryFreeslot.contains(optionalFreeslot)){
              commonTimes.add(optionalFreeslot);
            }
            if (optionalFreeslot.contains(mandatoryFreeslot)){
              commonTimes.add(mandatoryFreeslot);
            }
            if (optionalFreeslot.end()< mandatoryFreeslot.end() && optionalFreeslot.start()< mandatoryFreeslot.start()){
              if(mandatoryFreeslot.start() != optionalFreeslot.end()){
                commonTimes.add(TimeRange.fromStartEnd(mandatoryFreeslot.start(), optionalFreeslot.end(),false));
                }
            }
            if (optionalFreeslot.end() > mandatoryFreeslot.end() && optionalFreeslot.start()> mandatoryFreeslot.start()){
              if(optionalFreeslot.start() != mandatoryFreeslot.end()){
                commonTimes.add(TimeRange.fromStartEnd(optionalFreeslot.start(),mandatoryFreeslot.end(),false));
              }
            }
          }
        }
      }
      ArrayList<TimeRange> durationTooShort = new ArrayList<TimeRange>();
      for (TimeRange freeslot : commonTimes){
        if (freeslot.duration()< request.getDuration()){
          durationTooShort.add(freeslot);
        }
      }
    commonTimes.removeAll(durationTooShort);
    return commonTimes;
   }
}