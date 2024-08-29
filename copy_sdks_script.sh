#!/bin/bash

# Replace this with the path to the source folder
iOS_SOURCE_FOLDER="./iOS-sdk/SwiftSDK-sources"
android_SOURCE_FOLDER="./outbrain-android-sdk/outbrain-sdk/obsdk"

# Replace this with the path to the destination parent directory
iOS_DESTINATION_PARENT="./ios/Runner"
android_DESTINATION_PARENT="./android"

# Get the folder name
iOS_FOLDER_NAME=$(basename "$iOS_SOURCE_FOLDER")
android_FOLDER_NAME=$(basename "$android_SOURCE_FOLDER")

# Build the full path to the destination folder
iOS_DESTINATION_FOLDER="$iOS_DESTINATION_PARENT/$iOS_FOLDER_NAME"
android_DESTINATION_FOLDER="$android_DESTINATION_PARENT/$android_FOLDER_NAME"

# Remove the destination folder if it exists
if [ -d "$iOS_DESTINATION_FOLDER" ]; then
    rm -rf "$iOS_DESTINATION_FOLDER"
fi
if [ -d "$android_DESTINATION_FOLDER" ]; then
    rm -rf "$android_DESTINATION_FOLDER"
fi

# Move the source folder to the destination
cp -R "$iOS_SOURCE_FOLDER" "$iOS_DESTINATION_PARENT"
cp -R "$android_SOURCE_FOLDER" "$android_DESTINATION_PARENT"

echo "Folder '$iOS_FOLDER_NAME' has been replaced."
echo "Folder '$android_FOLDER_NAME' has been replaced."
