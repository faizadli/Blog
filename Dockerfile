FROM openjdk:11-jdk

# Install Android SDK
ENV ANDROID_HOME /opt/android-sdk
ENV ANDROID_SDK_URL https://dl.google.com/android/repository/commandlinetools-linux-latest.zip

# Install required packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    gradle

# Download and setup Android SDK
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    cd ${ANDROID_HOME}/cmdline-tools && \
    wget -q ${ANDROID_SDK_URL} -O android_tools.zip && \
    unzip android_tools.zip && \
    mv cmdline-tools latest && \
    rm android_tools.zip

# Set PATH
ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# Accept licenses
RUN yes | sdkmanager --licenses

# Install required Android packages
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
COPY . .

# Build command
CMD ["./gradlew", "assembleDebug"]