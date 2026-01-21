#!/usr/bin/env bash
# ----------------------------
# Cross-Platform Spring Boot HTTPS Keystore Generator
# ----------------------------

set -e

KEYSTORE_PATH="src/main/resources/keystore.p12"
KEYSTORE_PASSWORD="changeit"
KEY_ALIAS="springboot-local"
KEY_ALGORITHM="RSA"
KEY_SIZE=2048
VALIDITY_DAYS=3650
DNAME="CN=localhost, OU=Dev, O=MyCompany, L=City, S=State, C=IN"

# Create resources folder if it doesn't exist
RESOURCES_DIR=$(dirname "$KEYSTORE_PATH")
mkdir -p "$RESOURCES_DIR"

# Check if keystore already exists
if [ -f "$KEYSTORE_PATH" ]; then
  echo "✅ Keystore already exists at $KEYSTORE_PATH"
  exit 0
fi

# Find keytool
if [ -n "$JAVA_HOME" ]; then
  KEYTOOL="$JAVA_HOME/bin/keytool"
else
  if command -v keytool >/dev/null 2>&1; then
    KEYTOOL=$(command -v keytool)
  else
    echo "❌ keytool not found. Please set JAVA_HOME or install JDK."
    exit 1
  fi
fi

# Generate keystore
echo "🔑 Generating keystore at $KEYSTORE_PATH ..."
"$KEYTOOL" -genkeypair \
  -alias "$KEY_ALIAS" \
  -keyalg "$KEY_ALGORITHM" \
  -keysize "$KEY_SIZE" \
  -storetype PKCS12 \
  -keystore "$KEYSTORE_PATH" \
  -storepass "$KEYSTORE_PASSWORD" \
  -validity "$VALIDITY_DAYS" \
  -dname "$DNAME"

if [ -f "$KEYSTORE_PATH" ]; then
  echo "✅ Keystore generated successfully!"
else
  echo "❌ Failed to generate keystore."
  exit 1
fi
