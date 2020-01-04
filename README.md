# Group Tracker


Group Tracker is an Android application that provides users with an interface designed to help make traveling easier and safer. Users can join or create events with a group chat and view a shared map. The map will display a live location of all members in the group and allow them to broadcast messages.

Developer: **Syed Sadman**


# Features
The app has the following features:

* [X] Users can sign in and register  
* [ ] Create and Join events
    * [ ] Rediredct users to default or events activity
    * [ ] Search/Join list of events
    * [ ] Persist joined events
    * [ ] Create new events
    * [ ] Set images for events
    * [ ] Events can be open or password protected
    * [ ] Allow users to be invited to an event group
    * [ ] Setup GPS to location
    * [ ] Admin can remove members from group
* [ ] Configure Shared Map
    * [ ] Show marker for all members
    * [ ] Enable live preview
    * [ ] Set common destination marker
    * [ ] Broadcast message 
* [ ] Group chats 
* [ ] TBA



Short Demo             
:-------------------------:
<img src='Demo.gif' title='Demo' width='' alt='Demo' />


## Notes
Thought process is detailed in commit messages

== vs .equals 

First the user logs in as normal. During login, create user object as usual but now, check firebase if there is an eventid (null by default). 

if(eventid == null) {
From login class, set eventid of User to null. 
Change to default screen 
Loop through all groups in Events.json and add them to EventViewer.class recyclerview. 
If user clicks join group, then show them that list. If a group is joined, then update user.json eventid and event.json members list. 
Now change User constructor to set eventid of User to userid.
Leave activity and go to event screen. 
}

start the event activity

if(evemtid != null) {
Get references from Event.json based on the eventid in user object
if userid is the same as in userid from event.json, then show the delete group button
if uderid not same, show leave group button
display all the info
}

Now Assume the user leaves the group. Change the user object to reflect it. Take the user to defult screen. 

If user DELETES group, then remvoe the entire group from firebase and update user object. To delete group, first update all the members eventid status by getting their userid. Then delete group from firebase and go to default screen. 


"User"
-UID {
    email, name, ...
    // WHen user creates/joins an event, give it ID
    // if there exists an event id, then the user is in a group. Else its null
    event: eventID
    }
}

// Everytime a user is 
"Events"
-eventID {
    eventID, UID (Keep track of leader), name, location, desc, time...
    -members {
        // Create ordered list of UID's
        0: UID
        ...
    }
}
...

// When referencing users inside an event, for loop through that events members
// DISPLAY ALL EVENTS
    loop through all eventID using iterator
    eventID = i.next().toString();
// GETTING ALL USERS in "Events"
    reference json for "Users" and loop through each UID and record it
    UID = ID_of_clicked_event.getJSONObject("members").get(i)
// ADD USERS TO Event
    reference current event that user clicked on by evenID
    add current UID to members
    


Challenges Encountered:
- Allowing users to join and leave events
- Figuring out how to structure objects correctly in firebase
- Setting up different permissions for users based on choice and event status

https://medium.com/@shubham9032/structure-for-group-chat-using-firebase-583a84d794c2

## License

    Copyright 2019 Syed Sadman

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.






