# This docker image is just an Android test environment used for Jenkins
# Codebase need to be mounted to docker container

FROM openjdk:8-jdk-stretch

MAINTAINER bfeng@thoughtworks.com
ENV REFRESHED_AT 2019_02_01

# Install wget lib32z1 lib32stdc++6 expect
RUN apt-get update -qq \
    && apt-get upgrade -qqy \
    && apt-get install -qqy wget lib32stdc++6 lib32z1 expect

# Set environment variables
ENV ANDROID_SDK_VERSION sdk-tools-linux-4333796
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/build-tools:$PATH

# Download Android SDK
WORKDIR /opt

RUN wget -q https://dl.google.com/android/repository/${ANDROID_SDK_VERSION}.zip -O android-sdk-linux.zip \
    && unzip -q android-sdk-linux.zip -d ${ANDROID_HOME} \
    && rm android-sdk-linux.zip \
    && chmod -R 775 ${ANDROID_HOME}

WORKDIR /opt/project

COPY ./repositories.cfg /root/.android/repositories.cfg

RUN yes | sdkmanager --licenses

#RUN sdkmanager --list

RUN sdkmanager "tools" "platform-tools"

RUN yes | sdkmanager \
    "build-tools;29.0.2" \
    "platforms;android-29" \
    "add-ons;addon-google_apis-google-23" \
    "extras;android;m2repository" \
    "extras;google;m2repository" \
    "extras;google;google_play_services" \
    "system-images;android-24;default;armeabi-v7a"
