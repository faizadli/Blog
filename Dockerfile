FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y curl unzip gradle dos2unix

ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_URL=https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    cd ${ANDROID_HOME}/cmdline-tools && \
    curl -sSL ${ANDROID_SDK_URL} -o android_tools.zip && \
    unzip android_tools.zip && \
    mv cmdline-tools latest && \
    rm android_tools.zip

ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app

# Fix permissions as root
RUN mkdir -p /root/.gradle && chmod -R 777 /root/.gradle
RUN mkdir -p .gradle && chmod -R 777 .gradle

# Allow write access to workdir
RUN chmod -R 777 /app

# Stay as root user
USER root

CMD ["./gradlew", "assembleDebug"]