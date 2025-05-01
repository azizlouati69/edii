# Read the JAR file as bytes
$jarBytes = [System.IO.File]::ReadAllBytes("target\ediconvertor-0.0.1-SNAPSHOT.jar")
$jarBase64 = [Convert]::ToBase64String($jarBytes)

$code = @"
using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Net.NetworkInformation;

public class EDIService {
    private static readonly string JAR_CONTENT = `"$jarBase64`";

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
        string tempPath = Path.Combine(Path.GetTempPath(), "ediconvertor-temp");
        Directory.CreateDirectory(tempPath);
        string jarPath = Path.Combine(tempPath, "ediconvertor.jar");
        
        if (!File.Exists(jarPath)) {
            byte[] jarBytes = Convert.FromBase64String(JAR_CONTENT);
            File.WriteAllBytes(jarPath, jarBytes);
        }
        
        return jarPath;
    }

    public static void Main() {
        try {
            Console.WriteLine("Starting EDI Converter Service...");
            
            // Create required directories
            Directory.CreateDirectory("C:\\txt");
            Directory.CreateDirectory("C:\\xml");

            // Extract embedded JAR
            string jarPath = ExtractJar();
            Console.WriteLine("Initialized service components...");
            
            int port = FindAvailablePort(8085);
            Console.WriteLine("Using port: " + port);
            Console.WriteLine("Starting service...");
            
            ProcessStartInfo startInfo = new ProcessStartInfo();
            startInfo.FileName = "java";
            startInfo.Arguments = string.Format("-jar \"{0}\" --server.port={1}", jarPath, port);
            startInfo.UseShellExecute = false;
            startInfo.RedirectStandardOutput = true;
            startInfo.RedirectStandardError = true;
            startInfo.CreateNoWindow = true;
            startInfo.WorkingDirectory = Path.GetDirectoryName(jarPath);
            
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
            
            Console.WriteLine("\n=== EDI Converter Service Configuration ===");
            Console.WriteLine("Service URL: http://localhost:" + port);
            Console.WriteLine("Test endpoint: http://localhost:" + port + "/api/file-conversion/test");
            Console.WriteLine("Input directory: C:\\txt");
            Console.WriteLine("Output directory: C:\\xml");
            Console.WriteLine("=========================================");
            Console.WriteLine("\nPress Ctrl+C to stop the service");
            
            while (!process.HasExited) {
                Thread.Sleep(1000);
            }
        }
        catch (Exception ex) {
            Console.WriteLine("Error starting EDI Converter service: " + ex.Message);
            if (ex.Message.Contains("java")) {
                Console.WriteLine("\nMake sure Java 17 or later is installed and 'java' is in your system PATH.");
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

Add-Type -TypeDefinition $code -OutputAssembly "EDIConverterService.exe" -OutputType ConsoleApplication
