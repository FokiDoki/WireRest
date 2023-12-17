#!/bin/bash

# Step 0: Set variables
WIREREST_ARCHIVE_URL="https://github.com/FokiDoki/WireRest/releases/download/0.7-BETTA/wirerest-0.7-archive.tar"
JAVA_TARGET="21"
JAVA_DEFAULT_FOLDER="/usr/lib/jvm"
DEFAULT_PORT=8081
HEALTHCHECK_MAX_RETRIES=30
HEALTHCHECK_RETRY_INTERVAL_SEC=2

# Set color variables
RED='\e[31m'
GREEN='\e[32m'
YELLOW='\e[33m'
GRAY='\e[37m'
NC='\e[0m' # No Color


# Step 1: Check if the user is root
if [ "$(id -u)" -ne 0 ]; then
    echo -e "${RED}Error: Please run the script as root.${NC}"
    exit 1
fi

# Step 2: Welcome message
echo -e "${GREEN}Installing WireRest${NC}"

# Function to check package installation
check_package() {
    local PACKAGE="$1"
    echo -e -n "Checking that $PACKAGE is installed...	"
    if command -v "$PACKAGE" &> /dev/null; then
        echo -e "${GREEN}OK${NC}"
    else
        echo -e "${RED}FAILED${NC}"
        echo -e "$PACKAGE not found, please install $PACKAGE package"
        exit 1
    fi
}

function ask_yes() {
    while true; do
        read -p "$1 (Y/N): " response
        case $response in
            [Yy]* ) return 0;;
            [Nn]* ) return 1;;
            * ) echo "Please, enter Y(es) or N(no)";;
        esac
    done
}

# Step 3: Check for WireGuard package
check_package "/usr/bin/wg"

# Step 4: Check for WireGuard-tools package
check_package "/usr/bin/wg-quick"

# Step 4.1 Check java

find_java_by_version() {
    local dir="$1"
    local target_version="$2"

    for file in "$dir"/*; do
        if [ -x "$file/bin/java" ]; then
            local java_version=$("$file/bin/java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
            if [[ "$java_version" == "$target_version"* ]]; then
                echo -e "Java $target_version ${GREEN}found${NC} in $file"
                JAVA_DIRECTORY="$file"
                break
            fi
        fi
    done
}

is_java_binary() {
    local file="$1"

    # Check if the file exists
    if [ -e "$file" ]; then
        # Check if the file is executable
        if [ -x "$file" ]; then
            # Check if the file is a Java binary using java --version
            if "$file" --version &> /dev/null; then
                return 0 # The file is the Java binary
            else
                return 1 # The file is not the Java binary
            fi
        else
            return 1 # The file is not executable
        fi
    else
        return 1 # The file does not exist
    fi
}

# Function to display a numbered list of directories
display_numbered_list() {
    local list=("$@")
    echo -e "Choose a directory with Java ${JAVA_TARGET} by entering its number:"
    echo -e "0. There is no Java ${JAVA_TARGET}, install it"
    for i in "${!list[@]}"; do
        echo -e "$((i+1)). ${list[i]}"
    done
}

install_java_21() {
    # Update the package manager
    sudo apt-get update

    # Download JDK
    echo -e "Downloading JDK.."
    wget -q https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb

    # Install JDK with error handling
    if sudo dpkg -i jdk-21_linux-x64_bin.deb; then
        echo -e "JDK installation ${GREEN}successful${NC}"
    else
        # If an error occurs during JDK installation, fix broken dependencies
        echo -e "Error installing JDK. Fixing broken dependencies..."
        sudo apt --fix-broken install

        # Retry JDK installation
        echo -e "Retrying JDK installation..."
        sudo dpkg -i jdk-21_linux-x64_bin.deb

        # Check the success of the installation
        if [ $? -eq 0 ]; then
            echo -e "JDK installation ${GREEN}successful${NC} after fixing broken dependencies"
        else
            echo -e "${RED}Failed${NC} to install JDK even after fixing broken dependencies. Please check the error messages"
            exit 1
        fi
    fi
    rm jdk-21_linux-x64_bin.deb
}

# Function to prompt user for the desired directory
prompt_user_for_choice() {
    local list=("$@")
    read -p "Enter the number of the desired directory: " user_choice

    if [[ "$user_choice" == 0 ]]; then
        install_java_21
        check_directory_for_java_version "${JAVA_DEFAULT_FOLDER}" "$JAVA_TARGET"
    elif [[ "$user_choice" =~ ^[1-9][0-9]*$ && "$user_choice" -le "${#list[@]}" ]]; then
        JAVA_DIRECTORY="${list[user_choice-1]}"
    else
        echo -e "${RED}Invalid choice.${NC} Exiting"
        exit 1
    fi
}

# Check if a directory exists and contains Java with a specified version
check_directory_for_java_version() {
    local dir="$1"
    local target_version="$2"

    if [ -d "$dir" ]; then
        find_java_by_version "$dir" "$target_version"

        if [ -z "$JAVA_DIRECTORY" ]; then
            local sub_directories=("$dir"/*)
            if [ ${#sub_directories[@]} -eq 0 ]; then
                echo -e "${RED}No directories found${NC} in $dir. Exiting"
                exit 1
            fi

            display_numbered_list "${sub_directories[@]}"
            prompt_user_for_choice "${sub_directories[@]}"
        fi
    else
        echo -e "${YELLOW}Directory $dir does not exist.${NC} Looks like Java not found"
        # Check user response
        if ask_yes "Do you want to install Java 21?"; then
            # Call the install_java_21 function
            install_java_21
            check_directory_for_java_version "${JAVA_DEFAULT_FOLDER}" "$JAVA_TARGET"
        else
            echo -e "Can't install WireRest without java. Exiting"
            exit 0
        fi
    fi
}

# Check /usr/lib/jvm/ for Java with the specified version
check_directory_for_java_version "${JAVA_DEFAULT_FOLDER}" "$JAVA_TARGET"

# If Java not found, prompt user for a directory
if [ -z "$JAVA_DIRECTORY" ]; then
    read -p "Enter the path to the Java home directory: " JAVA_DIRECTORY
    if is_java_binary "$JAVA_DIRECTORY/bin/java"; then
        echo -e "Java binary ${GREEN}found${NC}!"
    else
        echo -e "${RED}Java binary not found${NC} in $JAVA_DIRECTORY"
        exit 1
    fi
fi

echo -e "Selected directory: $JAVA_DIRECTORY"

# Step 5: Choose WireGuard interface
interfaces=$(wg | grep "interface" | awk '{print $2}')
# Check if there are active interfaces
if [ -z "$interfaces" ]; then
    echo -e "${RED}Error: No active interfaces found.${NC} Please activate at least one interface (config)"
    exit 1
elif [ $(wc -w <<< "$interfaces") -eq 1 ]; then
    # If only one interface is available, choose it automatically
    INTERFACE="$interfaces"
    echo -e "Only one interface found: ${GREEN}$INTERFACE${NC}. Selected ${GREEN}automatically${NC}"
else
    # If there are multiple interfaces, let the user choose
    PS3="Select an interface: "
    select interface in $interfaces; do
        if [ -n "$interface" ]; then
            echo -e "Selected interface: ${GREEN}$interface${NC}"
            INTERFACE="$interface"
            break
        else
            echo -e "${RED}Please choose a valid interface number.${NC}"
        fi
    done
fi

cleanup() {
    rm -rf "${TMP_DIR}"
}


if ask_yes "All checks passed. Start installing?"; then
    echo -e "Starting the download..."
else
    echo -e "Installing canceled. Exiting"
    exit 0
fi

# Step 6: Download WireRest files
echo -e "Downloading WireRest files..."
wget -q -O wirerest.tar "$WIREREST_ARCHIVE_URL"

TMP_DIR="/tmp/wirerest-install"
# Step 7: Extract the archive
mkdir "${TMP_DIR}"
trap cleanup EXIT
trap cleanup INT
tar -xf wirerest.tar -C "${TMP_DIR}"
cd "${TMP_DIR}" || exit 1
# Step 8: Copy .jar file to /usr/local/bin
cp wirerest-*.jar /usr/local/bin/wirerest.jar

# Step 9: Generate and display security token
WIREREST_TOKEN=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 64)
echo -e "Your WireRest ${GREEN}access token${NC}: $WIREREST_TOKEN"
echo -e "This token provides full access to your VPN. ${YELLOW}Share carefully.${NC}"

# Step 10: Ask for API port
read -e -p "Enter the API port (default is $DEFAULT_PORT): " -i "$DEFAULT_PORT" user_input

# Validate and update the API port
while ! [[ "$user_input" =~ ^[0-9]+$ && "$user_input" -ge 1 && "$user_input" -le 65535 ]]; do
    echo -e "${RED}Invalid port.${NC} Please enter a valid port number"
    read -p "Enter the API port: " -i "$DEFAULT_PORT" user_input
done
WIREREST_PORT=$user_input

# Step 11: Create /etc/default/wirerest-INTERFACE file
cp enviroment "/etc/default/wirerest-$INTERFACE"
sed -i "s/^ACCESS_TOKEN=.*/ACCESS_TOKEN=${WIREREST_TOKEN}/" "/etc/default/wirerest-$INTERFACE"
sed -i "s/^PORT=.*/PORT=${WIREREST_PORT}/" "/etc/default/wirerest-$INTERFACE"

# Step 12: Replace JAVA_HOME_DIR in wirerest.service
sed -i "s|JAVA_HOME_DIR|$JAVA_DIRECTORY|" wirerest.service

# Step 13: Copy wirerest.service to /lib/systemd/system
cp wirerest.service /lib/systemd/system/wirerest@.service

# Step 13.1 CleanUp
rm -rf "${TMP_DIR}"
# Step 14: Reload systemd
systemctl daemon-reload

# Step 15: Enable and start WireRest service for the selected interface
SERVICE_NAME="wirerest@$INTERFACE"

# Check if the service is running
if systemctl is-active --quiet "$SERVICE_NAME"; then
    # Service is running

    if ask_yes "The service $SERVICE_NAME is currently running. Are you sure you want to recreate it?";  then
        # Stop the service
        systemctl stop "$SERVICE_NAME"

        # Remove from autostart
        systemctl disable "$SERVICE_NAME"

        # Recreate the service (replace this with your actual command)
        systemctl start "$SERVICE_NAME"

        # Add back to autostart
        systemctl enable "$SERVICE_NAME"

        echo -e "Service $SERVICE_NAME recreated and added back to autostart ${GREEN}successfully${NC}"
    else
        echo "Recreation canceled. Exiting"
        exit 0
    fi
else
    # Service is not running
   systemctl enable --now "$SERVICE_NAME"
fi
# Step 16: Checking service running
is_service_running() {
    local response_code
    response_code=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:$WIREREST_PORT/webjars/swagger-ui/index.html)
    if [ "$response_code" -eq 200 ]; then
        return 0
    else
        return 1
    fi
}

# Function to wait for the service to start
wait_for_service() {
    retries=0
    until is_service_running || [ $retries -eq $HEALTHCHECK_MAX_RETRIES ]; do
        sleep $HEALTHCHECK_RETRY_INTERVAL_SEC
        retries=$((retries + 1))
        echo -n "."
    done
    echo

    if is_service_running; then
        external_ip=$(dig @resolver4.opendns.com myip.opendns.com +short -4)
        echo -e "${GREEN}Success!${NC} Service $SERVICE_NAME is running. Now you can visit http://${external_ip}:${WIREREST_PORT}/swagger-ui and see available methods"
    else
        echo -e "${RED}Error: Service $SERVICE_NAME did not start within the expected time.${NC}"
        exit 1
    fi
}
echo -n "Waiting for the service to start."
wait_for_service
