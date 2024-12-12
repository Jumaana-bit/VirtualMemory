# __Virtual Memory App__ #

## Overview ##
The Virtual Memory App is an Android application designed to assist seniors and individuals with dementia by providing a user-friendly interface to manage memories, reminders, and important information. The app features personalized photo albums, contact management, and multimedia support for an enriching user experience.

 - - - -

 ## Features ##
1. __Personalized Photo Album__
* Create folders like "Family" or "Friends".
* Add, view, label, and delete photos and videos within folders.
* Each media item can be labeled for easy identification.
* Supports animations for folder icons to make navigation more intuitive.
2. __Contact Management__
  * Store contacts with categories such as "Friends" or "Family."
  * Each contact includes a name, relationship, email, phone number, category, and an optional audio note.
3. __Reminders__
  * Set reminders for important events or tasks.
  * Store important notes for quick reference.
4. __Assistant__
  * Recognizes speech and fetches events from the calendar
  * Dials 911 if user asks for help
5. __Interactve Maps__
  * Allow users to save "Home," "Work," and "Friend's House" addresses for future reference.
  * Load and display saved addresses automatically on app startup.
  * Open Google Maps for navigation to any saved address with a single click.

## Setup Instructions ##
### Prerequisites ##
* Android Studio (latest version recommended).
* Android Emulator or a physical device with API level 30 or higher.
* Basic knowledge of Kotlin and Android development.

### Installation ###
1. Clone the repository:
   ```
   git clone https://github.com/Jumaana-bit/VirtualMemory.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle files to download dependencies.
4. Build and run the project on an emulator or connected device.

## Tech Stack ##
* __Programming Language:__ Java for Android development.
* __Database:__ SQLite for local storage of contacts and media data.
* __Google Maps API:__ For map integration and navigation functionality.
* __Android Framework:__ Used to build a user-friendly interface and manage app lifecycle.
* __SharedPreferences:__ For saving and retrieving location data asynchronously.   


