#include <windows.h>
#include <stdio.h>

int main() {
    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    char cmd[512];

    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));

    // Get the current directory
    char currentDir[MAX_PATH];
    GetCurrentDirectory(MAX_PATH, currentDir);

    // Build the command
    sprintf(cmd, "cmd.exe /k java -jar \"%s\\ediconvertor-0.0.1-SNAPSHOT.jar\" --server.port=8081", currentDir);

    // Create the process
    if (!CreateProcess(NULL, cmd, NULL, NULL, FALSE, CREATE_NEW_CONSOLE, NULL, NULL, &si, &pi)) {
        printf("Failed to start EDI service. Error: %d\n", GetLastError());
        return 1;
    }

    // Close process and thread handles
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);

    return 0;
}
