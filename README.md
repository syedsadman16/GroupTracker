# Group Tracker


Group Tracker is an Android application that makes it easier to plan trips with friends. Users can join or create events, communicate in a group chat and share their location with each other. The map displays live locations of all members in the group to make it easier to meet up and plan their commute. 

Developer: **Syed Sadman**


# Features
The app has the following features:

* [X] Users can sign in and register  
* [ ] Create and Join events
    * [X] Rediredct users to default or events activity
    * [X] View/Join list of events
    * [X] Persist joined events
    * [X] Create new events
    * [X] Set images for events
    * [X] Autocomplete location
    * [X] Setup GPS to location
    * [X] Edit events
    * [ ] Events can be open or password protected
    * [ ] Allow users to be invited to an event group
    * [ ] View all users in event
    * [ ] Admin can remove members from group
* [ ] Configure Shared Map
    * [ ] Show marker for all members
    * [ ] Click to zoom into member
    * [X] Enable live preview
    * [ ] Set common destination 
    * [ ] Show text over marker 
* [ ] Group chats 
* [ ] Photo Sharing
* [ ] TBA



Preview             
:-------------------------:
<img src='demo3.gif' title='Demo' width='' alt='Demo' /> 



## Notes
Firebase - Does not support arrays directly since array index is always changing and firebase is a real time db. Creates problems when multiple users doing operations, array index shifts. Instead, it creates list of objects

---
ToDo: Google Maps Integration

Single user >> Multi User
Get user location once user clicks frag. If they leave fragment, stop listener. 
Firebase, each user will have Lat, Long, message params >> Create it for test users
Update coordinates for each user(current user). Push to Firebase every 2-3 seconds. >> Create function that pushes for time intervals
Firebase onDataChanged will add marker each time member moves: >> 
        LatLng user = new LatLng(-34, 151); // LatLng(long, lat)
        mMap.addMarker(new MarkerOptions().position(sydney).title("Name of person"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
onResume: Keeep updating coordinates and onStop: remove listener

Button to 

---

Events: 

Chat: 

Profile:


## Bugs/Challenges:
- Allowing users to join and leave events => create static User.eventid and use MainActivity to redirect to appropriate event. Also update firebase Users.json with an eventid status and maintain a members list in Events.json
- AutocompletePlacesFragment was depricated => Used newer PlacesAPI 
- DialogFragment for places api, null pointer when using it on fragments => launch activity instead when user clicks location edit text
- RecylerView only shows 1 item => layout_height of LinearLayout was set to to match_parent. Replaced Volley with DataSnapshot
- Creating and updating members list in firebase => Create user objects inside members since Firebase doesnt support arrays directly. Then easily get child.getvalue() for each  member
- Finding a way to upload image into firebase and retrieve it, without new dependencies and saving space on database =>  Encode image into base64 String and save to Firebase
- Picking image from gallery and uploading it to ImageView => https://demonuts.com/pick-image-gallery-camera-android/
- Images not getting pushed to firebase, somehow broke => Created Logs to test, suddenly works?
- Firebase doesn't create event with all fields, only createdBy and Date. Sometimes works => Create Event object, removed wrong reference, push class to firebase instead
- After creating event, doesn't launch new event. Sometimes works, sometimes doesn't => Changed from Volley to Firebase Datasnapshot. Same solution as below
- Cannot create new event after leaving event, new content isnt pushed to firebase => BUG:reference.child(key).setValue(key) was removed 
- ** When deleting event, it just keeps redirecting back to ViewEventsFragment instead of going to main. If event is not deleted, it works as intended. 
  - Tried different intents, passing extras, putting null conditions around ViewEventsFragment
  - Tried deleting from Main activity, still goes back to ViewEventFragment. 
  - Set null within delete function or launch intent within delete
  - Even if I delete it from random activity such as MapActivity, it automatically goes into ViewEventsFragment and crashes. Every deletion seems to be linked to ViewEventsFragment 
  - Switch to Main, wait 5 seconds, then delete event. After the 5 seconds, app crashes in ViewEventsFragment. It switches intents, loads new fragment, waits 5 seconds then crashes in the previous fragment. 
  - The only way to get to ViewEventsFragment is if User.eventid != "null". However, app ignores this and crashes in ViewEventsFragment even though I manually changed User.eventid to "null" upon deletion. 
  ==> Restructure and add a removeEventListener. When I added an addEventListenr,it was ALWAYS listening for onDataChanged. When event was deleted, this was set to null so it was trying to fetch null data which caused it to crash. Every listener registered needs to be unreggistered!
  - Location doesnt work despite beign turned on --> Enable in app location permissions


## Resources Used

Images 
https://stackoverflow.com/questions/36117882/is-it-possible-to-store-image-to-firebase-in-android
https://www.thecrazyprogrammer.com/2016/10/android-convert-image-base64-string-base64-string-image.html
https://demonuts.com/pick-image-gallery-camera-android/

Search event location
https://developers.google.com/places/android-sdk/autocomplete
https://www.youtube.com/watch?v=6Trdd9EnmqY
https://medium.com/skillhive/android-google-places-autocomplete-feature-bb3064308f05

Location
https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
https://www.youtube.com/playlist?list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi
https://medium.com/@shubham9032/structure-for-group-chat-using-firebase-583a84d794c2
- Follow user location
https://stackoverflow.com/questions/44992014/how-to-get-current-location-in-googlemap-using-fusedlocationproviderclient


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


