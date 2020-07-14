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
    Collection<TimeRange> mandatory_times = new ArrayList<TimeRange>();
    Collection<TimeRange> optional_times = new ArrayList<TimeRange>();
    Collection<TimeRange> common_times = new ArrayList<TimeRange>();
    Collection<String> mandatory_attendees = request.getAttendees();
    Collection<String> optional_attendees = request.getOptionalAttendees();
    if (request.getDuration() <= 1440){
        mandatory_times = findMandatoryAttendeeTimes(events, request);
        optional_times =  findOptionalAttendeeTimes(events,request);
        common_times = findCommonTimes(mandatory_times,optional_times, request);

        if(optional_attendees.size()== 0){
            return mandatory_times;
        }
        else if(mandatory_attendees.size()== 0){
            return optional_times;
        }
        if(optional_attendees.size() != 0 && mandatory_attendees.size()!= 0 && common_times.size() == 0){
            System.out.println("mandatory times returned");
            return mandatory_times;
        }
        else{
            System.out.println("common_times returned");
            return common_times;
        }
    
    }
  else {
      return mandatory_times;
      }
  }


   public Collection<TimeRange> findMandatoryAttendeeTimes(Collection<Event> events, MeetingRequest request){
       Collection<TimeRange> mandatory_times = new ArrayList<TimeRange>();
       Collection<String> mandatory_attendees = request.getAttendees();
       mandatory_times.add(TimeRange.WHOLE_DAY);
      
       for (Event event : events){
        boolean mandatory_attendee_overlap = false;
        Set<String> event_attendees = event.getAttendees();
        for (String attendee : event_attendees){
          if (mandatory_attendees.contains(attendee)){
            mandatory_attendee_overlap = true;
            break;
            }
        }
        if (mandatory_attendee_overlap ==  true){ 
            ArrayList<TimeRange> to_remove = new ArrayList<TimeRange>();
            ArrayList<TimeRange> to_add= new ArrayList<TimeRange>();
            for(TimeRange freeslot : mandatory_times){
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
            mandatory_times.removeAll(to_remove);
            mandatory_times.addAll(to_add);
        }
    }
    ArrayList<TimeRange> mandatory_duration_too_short = new ArrayList<TimeRange>();
    for (TimeRange freeslot : mandatory_times){
        if (freeslot.duration()< request.getDuration()){
            mandatory_duration_too_short.add(freeslot);
        }
    }
    mandatory_times.removeAll(mandatory_duration_too_short);
    return mandatory_times;
   }


   public Collection<TimeRange> findOptionalAttendeeTimes(Collection<Event> events, MeetingRequest request){
    Collection<TimeRange> optional_times = new ArrayList<TimeRange>();
    Collection<String> optional_attendees = request.getOptionalAttendees();
    optional_times.add(TimeRange.WHOLE_DAY);
    for (Event event : events){
      boolean optional_attendee_overlap = false;
        Set<String> event_attendees = event.getAttendees();
        for (String attendee : event_attendees){
          if (optional_attendees.contains(attendee)){
            optional_attendee_overlap = true;
            break;
            }
        }
        if (optional_attendee_overlap ==  true){ 
          ArrayList<TimeRange> to_remove = new ArrayList<TimeRange>();
          ArrayList<TimeRange> to_add= new ArrayList<TimeRange>();
          for(TimeRange freeslot : optional_times){
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
        optional_times.removeAll(to_remove);
        optional_times.addAll(to_add);
        } 
    }
    ArrayList<TimeRange> optional_duration_too_short = new ArrayList<TimeRange>();
    for (TimeRange freeslot : optional_times){
        if (freeslot.duration()< request.getDuration()){
            optional_duration_too_short.add(freeslot);
        }
    }
    optional_times.removeAll(optional_duration_too_short);
    return optional_times;
}

   public Collection<TimeRange> findCommonTimes(Collection<TimeRange> mandatory_times , Collection<TimeRange> optional_times, MeetingRequest request){
      Collection<TimeRange> common_times = new ArrayList<TimeRange>();
    
      for (TimeRange mandatory_freeslot : mandatory_times){
        for (TimeRange optional_freeslot : optional_times){
          System.out.println(mandatory_freeslot + "and "+ optional_freeslot);
          if (mandatory_freeslot.overlaps(optional_freeslot)){
            if (mandatory_freeslot.contains(optional_freeslot)){
              common_times.add(optional_freeslot);
              System.out.println("1");
            }
            if (optional_freeslot.contains(mandatory_freeslot)){
              common_times.add(mandatory_freeslot);
              System.out.println("2");
            }
            if (optional_freeslot.end()< mandatory_freeslot.end() && optional_freeslot.start()< mandatory_freeslot.start()){
              if(mandatory_freeslot.start() != optional_freeslot.end()){
                common_times.add(TimeRange.fromStartEnd(mandatory_freeslot.start(), optional_freeslot.end(),false));
                System.out.println("3");
                }
            }
            if (optional_freeslot.end() > mandatory_freeslot.end() && optional_freeslot.start()> mandatory_freeslot.start()){
              if(optional_freeslot.start() != mandatory_freeslot.end()){
                common_times.add(TimeRange.fromStartEnd(optional_freeslot.start(),mandatory_freeslot.end(),false));
                System.out.println("4");
                }
            }
          }
        }
      }
      System.out.println("commontimes:"+ common_times);
      ArrayList<TimeRange> common_duration_too_short = new ArrayList<TimeRange>();
      for (TimeRange freeslot : common_times){
        if (freeslot.duration()< request.getDuration()){
          common_duration_too_short.add(freeslot);
        }
      }
    common_times.removeAll(common_duration_too_short);
    return common_times;
   }
}