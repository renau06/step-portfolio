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
        ArrayList<TimeRange> mandatoryTimes = findAvailableMeetingTimes(events, request, true);
        ArrayList<TimeRange> optionalTimes =  findAvailableMeetingTimes(events,request, false);
        Collection<String> mandatoryAttendees = request.getAttendees();
        Collection<String> optionalAttendees = request.getOptionalAttendees();

        if(optionalAttendees.size()== 0){
            return mandatoryTimes;
        }
        if(mandatoryAttendees.size()== 0){
            return optionalTimes;
        }
        ArrayList<TimeRange> commonTimes = findCommonTimes(mandatoryTimes,optionalTimes, request);
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
  
  //for findTimesforMandatoryAttendees, true means mandatory attendees and false means optional attendees
   public ArrayList<TimeRange> findAvailableMeetingTimes(Collection<Event> events, MeetingRequest request, boolean findTimesforMandatoryAttendees){
      ArrayList<TimeRange> meetingTimes = new ArrayList<TimeRange>(Arrays.asList(TimeRange.WHOLE_DAY));
      Collection<String> meetingAttendees = findTimesforMandatoryAttendees ? request.getAttendees() : request.getOptionalAttendees();
      for (Event event : events){
        if (!Collections.disjoint(meetingAttendees, event.getAttendees())){ 
          removeEventTimefromAvailableTimes(event,meetingTimes);
        }
        ArrayList<TimeRange> durationTooShort = new ArrayList<TimeRange>();
          for (TimeRange freeslot : meetingTimes){
            if (freeslot.duration()< request.getDuration()){
              durationTooShort.add(freeslot);
            }
          }
          meetingTimes.removeAll(durationTooShort);
      }
    return meetingTimes;
   }
     

   public ArrayList<TimeRange> findCommonTimes(ArrayList<TimeRange> mandatoryTimes , ArrayList<TimeRange> optionalTimes, MeetingRequest request){
      ArrayList<TimeRange> commonTimes = new ArrayList<TimeRange>();
      for (TimeRange mandatoryFreeslot : mandatoryTimes){
        for (TimeRange optionalFreeslot : optionalTimes){
          if (mandatoryFreeslot.overlaps(optionalFreeslot)){
            if (mandatoryFreeslot.contains(optionalFreeslot)){
              commonTimes.add(optionalFreeslot);
            }
            else if (optionalFreeslot.contains(mandatoryFreeslot)){
              commonTimes.add(mandatoryFreeslot);
            }
            else if (optionalFreeslot.end() < mandatoryFreeslot.end() && optionalFreeslot.start()< mandatoryFreeslot.start() && mandatoryFreeslot.start() != optionalFreeslot.end()){
              commonTimes.add(TimeRange.fromStartEnd(mandatoryFreeslot.start(), optionalFreeslot.end(),false));   
            }
            else if (optionalFreeslot.end() > mandatoryFreeslot.end() && optionalFreeslot.start()> mandatoryFreeslot.start() && optionalFreeslot.start() != mandatoryFreeslot.end()){
              commonTimes.add(TimeRange.fromStartEnd(optionalFreeslot.start(),mandatoryFreeslot.end(),false));
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


//if event time overlaps with any of the free slots, create new free slots that no longer overlap and remove the previous free slot
public ArrayList<TimeRange> removeEventTimefromAvailableTimes (Event event, ArrayList<TimeRange> meetingTimes){
  ArrayList<TimeRange> toRemove = new ArrayList<TimeRange>();
  ArrayList<TimeRange> toAdd= new ArrayList<TimeRange>();
  for(TimeRange freeslot : meetingTimes){
    if (freeslot.overlaps(event.getWhen())){
      if (event.getWhen().start()<=freeslot.end() && event.getWhen().start()> freeslot.start()){
        toAdd.add(TimeRange.fromStartEnd(freeslot.start(), event.getWhen().start(),false));
      }
      if (event.getWhen().end() < freeslot.end() && event.getWhen().end()>= freeslot.start()){
        toAdd.add(TimeRange.fromStartEnd(event.getWhen().end(), freeslot.end(),false));
      }
      toRemove.add(freeslot);
    }
  }
  meetingTimes.removeAll(toRemove);
  meetingTimes.addAll(toAdd);
  return meetingTimes;
 }

}