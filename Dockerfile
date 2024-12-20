FROM openjdk:11-jdk

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    gradle \
    dos2unix

# Install Android SDK
ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_URL=https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

# Download and setup Android SDK
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools
WORKDIR ${ANDROID_HOME}/cmdline-tools
RUN curl -sSL ${ANDROID_SDK_URL} -o android_tools.zip && \
    unzip android_tools.zip && \
    mv cmdline-tools latest && \
    rm android_tools.zip

# Set PATH
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# Accept licenses
RUN yes | sdkmanager --licenses

# Install required Android packages
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app

# Set entrypoint to ensure proper shell execution
ENTRYPOINT ["/bin/sh", "-c"]
CMD ["./gradlew assembleDebug"]