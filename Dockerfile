FROM openjdk:11-jdk

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    gradle \
    bash \
    dos2unix

# Install Android SDK
ENV ANDROID_HOME=/opt/android-sdk \
    ANDROID_SDK_URL=https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

# Download and setup Android SDK
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools
WORKDIR ${ANDROID_HOME}/cmdline-tools
RUN curl -sSL ${ANDROID_SDK_URL} -o android_tools.zip
RUN unzip android_tools.zip
RUN mv cmdline-tools latest
RUN rm android_tools.zip
RUN echo "Android SDK Command-line Tools installed successfully"

# Set PATH
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# Accept licenses
RUN yes | sdkmanager --licenses

# Install required Android packages
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
COPY . .

# Ensure gradlew has correct permissions and line endings
RUN dos2unix gradlew && chmod +x gradlew

# Build command
CMD ["./gradlew", "assembleDebug"]