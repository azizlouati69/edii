# Read the JAR file and convert it to base64
$jarPath = "target\ediconvertor-0.0.1-SNAPSHOT.jar"
$jarBytes = [System.IO.File]::ReadAllBytes($jarPath)
$jarBase64 = [Convert]::ToBase64String($jarBytes)

$code = @"
using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Net.NetworkInformation;

public class EDIService {
    // Embed the JAR content directly in the executable
    private static readonly string JarContent = @"$jarBase64";

    private static bool IsPortAvailable(int port) {
        IPGlobalProperties ipGlobalProperties = IPGlobalProperties.GetIPGlobalProperties();
        var tcpConnInfoArray = ipGlobalProperties.GetActiveTcpListeners();
        foreach (var endpoint in tcpConnInfoArray) {
            if (endpoint.Port == port) {
                return false;
            }
        }
        return true;
    }

    private static int FindAvailablePort(int startPort) {
        int port = startPort;
        while (!IsPortAvailable(port) && port < startPort + 10) {
            port++;
        }
        return port;
    }

    private static string ExtractJar() {
        string tempDir = Path.Combine(Path.GetTempPath(), "edi-service");
        Directory.CreateDirectory(tempDir);
        string jarPath = Path.Combine(tempDir, "service.jar");
        
        // Convert base64 back to bytes and save as JAR
        byte[] jarBytes = Convert.FromBase64String(JarContent);
        File.WriteAllBytes(jarPath, jarBytes);
        
        return jarPath;
    }

    public static void Main() {
        try {
            Console.WriteLine("Starting EDI Service...");
            
            // Create required directories
            Directory.CreateDirectory("C:\\txt");
            Directory.CreateDirectory("C:\\test");

            // Extract embedded JAR to temp directory
            string jarPath = ExtractJar();
            Console.WriteLine("Initialized service components...");
            
            int port = FindAvailablePort(8085);
            Console.WriteLine("Using port: " + port);
            
            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.FileName = "java";
            startInfo.Arguments = string.Format("-jar \"{0}\" --server.port={1}", jarPath, port);
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            startInfo.CreateNoWindow = true;
            
            Process process = new Process();
            process.StartInfo = startInfo;
            process.OutputDataReceived += (sender, e) => { 
                if (e.Data != null) Console.WriteLine(e.Data); 
            };
            process.ErrorDataReceived += (sender, e) => { 
                if (e.Data != null) Console.WriteLine(e.Data); 
            };
            
            process.Start();
            process.BeginOutputReadLine();
            process.BeginErrorReadLine();
            
            Console.WriteLine("\n=== EDI Service Configuration ===");
            Console.WriteLine("Service URL: http://localhost:" + port);
            Console.WriteLine("Test endpoint: http://localhost:" + port + "/api/file-conversion/test");
            Console.WriteLine("Input directory: C:\\txt");
            Console.WriteLine("Output directory: C:\\test");
            Console.WriteLine("===============================");
            Console.WriteLine("\nPress Ctrl+C to stop the service");
            
            while (!process.HasExited) {
                Thread.Sleep(1000);
            }
        }
        catch (Exception ex) {
            Console.WriteLine("Error starting EDI service: " + ex.Message);
            if (ex.Message.Contains("java")) {
                Console.WriteLine("\nMake sure Java 17 or later is installed and 'java' is in your system PATH");
                Console.WriteLine("You can download Java from: https://www.oracle.com/java/technologies/downloads/");
            }
            Console.WriteLine("\nPress any key to see technical details...");
            Console.ReadKey();
            Console.WriteLine("\nTechnical details:");
            Console.WriteLine(ex.ToString());
        }
        Console.WriteLine("\nPress any key to exit...");
        Console.ReadKey();
    }
}
"@

Add-Type -TypeDefinition $code -OutputAssembly "EDIService.exe" -OutputType ConsoleApplication
