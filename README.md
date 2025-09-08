# Findro Real Estate App

Welcome to Findro Real Estate App! This Android application is designed to help users find and list properties with ease. Whether you're looking for a flat, hostel, PG, commercial space, or a room, our app provides a user-friendly interface to simplify your search and listing process.

## Features

### Splash Screen
- Engaging splash screen to welcome users.

### Authentication
- **Login**: Users can log in using Firebase Email Authentication.
- **Sign Up**: New users can sign up using their email and mobile number with Firebase Phone Authentication and Email Authentication.

### Dashboard
- **Categories**: Easily browse through various categories such as "Flat", "Hostel", "PG", "Commercial", and "Room".
- **Search**: Use the search button to find properties by location, area, or property type.
- **Filters**: Refine your search with filters for location, property type, and price range.

### Profile
- **Post Property**: Users can post their property for rent, select multiple images, set Google Map location, create a gallery, and specify the size of the property.
- **Edit Profile**: Update personal information and manage your profile.
- **Uploaded Properties**: View and edit your uploaded properties.

### Property Details
- **Gallery**: View all images of the property.
- **Similar Properties**: Find properties similar to the one you're viewing.
- **Map Location**: See the property's location on Google Maps.
- **Contact Owner**: Contact property owners via call, WhatsApp, or Gmail.

### Notifications
- **Broadcast Notifications**: Receive important updates using One Signal.
- **Device-to-Device Notifications**: Get notifications via Firebase Cloud Messaging.

## Technologies Used
- **Programming Language**: Java
- **Libraries and Frameworks**: Retrofit
- **Backend Services**: Firebase Realtime Database, Firebase Cloud Messaging, Firebase Crashlytics, Firebase Authentication, One Signal
- **Database**: Firebase Realtime Database

## Screenshots

![1st (1)](https://github.com/bypriyan/Findro-Real-Estate-App/assets/86232180/31c76cec-15f3-4bf1-af1a-6c29dfd508b1)
![2nd](https://github.com/bypriyan/Findro-Real-Estate-App/assets/86232180/ae4eb4c9-5fc8-4227-b8cf-c2af98e56eb7)
![2rd](https://github.com/bypriyan/Findro-Real-Estate-App/assets/86232180/7d6dbbd4-16fe-4df8-a1fe-2ee6ec65b62c)
![4th](https://github.com/bypriyan/Findro-Real-Estate-App/assets/86232180/9af8e24d-ba6c-44c4-b5c1-f4fbf4439b22)
![5th](https://github.com/bypriyan/Findro-Real-Estate-App/assets/86232180/85a36853-4ff4-481f-859f-6876b7c077eb)
![6th](https://github.com/bypriyan/Findro-Real-Estate-App/assets/86232180/2e2212ec-631c-45da-bfe4-50d486a6d532)

## Getting Started
To get started with the app, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/bypriyan/Findro-Real-Estate-App.git
    ```

2. Open the project in Android Studio.

3. Set up Firebase for your Android app:
   - Go to the [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one
   - Add your Android app to the project
   - Follow the instructions to download the `google-services.json` file and place it in the `app` directory
   - Enable Firebase Authentication (Email/Password and Phone)

4. Set up your PHP backend and MySQL database:
   - Configure your PHP server to handle API requests.
   - Create a MySQL database and import the necessary tables.

5. Update the backend URLs in your Retrofit instance.

6. Build and run the app on your Android device or emulator.

## Contributing

Contributions are welcome! Please fork this repository and submit pull requests for any enhancements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

If you have any questions or suggestions, feel free to reach out to me at [104priyanshu@gmail.com].

---

Thank you for checking out Findro! We hope you enjoy using the app as much as we enjoyed building it.

