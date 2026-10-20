#!/bin/bash
#
# Build the installable debug APK.
# Gradle 7.5 runs on JDK 17. Android Studio's bundled JBR is JDK 21 and cannot start this build.
#

set -euo pipefail

cd "$(dirname "$0")"

java_major() {
    "$1" -version 2>&1 | awk -F '"' '/version/ {
        split($2, v, ".")
        if (v[1] == "1") {
            print v[2]
        } else {
            print v[1]
        }
        exit
    }'
}

java_is_usable() {
    local home="$1"
    local major
    if [[ ! -x "$home/bin/java" ]]; then
        return 1
    fi
    major="$(java_major "$home/bin/java")"
    # Gradle 7.5 supports JDK 8 through 18. 17 is what this project is built with.
    [[ "$major" -ge 11 && "$major" -le 18 ]]
}

if [[ -n "${JAVA_HOME:-}" ]] && java_is_usable "$JAVA_HOME"; then
    :
else
    JAVA_HOME=""
    candidates=(
        "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
        "/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
        "/usr/local/opt/openjdk@17"
        "/opt/homebrew/opt/openjdk@17"
    )
    if /usr/libexec/java_home -v 17 >/dev/null 2>&1; then
        candidates+=("$(/usr/libexec/java_home -v 17)")
    fi
    for candidate in "${candidates[@]}"; do
        if java_is_usable "$candidate"; then
            JAVA_HOME="$candidate"
            break
        fi
    done
fi

if [[ -z "$JAVA_HOME" ]]; then
    echo "需要 JDK 17。Gradle 7.5 不能使用 Android Studio 自带的 JDK 21。" >&2
    echo "可安装：brew install openjdk@17" >&2
    exit 1
fi

export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using $(java -version 2>&1 | head -n 1)"
echo "JAVA_HOME=$JAVA_HOME"

./gradlew :app:assembleDebug "$@"

apk="app/build/outputs/apk/debug/app-debug.apk"
echo "APK: $(pwd)/$apk"
