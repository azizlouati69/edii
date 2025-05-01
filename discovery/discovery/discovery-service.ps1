$code = @"
using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Net.NetworkInformation;

public class DiscoveryService {
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

    public static void Main() {
        try {
            Console.WriteLine("Starting Discovery Service...");
            string jarPath = @".\discovery-0.0.1-SNAPSHOT.jar";
            
            if (!File.Exists(jarPath)) {
                throw new FileNotFoundException("JAR file not found at: " + jarPath);
            }

            Console.WriteLine("Found JAR file: " + jarPath);
            
            int port = FindAvailablePort(8761);  // Default Eureka port
            Console.WriteLine("Using port: " + port);
            Console.WriteLine("Starting Java process...");
            
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
            
            Console.WriteLine("Discovery Service is starting on port " + port + "...");
            Console.WriteLine("Press Ctrl+C to stop the service");
            Console.WriteLine("Eureka Dashboard: http://localhost:" + port);
            
            while (!process.HasExited) {
                Thread.Sleep(1000);
            }
        }
        catch (Exception ex) {
            Console.WriteLine("Error starting Discovery service: " + ex.Message);
            Console.WriteLine("Press any key to see details...");
            Console.ReadKey();
            Console.WriteLine("\nFull error details:");
            Console.WriteLine(ex.ToString());
        }
        Console.WriteLine("\nPress any key to exit...");
        Console.ReadKey();
    }
}
"@

Add-Type -TypeDefinition $code -OutputAssembly "discovery-service.exe" -OutputType ConsoleApplication
