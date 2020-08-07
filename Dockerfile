# This docker image is just an Android test environment used for Jenkins
# Codebase need to be mounted to docker container

FROM openjdk:11-jdk-slim

MAINTAINER bfeng@thoughtworks.com
ENV REFRESHED_AT 2019_02_01

# Install wget lib32z1 lib32stdc++6 expect
RUN apt-get update -qq \
    && apt-get upgrade -qqy \
    && apt-get install -qqy wget lib32stdc++6 lib32z1 expect unzip

# Set environment variables
ENV ANDROID_SDK_VERSION sdk-tools-linux-4333796
ENV ANDROID_HOME /opt/android-sdk-linux
ENV PATH ${ANDROID_HOME}/cmdline-tools/tools/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/build-tools:$PATH

WORKDIR /opt

# Download Command line Tools
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-6609375_latest.zip -O android-commandline-tools.zip \
    && mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && unzip -q android-commandline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && rm android-commandline-tools.zip

WORKDIR /opt/project

COPY ./repositories.cfg /root/.android/repositories.cfg

RUN yes | sdkmanager --licenses

RUN sdkmanager "tools" "platform-tools"

RUN yes | sdkmanager \
    "ndk;21.0.6113669" \
    "build-tools;29.0.2" \
    "platforms;android-29" \
    "add-ons;addon-google_apis-google-23" \
    "extras;android;m2repository" \
    "extras;google;m2repository" \
    "extras;google;google_play_services" \
    "system-images;android-29;google_apis;x86"